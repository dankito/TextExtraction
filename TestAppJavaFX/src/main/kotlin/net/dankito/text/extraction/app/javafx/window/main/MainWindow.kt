package net.dankito.text.extraction.app.javafx.window.main

import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import net.dankito.text.extraction.app.javafx.window.main.controls.ExtractTextTabBase
import net.dankito.text.extraction.app.javafx.window.main.controls.OpenPdfExtractTextTab
import net.dankito.text.extraction.app.javafx.window.main.controls.itextExtractTextTab
import net.dankito.text.extraction.app.javafx.window.main.controls.pdfToTextPdfTextExtractorExtractTextTab
import net.dankito.utils.PackageInfo
import net.dankito.utils.ThreadPool
import tornadofx.*


class MainWindow : Fragment(String.format(FX.messages["application.title"], PackageInfo.getAppVersionFromManifest())) {


    private val threadPool = ThreadPool()


    override val root = vbox {
        prefWidth = 850.0
        prefHeight = 450.0


        tabpane {
            addTab("main.window.tab.openpdf", OpenPdfExtractTextTab(threadPool))
            addTab("main.window.tab.itext", itextExtractTextTab(threadPool))
            addTab("main.window.tab.pdftotext", pdfToTextPdfTextExtractorExtractTextTab(threadPool))

            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
        }

    }


    private fun TabPane.addTab(titleResourceKey: String, tabContent: ExtractTextTabBase) {
        tab(messages[titleResourceKey], tabContent.root).apply {
            isClosable = false
        }
    }

}