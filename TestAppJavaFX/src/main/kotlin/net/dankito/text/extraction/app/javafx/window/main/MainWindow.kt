package net.dankito.text.extraction.app.javafx.window.main

import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import net.dankito.text.extraction.FindBestTextExtractor
import net.dankito.text.extraction.ITextExtractorRegistry
import net.dankito.text.extraction.TextExtractorRegistry
import net.dankito.text.extraction.TikaTextExtractor
import net.dankito.text.extraction.app.javafx.window.main.controls.TextExtractorTab
import net.dankito.text.extraction.image.Tesseract4CommandlineImageTextExtractor
import net.dankito.text.extraction.image.Tesseract4JniImageTextExtractor
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.text.extraction.image.model.TesseractHelper
import net.dankito.text.extraction.model.PdfContentExtractorStrategy
import net.dankito.text.extraction.model.TikaSettings
import net.dankito.text.extraction.pdf.OpenPdfPdfTextExtractor
import net.dankito.text.extraction.pdf.itextPdfTextExtractor
import net.dankito.text.extraction.pdf.pdfToTextPdfTextExtractor
import net.dankito.utils.PackageInfo
import net.dankito.utils.process.CommandExecutor
import tornadofx.*


class MainWindow : Fragment(String.format(FX.messages["application.title"], PackageInfo.getAppVersionFromManifest())) {


    private val commandExecutor = CommandExecutor()

    private val tesseractConfig = TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German))

    private val tesseractHelper = TesseractHelper()


    private val openPdfPdfTextExtractor = OpenPdfPdfTextExtractor()

    private val itextPdfTextExtractor = itextPdfTextExtractor()

    private val pdfToTextPdfTextExtractor = pdfToTextPdfTextExtractor()

    private val tesseract4CommandlineImageTextExtractor = Tesseract4CommandlineImageTextExtractor(tesseractConfig, tesseractHelper, commandExecutor)

    private val tesseract4JniImageTextExtractor = Tesseract4JniImageTextExtractor(tesseractConfig, tesseractHelper)

    private val tikaTextExtractor = TikaTextExtractor(TikaSettings(PdfContentExtractorStrategy.OcrAndText, tesseractConfig), tesseractHelper)

    private val extractorRegistry: ITextExtractorRegistry = TextExtractorRegistry(listOf(
        openPdfPdfTextExtractor,
        itextPdfTextExtractor,
        pdfToTextPdfTextExtractor,
        tesseract4CommandlineImageTextExtractor,
        tesseract4JniImageTextExtractor,
        tikaTextExtractor
    ))


    override val root = vbox {
        prefWidth = 850.0
        prefHeight = 450.0


        tabpane {
            addTab("main.window.tab.openpdf", TextExtractorTab(openPdfPdfTextExtractor))
            addTab("main.window.tab.itext", TextExtractorTab(itextPdfTextExtractor))
            addTab("main.window.tab.pdftotext", TextExtractorTab(pdfToTextPdfTextExtractor))
            addTab("main.window.tab.tesseract4.commandline", TextExtractorTab(tesseract4CommandlineImageTextExtractor))
            addTab("main.window.tab.tesseract4.jni", TextExtractorTab(tesseract4JniImageTextExtractor))
            addTab("main.window.tab.tika", TextExtractorTab(tikaTextExtractor))
            addTab("main.window.tab.find.best.extractor.for.type", TextExtractorTab(FindBestTextExtractor(extractorRegistry)))

            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
        }

    }


    private fun TabPane.addTab(titleResourceKey: String, tabContent: TextExtractorTab) {
        tab(messages[titleResourceKey], tabContent.root).apply {
            isClosable = false
        }
    }

}