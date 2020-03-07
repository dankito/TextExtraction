package net.dankito.text.extraction.app.javafx.window.main.controls

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.ExtractionResultForExtractor
import net.dankito.utils.IThreadPool
import net.dankito.utils.javafx.ui.extensions.ensureOnlyUsesSpaceIfVisible
import net.dankito.utils.javafx.ui.extensions.setBackgroundToColor
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.File
import java.util.*


abstract class ExtractTextTabBase(protected val threadPool: IThreadPool) : View() {

    companion object {
        private val logger = LoggerFactory.getLogger(ExtractTextTabBase::class.java)
    }


    protected abstract fun createTextExtractor(): ITextExtractor


    protected val lastSelectedFilePath = SimpleStringProperty("")

    protected val extractionTime = SimpleStringProperty("")

    protected val isExistingFileSelected = SimpleBooleanProperty(false)

    protected val isExtractingText = SimpleBooleanProperty(false)

    protected val didTextExtractionReturnAnError = SimpleBooleanProperty(false)

    protected val textExtractionErrorMessage = SimpleStringProperty("")

    protected val showTextExtractorInfo = SimpleBooleanProperty(false)

    protected val textExtractionInfo = SimpleStringProperty("")

    protected val extractedText = SimpleStringProperty("")


    protected var textExtractorField: ITextExtractor? = null

    protected var lastSelectedFile: File? = null


    init {
        lastSelectedFilePath.addListener { _, _, newValue -> checkIfFileExists(newValue) }
    }


    override val root = vbox {
        useMaxHeight = true

        hbox {
            prefHeight = 34.0
            alignment = Pos.CENTER_LEFT

            label(messages["extract.text.tab.file.label"])

            textfield(lastSelectedFilePath) {
                useMaxHeight = true

                setOnKeyReleased { keyReleased(it) }

                hboxConstraints {
                    hGrow = Priority.ALWAYS

                    marginLeftRight(6.0)
                }
            }

            button(messages["open.file.button.label"]) {
                useMaxHeight = true
                prefWidth = 45.0

                action { selectFile() }
            }

            label(extractionTime) {
                hboxConstraints {
                    marginLeft = 12.0
                    marginRight = 6.0
                }
            }

            progressindicator {
                maxHeight = this@hbox.prefHeight

                visibleWhen(isExtractingText)

                ensureOnlyUsesSpaceIfVisible()

                hboxConstraints {
                    marginRight = 6.0
                }
            }

            button(messages["extract.text.tab.extract.button.label"]) {
                useMaxHeight = true
                prefWidth = 125.0

                enableWhen(isExistingFileSelected.and(isExtractingText.not()))

                action { extractTextOfFileAndShowResult() }
            }
        }

        label(textExtractionInfo) {
            useMaxWidth = true

            paddingTop = 8.0
            paddingBottom = 8.0

            visibleWhen(showTextExtractorInfo)
            ensureOnlyUsesSpaceIfVisible()
        }

        label(textExtractionErrorMessage) {
            useMaxWidth = true

            paddingTop = 8.0
            paddingBottom = 8.0

            setBackgroundToColor(Color.RED)
            textFill = Color.WHITE

            visibleWhen(didTextExtractionReturnAnError)
            ensureOnlyUsesSpaceIfVisible()
        }

        textarea(extractedText) {
            isWrapText = true

            useMaxHeight = true
            useMaxWidth = true

            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
        }
    }


    protected open fun checkIfFileExists(enteredFilePath: String) {
        try {
            val file = File(enteredFilePath)

            if (file.exists()) {
                existingFileSelected(file)
            } else {
                isExistingFileSelected.value = false
            }
        } catch (e: Exception) {
            logger.warn("Could not convert '$enteredFilePath' to a File object", e)
        }
    }

    private fun keyReleased(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            if (isExistingFileSelected.value) {
                lastSelectedFile?.let { selectedFile(it) }
            }
        }
    }

    protected open fun selectFile() {
        val fileChooser = FileChooser()

        lastSelectedFile?.let {
            fileChooser.initialDirectory = lastSelectedFile?.parentFile
        }

        fileChooser.showOpenDialog(FX.primaryStage)?.let { selectedFile ->
            selectedFile(selectedFile)
        }
    }

    protected open fun selectedFile(file: File) {
        lastSelectedFilePath.value = file.absolutePath

        existingFileSelected(file)

        extractTextOfFileAndShowResult()
    }

    protected open fun existingFileSelected(file: File) {
        lastSelectedFile = file

        isExistingFileSelected.value = true
    }


    protected open fun extractTextOfFileAndShowResult() {
        lastSelectedFile?.let { file ->
            isExtractingText.value = true
            didTextExtractionReturnAnError.value = false
            showTextExtractorInfo.value = false
            val startTime = Date().time

            extractTextOfFileAsync(file) { extractedText ->
                val durationMillis = Date().time - startTime

                runLater {
                    showExtractedTextOnUiThread(file, extractedText, durationMillis)
                }
            }
        }
    }

    protected open fun showExtractedTextOnUiThread(fileToExtract: File, extractionResult: ExtractionResult, durationMillis: Long) {
        isExtractingText.value = false
        extractionTime.value = String.format(
            "%02d:%02d.%03d min", durationMillis / (60 * 1000),
            (durationMillis / 1000) % 60, durationMillis % 1000
        )

        extractedText.value = extractionResult.text

        if (extractionResult is ExtractionResultForExtractor) {
            showTextExtractorInfo.value = true
            textExtractionInfo.value = String.format(messages["info.text.extracted.with"], extractionResult.extractor?.javaClass?.simpleName)
        }

        didTextExtractionReturnAnError.value = extractionResult.errorOccurred
        extractionResult.error?.let { error ->
            textExtractionErrorMessage.value = getErrorMessage(fileToExtract, error)
        }
    }

    protected open fun getErrorMessage(fileToExtract: File, error: ErrorInfo): String {
        val extractorName = getExtractorName()
        val fileType = fileToExtract.extension

        return when (error.type) {
            ErrorType.FileTypeNotSupportedByExtractor -> String.format(messages["error.message.extractor.does.not.support.extracting.file.type"], extractorName, fileType)
            ErrorType.ExtractorNotAvailable -> String.format(messages["error.message.extractor.not.available"], extractorName)
            ErrorType.ParseError -> String.format(messages["error.message.could.not.parse.file"], error.exception?.localizedMessage)
            ErrorType.NoExtractorFoundForFileType -> String.format(messages["error.message.no.extractor.found.for.type"], fileType)
        }
    }

    protected open fun getExtractorName(): String {
        // kind a hack to get Tab name
        val tabPaneSkin = this.root.parent
        val tabPane = tabPaneSkin?.parent as? TabPane

        tabPane?.tabs?.firstOrNull { it.content == this.root }?.text?.let { tabName ->
            return tabName
        }

        return getTextExtractor().javaClass.simpleName
    }


    protected open fun extractTextOfFileAsync(file: File, callback: (ExtractionResult) -> Unit) {
        threadPool.runAsync {
            callback(extractTextOfFile(file))
        }
    }

    protected open fun extractTextOfFile(file: File): ExtractionResult {
        try {
            val textExtractor = getTextExtractor()

            val startTime = Date()
            val extractedText = textExtractor.extractText(file)
            val timeElapsed = Date().time - startTime.time

            logger.info("Extracting text of file $file took ${timeElapsed / 1000} seconds and ${timeElapsed % 1000} milliseconds")

            return extractedText
        } catch (e: Exception) {
            logger.error("Could not extract text of file $file", e)

            return ExtractionResult(ErrorInfo(ErrorType.ParseError, e)) // TODO: add suitable ErrorType
        }
    }

    protected open fun getTextExtractor(): ITextExtractor {
        textExtractorField?.let {
            return it
        }

        val newTextExtractor = createTextExtractor()

        this.textExtractorField = newTextExtractor

        return newTextExtractor
    }

}