package net.dankito.text.extraction.app.javafx.window.main

import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import net.dankito.text.extraction.ITextExtractorRegistry
import net.dankito.text.extraction.TextExtractorRegistry
import net.dankito.text.extraction.TikaTextExtractor
import net.dankito.text.extraction.app.javafx.window.main.controls.*
import net.dankito.text.extraction.image.Tesseract4ImageTextExtractor
import net.dankito.text.extraction.image.model.Tesseract4Config
import net.dankito.text.extraction.pdf.OpenPdfPdfTextExtractor
import net.dankito.text.extraction.pdf.itextPdfTextExtractor
import net.dankito.text.extraction.pdf.pdfToTextPdfTextExtractor
import net.dankito.utils.PackageInfo
import net.dankito.utils.ThreadPool
import tornadofx.*


class MainWindow : Fragment(String.format(FX.messages["application.title"], PackageInfo.getAppVersionFromManifest())) {


    private val extractorRegistry: ITextExtractorRegistry = TextExtractorRegistry(listOf(
        OpenPdfPdfTextExtractor(),
        itextPdfTextExtractor(),
        pdfToTextPdfTextExtractor(),
        Tesseract4ImageTextExtractor(Tesseract4Config()),
        TikaTextExtractor()
    ))

    private val threadPool = ThreadPool()


    override val root = vbox {
        prefWidth = 850.0
        prefHeight = 450.0


        tabpane {
            addTab("main.window.tab.openpdf", OpenPdfExtractTextTab(threadPool))
            addTab("main.window.tab.itext", itextExtractTextTab(threadPool))
            addTab("main.window.tab.pdftotext", pdfToTextPdfTextExtractorExtractTextTab(threadPool))
            addTab("main.window.tab.tesseract4", Tesseract4ImageTextExtractorExtractTextTab(threadPool))
            addTab("main.window.tab.tika", TikaTextExtractorExtractTextTab(threadPool))
            addTab("main.window.tab.find.best.extractor.for.type", FindBestTextExtractorExtractTextTab(extractorRegistry, threadPool))

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