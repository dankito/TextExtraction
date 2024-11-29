plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


val pdfBox2Version: String by project
val pdfBoxLayoutTextStripperVersion: String by project

dependencies {
    api(project(":TextExtractorCommon"))

    implementation("org.apache.pdfbox:pdfbox:$pdfBox2Version")

    implementation("io.github.jonathanlink:PDFLayoutTextStripper:$pdfBoxLayoutTextStripperVersion")


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "pdfbox2-text-extractor"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")
