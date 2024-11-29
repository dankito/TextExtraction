pluginManagement {
    val kotlinVersion: String by settings


    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version kotlinVersion

        kotlin("plugin.allopen") version kotlinVersion
        kotlin("plugin.noarg") version kotlinVersion
    }
}


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}


include(":TextExtractorCommon")

include(":PopplerPdfTextExtractor")
include(":iText2PdfTextExtractor")
include(":iTextPdfTextExtractor")
include(":PdfBoxPdfTextExtractor")
include(":PdfBox2PdfTextExtractor")
include(":PdfBoxAndroidPdfTextExtractor")
include(":OpenPdfPdfTextExtractor")

include(":TesseractCommon")
include(":Tesseract4CommandlineImageTextExtractor")
include(":Tesseract4JniImageTextExtractor")
include(":FineReaderHotFolderImageTextExtractor")
include(":FineReaderCommandlineImageTextExtractor")

include(":TikaTextExtractor")

include(":TestAppAndroid")

include (":TestAppJavaFX")
