package net.dankito.text.extraction.app.javafx.window.main

import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import net.dankito.text.extraction.ITextExtractorRegistry
import net.dankito.text.extraction.TextExtractorRegistry
import net.dankito.text.extraction.TikaTextExtractor
import net.dankito.text.extraction.app.javafx.window.main.controls.*
import net.dankito.text.extraction.image.Tesseract4JniImageTextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.text.extraction.pdf.OpenPdfPdfTextExtractor
import net.dankito.text.extraction.pdf.itextPdfTextExtractor
import net.dankito.text.extraction.pdf.pdfToTextPdfTextExtractor
import net.dankito.utils.PackageInfo
import tornadofx.*


class MainWindow : Fragment(String.format(FX.messages["application.title"], PackageInfo.getAppVersionFromManifest())) {


    private val extractorRegistry: ITextExtractorRegistry = TextExtractorRegistry(listOf(
        OpenPdfPdfTextExtractor(),
        itextPdfTextExtractor(),
        pdfToTextPdfTextExtractor(),
        Tesseract4JniImageTextExtractor(TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German))),
        TikaTextExtractor()
    ))


    override val root = vbox {
        prefWidth = 850.0
        prefHeight = 450.0


        tabpane {
            addTab("main.window.tab.openpdf", OpenPdfExtractTextTab())
            addTab("main.window.tab.itext", itextExtractTextTab())
            addTab("main.window.tab.pdftotext", pdfToTextPdfTextExtractorExtractTextTab())
            addTab("main.window.tab.tesseract4jni", Tesseract4JniImageTextExtractorExtractTextTab())
            addTab("main.window.tab.tika", TikaTextExtractorExtractTextTab())
            addTab("main.window.tab.find.best.extractor.for.type", FindBestTextExtractorExtractTextTab(extractorRegistry))

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