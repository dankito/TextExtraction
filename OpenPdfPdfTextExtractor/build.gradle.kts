plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


val openPdfVersion: String by project
val bouncyCastleVersion: String by project

dependencies {
    api(project(":TextExtractorCommon"))

    implementation("com.github.librepdf:openpdf:$openPdfVersion")
    // optional dependencies of OpenPdf but needed for some PDFs
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncyCastleVersion")
    implementation("org.bouncycastle:bcpkix-jdk15on:$bouncyCastleVersion")


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "openpdf-text-extractor"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")