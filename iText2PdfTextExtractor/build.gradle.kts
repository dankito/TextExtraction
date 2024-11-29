plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


val iText2Version: String by project

dependencies {
    api(project(":TextExtractorCommon"))

    implementation("com.lowagie:itext:$iText2Version")


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "itext2-text-extractor"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")
