plugins {
    kotlin("jvm")
}


kotlin {
    jvmToolchain(8)
}


val iTextVersion: String by project

dependencies {
    api(project(":TextExtractorCommon"))

    implementation("com.itextpdf:itext7-core:$iTextVersion")


    testImplementation(project(path = ":TextExtractorCommon", configuration = "tests"))
}


ext["customArtifactId"] = "itext-text-extractor"

// iText is under AGPL license, so we also have to put this library and AGPL
ext["licenseName"] = "GNU Affero General Public License v3.0"
ext["licenseUrl"] = "https://www.gnu.org/licenses/agpl.txt"

apply(from = "../gradle/scripts/publish-dankito.gradle.kts")