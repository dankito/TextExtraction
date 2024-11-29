plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


dependencies {
    api(project(":TextExtractorCommon"))


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "finereader-hotfolder-text-extractor"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")