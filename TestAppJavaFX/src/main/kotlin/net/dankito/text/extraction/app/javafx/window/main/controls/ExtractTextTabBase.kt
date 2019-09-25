package net.dankito.text.extraction.app.javafx.window.main.controls

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.model.ExtractedText
import net.dankito.utils.IThreadPool
import net.dankito.utils.javafx.ui.extensions.ensureOnlyUsesSpaceIfVisible
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
            val startTime = Date().time
            isExtractingText.value = true

            extractTextOfFileAsync(file) { extractedText ->
                val durationMillis = Date().time - startTime

                runLater {
                    showExtractedTextOnUiThread(extractedText, durationMillis)
                }
            }
        }
    }

    protected open fun showExtractedTextOnUiThread(extractedText: ExtractedText?, durationMillis: Long) {
        isExtractingText.value = false
        extractionTime.value = String.format(
            "%02d:%02d.%03d min", durationMillis / (60 * 1000),
            (durationMillis / 1000) % 60, durationMillis % 1000
        )

        extractedText?.let {
            this.extractedText.value = extractedText.text
        }

        // TODO: elsewise show error message
    }


    protected open fun extractTextOfFileAsync(file: File, callback: (ExtractedText?) -> Unit) {
        threadPool.runAsync {
            callback(extractTextOfFile(file))
        }
    }

    protected open fun extractTextOfFile(file: File): ExtractedText? {
        try {
            val textExtractor = getTextExtractor()

            val startTime = Date()
            val extractedText = textExtractor.extractText(file)
            val timeElapsed = Date().time - startTime.time

            logger.info("Extracting text of file $file took ${timeElapsed / 1000} seconds and ${timeElapsed % 1000} milliseconds")

            return extractedText
        } catch (e: Exception) {
            logger.error("Could not extract text of file $file", e)
        }

        return null // TODO: add error to ExtractedText instead of returning null
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