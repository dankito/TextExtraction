package net.dankito.text.extraction

import net.dankito.text.extraction.image.model.OcrLanguage
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.Page
import net.dankito.utils.io.FileUtils
import net.dankito.utils.resources.ResourceFilesExtractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File


abstract class TextExtractorTestBase {

    companion object {
        const val TestFilesFolderName = "test_files"

        const val WikipediaGandhiEnglishTestFileBaseName = "Wikipedia_Gandhi_en"
        const val WikipediaGandhiGermanTestFileBaseName = "Wikipedia_Gandhi_de"
    }


    abstract fun createExtractorForLanguage(language: OcrLanguage): ITextExtractor

    abstract val textExtractorType: TextExtractorType


    protected val fileUtils = FileUtils()

    protected val resourceFilesExtractor = ResourceFilesExtractor()


    @Test
    fun wikipediaGandhiEnglish() {

        // given
        val underTest = createExtractorForLanguage(OcrLanguage.English)

        val testFile = getTestFile(WikipediaGandhiEnglishTestFileBaseName)
        val countExpectedPages = getCountExpectedPages(61)

        // when
        val result = underTest.extractText(testFile)

        // then
        assertCountPagesAndTextIsNotEmpty(result, countExpectedPages) // OpenPdf fails here

        val firstPage = result.pages[0]

        assertContainsIgnoringWhiteSpaces(firstPage,
            "Born and raised in a Hindu family in coastal Gujarat, western India, and trained",
            "in law at the Inner Temple, London, Gandhi first employed nonviolent civil",
            "disobedience as an expatriate lawyer in South Africa, in the resident Indian",
            "community's struggle for civil rights. After his return to India in 1915, he set",
            "about organising peasants, farmers, and urban labourers to protest against",
            "excessive land-tax and discrimination. Assuming leadership of the Indian",
            "National Congress in 1921, Gandhi led nationwide campaigns for various social "
        )

        assertContainsIgnoringWhiteSpaces(firstPage,
            "Gandhi led Indians in challenging the British-imposed salt tax with the 400 km",
            "(250 mi) Dandi Salt March in 1930, and later in calling for the British to Quit",
            "Mohandas",
            "India in 1942. He was imprisoned for many years, upon many occasions, in both",
            "Karamchand Gandhi",
            "South Africa and India. He lived modestly in a self-sufficient residential",
            "2 October 1869",
            "community and wore the traditional Indian dhoti and shawl, woven with yarn",
            "Porbandar,",
            "hand-spun on a charkha. He ate simple vegetarian food, and also undertook long",
            "Porbandar State,",
            "fasts as a means of both self-purification and political protest."
        )

        assertContainsIgnoringWhiteSpaces(firstPage,
            "As many displaced Hindus, Muslims, and Sikhs made their way to their new",
            "lands, religious violence broke out, especially in the Punjab and Bengal.",
            "Eschewing the official celebration of independence in Delhi, Gandhi visited the",
            "affected areas, attempting to provide solace. In the months following, he",
            "undertook several fasts unto death to stop religious violence. The last of these, "
        )

        assertContainsIgnoringWhiteSpaces(firstPage,
            "30 January 1948",
            "(aged 78)",
            "New Delhi, Delhi",
            "Dominion of India",
            "(present-day India)"
        )
    }

    @Test
    fun wikipediaGandhiGerman() {

        // given
        val underTest = createExtractorForLanguage(OcrLanguage.German)

        val testFile = getTestFile(WikipediaGandhiGermanTestFileBaseName)
        val countExpectedPages = getCountExpectedPages(32)

        // when
        val result = underTest.extractText(testFile)

        // then
        assertCountPagesAndTextIsNotEmpty(result, countExpectedPages) // OpenPdf fails here

        val firstPage = result.pages[0]

        assertContainsIgnoringWhiteSpaces(firstPage,
            "in Neu-Delhi, Delhi) war ein indischer Rechtsanwalt, Widerstandskämpfer,",
            "Revolutionär, Publizist, Morallehrer, Asket und Pazifist.")

        assertContainsIgnoringWhiteSpaces(firstPage,
            "Zu Beginn des 20. Jahrhunderts setzte sich Gandhi in Südafrika gegen die",
            "Rassentrennung und für die Gleichberechtigung der Inder ein. Danach",
            "entwickelte er sich ab Ende der 1910er Jahre in Indien zum politischen und",
            "geistigen Anführer der indischen Unabhängigkeitsbewegung. Gandhi forderte",
            "die Menschenrechte für Dalit und Frauen, er trat für die Versöhnung zwischen",
            "Hindus und Muslimen ein, kämpfte gegen die koloniale Ausbeutung und für ein",
            "neues, autarkes, von der bäuerlichen Lebensweise geprägtes Wirtschaftssystem.",
            "Die Unabhängigkeitsbewegung führte mit gewaltfreiem Widerstand, zivilem",
            "Ungehorsam und Hungerstreiks schließlich das Ende der britischen",
            "Kolonialherrschaft über Indien herbei (1947), verbunden mit der Teilung",
            "Indiens. Ein halbes Jahr danach fiel Gandhi einem Attentat zum Opfer."
        )

        assertContainsIgnoringWhiteSpaces(firstPage,
            "Gandhi musste in Südafrika und Indien insgesamt acht Jahre in Gefängnissen",
            "verbringen. Seine Grundhaltung Satyagraha, das beharrliche Festhalten an der",
            "Wahrheit, umfasst neben Ahimsa, der Gewaltlosigkeit, noch weitere ethische",
            "Forderungen wie etwa Swaraj, was sowohl individuelle als auch politische",
            "Selbstkontrolle und Selbstbestimmung bedeutet."
        )

        assertContainsIgnoringWhiteSpaces(firstPage,
            "Schon zu Lebzeiten war Gandhi weltberühmt, für viele ein Vorbild und so",
            "anerkannt, dass er mehrmals für den Friedensnobelpreis nominiert wurde. In",
            "seinem Todesjahr wurde dieser Nobelpreis symbolisch nicht vergeben. Ebenso wie Nelson Mandela oder Martin Luther King gilt",
            "er als herausragender Vertreter im Freiheitskampf gegen Unterdrückung und soziale Ungerechtigkeit."
        )

        assertContainsIgnoringWhiteSpaces(firstPage,
            "Mohandas Karamchand Gandhi",
            "(Porträtfotografie etwa Ende der",
            "1930er Jahre)"
        )
    }


    protected open fun getCountExpectedPages(countPagesInWholePdf: Int): Int {
        return if (textExtractorType == TextExtractorType.Image) 1 // for images only first page gets extracted
            else countPagesInWholePdf
    }


    protected open fun assertCountPagesAndTextIsNotEmpty(result: ExtractionResult, countPages: Int) {
        assertThat(result.pages).hasSize(countPages)

        assertThat(result.text?.trim()).isNotEmpty()
    }

    protected open fun assertContainsIgnoringWhiteSpaces(page: Page, vararg stringsToContain: String) {
        assertThat(normalizeWhitespace(page.text)).contains(*stringsToContain)
    }

    protected open fun normalizeWhitespace(toNormalize: CharSequence): String {
        val result = StringBuilder(toNormalize.length)
        var lastWasSpace = true

        for (i in 0 until toNormalize.length) {
            val c = toNormalize[i]
            if (Character.isWhitespace(c)) {
                if (!!!lastWasSpace) {
                    result.append(' ')
                }

                lastWasSpace = true
            } else {
                result.append(c)
                lastWasSpace = false
            }
        }

        return result.toString().trim()
    }


    protected open fun getTestFile(filename: String): File {
        val resourceFolderName = if (textExtractorType == TextExtractorType.Pdf) "pdf" else "images"
        val testFilename = if (textExtractorType == TextExtractorType.Pdf) filename else filename + "_01"
        val fileExtension = if (textExtractorType == TextExtractorType.Pdf) "pdf" else "png"
        val testFile = File(File(TestFilesFolderName, resourceFolderName), testFilename + "." + fileExtension)

        val fileUrl = TextExtractorTestBase::class.java.classLoader.getResource(testFile.path)

        if ("jar" == fileUrl.protocol) { // if running from jar file
            val tempFolder = fileUtils.getTempDir() // extract test pdfs to temp folder

            resourceFilesExtractor.extractAllFilesFromFolder(
                TextExtractorTestBase::class.java,
                File(TestFilesFolderName), tempFolder
            )

            return File(tempFolder, testFilename)
        } else {
            return File(fileUrl.toURI())
        }
    }

}