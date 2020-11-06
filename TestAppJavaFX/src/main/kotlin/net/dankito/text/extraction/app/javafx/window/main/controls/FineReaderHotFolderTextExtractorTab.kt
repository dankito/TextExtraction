package net.dankito.text.extraction.app.javafx.window.main.controls

import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import net.dankito.text.extraction.image.FineReaderHotFolderImageTextExtractor
import tornadofx.*
import java.io.File


open class FineReaderHotFolderTextExtractorTab(
    textExtractor: FineReaderHotFolderImageTextExtractor
) : TextExtractorTab(textExtractor) {

    protected val hotFolderPath = SimpleStringProperty("")

    protected val hotFolderOutputPath = SimpleStringProperty("")


    init {
        configureCustomTextExtractorConfigurationPane()
    }


    protected open fun configureCustomTextExtractorConfigurationPane() {
        showCustomTextExtractorConfigurationPane.value = true

        customConfigurationPane.apply {

            form {
                fieldset {
                    field(messages["extract.text.tab.configure.finereader.hotfolder.path.label"]) {
                        prefHeight = TextFieldInputPaneHeight

                        hbox {
                            textfield(hotFolderPath) {
                                useMaxHeight = true

                                hboxConstraints {
                                    hGrow = Priority.ALWAYS

                                    marginLeftRight(6.0)
                                }
                            }

                            button(messages["open.file.button.label"]) {
                                useMaxHeight = true
                                prefWidth = 45.0

                                action { selectHotFolderPath() }
                            }
                        }
                    }

                    field(messages["extract.text.tab.configure.finereader.hotfolder.output.path.label"]) {
                        prefHeight = TextFieldInputPaneHeight

                        hbox {
                            textfield(hotFolderOutputPath) {
                                useMaxHeight = true

                                hboxConstraints {
                                    hGrow = Priority.ALWAYS

                                    marginLeftRight(6.0)
                                }
                            }

                            button(messages["open.file.button.label"]) {
                                useMaxHeight = true
                                prefWidth = 45.0

                                action { selectHotFolderOutputPath() }
                            }
                        }
                    }
                }
            }
        }
    }


    protected open fun selectHotFolderPath() {
        selectFolder(hotFolderPath) { selectedHotFolder ->
            if (hotFolderOutputPath.value.isBlank()) {
                hotFolderOutputPath.value = selectedHotFolder.absolutePath
            }

            checkIfHotFolderIsConfiguredCorrectly()
        }
    }

    protected open fun selectHotFolderOutputPath() {
        selectFolder(hotFolderOutputPath) { selectedHotFolderOutput ->
            checkIfHotFolderIsConfiguredCorrectly()
        }
    }

    protected open fun checkIfHotFolderIsConfiguredCorrectly() {
        addIsAvailableDeterminedYetListener()
        checkIfTextExtractorAvailableButton.setIsProcessing()

        (textExtractor as FineReaderHotFolderImageTextExtractor).updateConfigAndDetectIsAvailableAsync(
            File(hotFolderPath.value), File(hotFolderOutputPath.value)
        )
    }

    override fun recheckIfProgramIsNowAvailable() {
        checkIfHotFolderIsConfiguredCorrectly()
    }

}