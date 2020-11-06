package net.dankito.text.extraction.app.javafx.window.main

import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import net.dankito.text.extraction.FindBestTextExtractor
import net.dankito.text.extraction.ITextExtractorRegistry
import net.dankito.text.extraction.TextExtractorRegistry
import net.dankito.text.extraction.TikaTextExtractor
import net.dankito.text.extraction.app.javafx.window.main.controls.FineReaderHotFolderTextExtractorTab
import net.dankito.text.extraction.app.javafx.window.main.controls.TextExtractorTab
import net.dankito.text.extraction.image.*
import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.image.model.TesseractConfig
import net.dankito.text.extraction.image.model.TesseractHelper
import net.dankito.text.extraction.model.PdfContentExtractorStrategy
import net.dankito.text.extraction.model.TikaSettings
import net.dankito.text.extraction.pdf.*
import net.dankito.utils.PackageInfo
import net.dankito.utils.process.CommandExecutor
import tornadofx.*
import java.io.File


class MainWindow : Fragment(String.format(FX.messages["application.title"], PackageInfo.getAppVersionFromManifest())) {


    private val commandExecutor = CommandExecutor()

    private val tesseractConfig = TesseractConfig(listOf(OcrLanguage.English, OcrLanguage.German))

    private val tesseractHelper = TesseractHelper()


    private val pdfToTextPdfTextExtractor = pdfToTextPdfTextExtractor(installHintLocalization = FX.messages)

//    private val openPdfPdfTextExtractor = OpenPdfPdfTextExtractor()

    private val itext2PdfTextExtractor = iText2PdfTextExtractor()

    private val itextPdfTextExtractor = iTextPdfTextExtractor()

    private val pdfBoxPdfTextExtractor = PdfBoxPdfTextExtractor()

    private val tesseract4CommandlineImageTextExtractor = Tesseract4CommandlineImageTextExtractor(tesseractConfig, tesseractHelper, commandExecutor, installHintLocalization = FX.messages)

    private val tesseract4JniImageTextExtractor = Tesseract4JniImageTextExtractor(tesseractConfig, tesseractHelper)

    private val fineReaderHotFolderImageTextExtractor = FineReaderHotFolderImageTextExtractor(FineReaderHotFolderConfig(File(""), File("")))

    private val fineReaderCommandlineImageTextExtractor = FineReaderCommandlineImageTextExtractor(commandExecutor)

    private val imageBasedPdfTextExtractor = ImageOnlyPdfTextExtractor(tesseract4CommandlineImageTextExtractor, pdfimagesImagesFromPdfExtractor(commandExecutor))

    private val tikaTextExtractor = TikaTextExtractor(TikaSettings(true, PdfContentExtractorStrategy.OcrAndText, tesseractConfig), tesseractHelper)

    private val extractorRegistry: ITextExtractorRegistry = TextExtractorRegistry(pdffontsPdfTypeDetector(), listOf(
        pdfToTextPdfTextExtractor,
//        openPdfPdfTextExtractor,
        itext2PdfTextExtractor,
        itextPdfTextExtractor,
        pdfBoxPdfTextExtractor,
        tesseract4CommandlineImageTextExtractor,
        tesseract4JniImageTextExtractor,
        fineReaderHotFolderImageTextExtractor,
        fineReaderCommandlineImageTextExtractor,
        imageBasedPdfTextExtractor,
        tikaTextExtractor
    ))


    override val root = vbox {
        prefWidth = 850.0
        prefHeight = 450.0


        tabpane {
            // do not include OpenPdfPdfTextExtractor and iText2PdfTextExtractor at the same time as both
            // have the same package and class names but different method and class signatures
//            addTab("main.window.tab.openpdf", TextExtractorTab(openPdfPdfTextExtractor))
            addTab("main.window.tab.itext2", TextExtractorTab(itext2PdfTextExtractor))
            addTab("main.window.tab.itext", TextExtractorTab(itextPdfTextExtractor))
            addTab("main.window.tab.pdfbox", TextExtractorTab(pdfBoxPdfTextExtractor))
            addTab("main.window.tab.pdftotext", TextExtractorTab(pdfToTextPdfTextExtractor))
            addTab("main.window.tab.tesseract4.commandline", TextExtractorTab(tesseract4CommandlineImageTextExtractor))
            addTab("main.window.tab.tesseract4.jni", TextExtractorTab(tesseract4JniImageTextExtractor))
            addTab("main.window.tab.finereader.hotfolder", FineReaderHotFolderTextExtractorTab(fineReaderHotFolderImageTextExtractor))
            addTab("main.window.tab.finereader.commandline", TextExtractorTab(fineReaderCommandlineImageTextExtractor))
            addTab("main.window.tab.image.based.pdf.text.extractor", TextExtractorTab(imageBasedPdfTextExtractor))
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