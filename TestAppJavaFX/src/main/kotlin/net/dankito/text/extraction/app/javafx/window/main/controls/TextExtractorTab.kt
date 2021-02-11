package net.dankito.text.extraction.app.javafx.window.main.controls

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import net.dankito.text.extraction.ExternalToolTextExtractorBase
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.ExtractionResultForExtractor
import net.dankito.utils.Stopwatch
import net.dankito.utils.javafx.ui.controls.ProcessingIndicatorButton
import net.dankito.utils.javafx.ui.controls.processingIndicatorButton
import net.dankito.utils.javafx.ui.extensions.ensureOnlyUsesSpaceIfVisible
import net.dankito.utils.javafx.ui.extensions.fixedHeight
import net.dankito.utils.javafx.ui.extensions.setBackgroundToColor
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.File
import kotlin.coroutines.CoroutineContext


open class TextExtractorTab(val textExtractor: ITextExtractor) : View(), CoroutineScope {

    companion object {
        const val TextFieldInputPaneHeight = 34.0

        private val logger = LoggerFactory.getLogger(TextExtractorTab::class.java)
    }


    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.JavaFx


    protected val showInstallHint = SimpleBooleanProperty(textExtractor.isAvailable == false && textExtractor.installHint.isNotBlank())

    protected val installHintMessage = SimpleStringProperty(textExtractor.installHint)

    protected val userMustSelectProgramExecutablePath = SimpleBooleanProperty(false)

    protected val programExecutablePath = SimpleStringProperty("")

    protected val lastSelectedFilePath = SimpleStringProperty("")

    protected val showCustomTextExtractorConfigurationPane = SimpleBooleanProperty(false)

    protected val extractionTime = SimpleStringProperty("")

    protected val isExistingFileSelected = SimpleBooleanProperty(true)

    protected val canExtractText = SimpleBooleanProperty(false)

    protected val isExtractingText = SimpleBooleanProperty(false)

    protected val didTextExtractionReturnAnError = SimpleBooleanProperty(false)

    protected val textExtractionErrorMessage = SimpleStringProperty("")

    protected val showTextExtractorInfo = SimpleBooleanProperty(false)

    protected val textExtractionInfo = SimpleStringProperty("")

    protected val extractedText = SimpleStringProperty("")


    protected var checkIfTextExtractorAvailableButton: ProcessingIndicatorButton by singleAssign()

    protected var customConfigurationPane: Pane by singleAssign()


    protected var lastSelectedFile: File? = File("")


    init {
        if (textExtractor.isIsAvailableDeterminedYet == false) {
            addIsAvailableDeterminedYetListener()
        }

        programExecutablePath.addListener { _, _, newValue -> checkIfProgramExecutableExists(newValue) }

        lastSelectedFilePath.addListener { _, _, newValue -> checkIfFileExists(newValue) }

        if (textExtractor is ExternalToolTextExtractorBase) {
            programExecutablePath.value = textExtractor.programExecutablePath
        }

        userMustSelectProgramExecutablePath.value = textExtractor is ExternalToolTextExtractorBase && textExtractor.isAvailable == false
    }


    override val root = vbox {
        useMaxHeight = true

        hbox {
            alignment = Pos.CENTER_LEFT

            setBackgroundToColor(Color.ORANGE)

            visibleWhen(showInstallHint)
            ensureOnlyUsesSpaceIfVisible()

            label(installHintMessage) {
                useMaxWidth = true

                paddingAll = 8.0
                textFill = Color.WHITE

                lineSpacing = 4.0
                isWrapText = true

                hboxConstraints {
                    hGrow = Priority.ALWAYS

                    marginRight = 8.0
                }
            }

            checkIfTextExtractorAvailableButton = processingIndicatorButton(messages["recheck.program.executable.found.button.label"]) {
                fixedHeight = 40.0
                minWidth = 125.0

                action { recheckIfProgramIsNowAvailable() }
            }
        }

        hbox {
            prefHeight = TextFieldInputPaneHeight
            alignment = Pos.CENTER_LEFT

            visibleWhen(userMustSelectProgramExecutablePath)
            ensureOnlyUsesSpaceIfVisible()

            vboxConstraints {
                marginTopBottom(8.0)
            }


            label(messages["extract.text.tab.select.program.executable.label"])

            textfield(programExecutablePath) {
                useMaxHeight = true

                hboxConstraints {
                    hGrow = Priority.ALWAYS

                    marginLeftRight(6.0)
                }
            }

            button(messages["open.file.button.label"]) {
                useMaxHeight = true
                prefWidth = 45.0

                action { selectProgramExecutablePath() }
            }

        }

        // may be configured in sub classes
        customConfigurationPane = vbox {
            visibleWhen(showCustomTextExtractorConfigurationPane)
            ensureOnlyUsesSpaceIfVisible()
        }

        hbox {
            prefHeight = TextFieldInputPaneHeight
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

                action { selectFileToExtract() }
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

                enableWhen(canExtractText.and(isExtractingText.not()))

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


    protected open fun addIsAvailableDeterminedYetListener() {
        textExtractor.addIsIsAvailableDeterminedYetListener {
            runLater {
                recheckIfInstallHintShouldBeDisplayed()
                updateCanExtractText()
            }
        }
    }

    protected open fun recheckIfProgramIsNowAvailable() {
        (textExtractor as? ExternalToolTextExtractorBase)?.let {
            textExtractor.setProgramExecutablePathTo(textExtractor.programExecutablePath)
        }

        recheckIfInstallHintShouldBeDisplayed()
    }

    protected open fun recheckIfInstallHintShouldBeDisplayed() {
        showInstallHint.value = textExtractor.isAvailable == false && textExtractor.installHint.isNotBlank()

        checkIfTextExtractorAvailableButton.resetIsProcessing()
    }


    protected open fun checkIfProgramExecutableExists(programExecutablePath: String) {
        if (textExtractor is ExternalToolTextExtractorBase) {
            textExtractor.setProgramExecutablePathTo(programExecutablePath)

            updateCanExtractText()
        }
    }

    protected open fun selectProgramExecutablePath() {
        selectFile(programExecutablePath) { selectedProgramExecutablePath ->
            selectedProgramExecutablePath(selectedProgramExecutablePath.absolutePath)
        }
    }

    protected fun selectedProgramExecutablePath(selectedProgramExecutablePath: String) {
        checkIfProgramExecutableExists(selectedProgramExecutablePath)

        ifPossibleExtractTextOfFileAndShowResult()
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

    protected open fun keyReleased(event: KeyEvent) {
        if (event.code == KeyCode.ENTER) {
            if (isExistingFileSelected.value) {
                lastSelectedFile?.let { selectedFileToExtract(it) }
            }
        }
    }

    protected open fun selectFileToExtract() {
        selectFile(SimpleStringProperty(lastSelectedFile?.absolutePath ?: "")) { selectedFile ->
            selectedFileToExtract(selectedFile)
        }
    }

    protected open fun selectedFileToExtract(file: File) {
        lastSelectedFilePath.value = file.absolutePath

        existingFileSelected(file)

        ifPossibleExtractTextOfFileAndShowResult()
    }

    protected open fun ifPossibleExtractTextOfFileAndShowResult() {
        if (canExtractText.value) {
            extractTextOfFileAndShowResult()
        }
    }

    protected open fun existingFileSelected(file: File) {
        lastSelectedFile = file

        isExistingFileSelected.value = true

        updateCanExtractText()
    }

    protected open fun updateCanExtractText() {
        canExtractText.value = isExistingFileSelected.value && textExtractor.isAvailable
    }

    protected open fun selectFile(currentValue: SimpleStringProperty, pathSelected: (File) -> Unit) {
        val fileChooser = FileChooser()

        val currentValueAsFile = File(currentValue.value)

        if (currentValueAsFile.parentFile?.exists() == true) {
            fileChooser.initialDirectory = currentValueAsFile.parentFile
        }

        fileChooser.showOpenDialog(FX.primaryStage)?.let { selectedFile ->
            currentValue.value = selectedFile.absolutePath

            pathSelected(selectedFile)
        }
    }

    protected open fun selectFolder(currentValue: SimpleStringProperty, pathSelected: (File) -> Unit) {
        val fileChooser = DirectoryChooser()

        val currentValueAsFile = File(currentValue.value)

        if (currentValueAsFile.exists()) {
            fileChooser.initialDirectory = currentValueAsFile
        }

        fileChooser.showDialog(FX.primaryStage)?.let { selectedFolder ->
            currentValue.value = selectedFolder.absolutePath

            pathSelected(selectedFolder)
        }
    }


    protected open fun extractTextOfFileAndShowResult() {
        lastSelectedFile?.let { file ->
            isExtractingText.value = true
            didTextExtractionReturnAnError.value = false
            showTextExtractorInfo.value = false

            GlobalScope.launch(Dispatchers.IO) {
                val stopwatch = Stopwatch()

                val extractionResult = extractTextOfFile(file)

                stopwatch.stop()

                withContext(Dispatchers.JavaFx) {
                    showExtractedTextOnUiThread(file, extractionResult, stopwatch)
                }
            }
        }
    }

    protected open fun showExtractedTextOnUiThread(fileToExtract: File, extractionResult: ExtractionResult, stopwatch: Stopwatch) {
        isExtractingText.value = false
        extractionTime.value = stopwatch.formatElapsedTime()

        extractedText.value = extractionResult.text ?: ""

        if (extractionResult is ExtractionResultForExtractor) {
            showTextExtractorInfo.value = true
            textExtractionInfo.value = String.format(messages["info.text.extracted.with"], extractionResult.extractor?.name)
        }

        didTextExtractionReturnAnError.value = extractionResult.errorOccurred
        extractionResult.error?.let { error ->
            textExtractionErrorMessage.value = getErrorMessage(fileToExtract, error)
        }
    }

    protected open fun getErrorMessage(fileToExtract: File, error: ErrorInfo): String {
        val extractorName = textExtractor.name
        val fileType = fileToExtract.extension

        return when (error.type) {
            ErrorType.FileTypeNotSupportedByExtractor -> String.format(messages["error.message.extractor.does.not.support.extracting.file.type"], extractorName, fileType)
            ErrorType.ExtractorNotAvailable -> String.format(messages["error.message.extractor.not.available"], extractorName)
            ErrorType.ParseError -> String.format(messages["error.message.could.not.parse.file"], error.exception?.localizedMessage)
            ErrorType.NoExtractorFoundForFileType -> String.format(messages["error.message.no.extractor.found.for.type"], fileType)
        }
    }


    protected open suspend fun extractTextOfFile(file: File): ExtractionResult {
        try {
            val stopwatch = Stopwatch()

            val extractedText = textExtractor.extractTextSuspendable(file)

            stopwatch.stopAndLog("Extracting text of file $file", logger)

            return extractedText
        } catch (e: Exception) {
            logger.error("Could not extract text of file $file", e)

            return ExtractionResult(ErrorInfo(ErrorType.ParseError, e)) // TODO: add suitable ErrorType
        }
    }

}