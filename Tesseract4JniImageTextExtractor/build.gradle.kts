plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


val tesseract4Version: String by project

dependencies {
    api(project(":TextExtractorCommon"))
    api(project(":TesseractCommon"))

    implementation("org.bytedeco:tesseract-platform:$tesseract4Version")


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "tesseract4-jni-text-extractor"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")