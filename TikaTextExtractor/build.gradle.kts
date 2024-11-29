plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


val tikaVersion: String by project

dependencies {
    api(project(":TextExtractorCommon"))
    api(project(":TesseractCommon"))

    implementation("org.apache.tika:tika-java7:$tikaVersion")


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "tika-text-extractor"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")