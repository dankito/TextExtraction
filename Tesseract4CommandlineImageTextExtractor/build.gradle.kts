plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


dependencies {
    api(project(":TextExtractorCommon"))
    api(project(":TesseractCommon"))


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "tesseract4-commandline-text-extractor"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")