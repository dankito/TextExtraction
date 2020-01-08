package net.dankito.text.extraction.info

import net.dankito.text.extraction.info.model.DateData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DateExtractorTest {

    companion object {

        val ExpectedResult = DateData(27, 3, 1988, "", "")

        val ExpectedResultShortYear = DateData(27, 3, 88, "", "")

        val ExpectedResultShortDay = DateData(7, 3, 1988, "", "")

    }


    private val underTest = DateExtractor()


    @Test
    fun ddMMyyyyWithDots() {

        // when
        val result = underTest.extractDates("27.03.1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun ddMMyyWithDots() {

        // when
        val result = underTest.extractDates("27.03.88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun dMMyyyyWithDots() {

        // when
        val result = underTest.extractDates("7.03.1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun ddMyyyyWithDots() {

        // when
        val result = underTest.extractDates("27.3.1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun ddMMyyyyWithDashes() {

        // when
        val result = underTest.extractDates("27-03-1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun ddMMyyWithDashes() {

        // when
        val result = underTest.extractDates("27-03-88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun dMMyyyyWithDashes() {

        // when
        val result = underTest.extractDates("7-03-1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun ddMyyyyWithDashes() {

        // when
        val result = underTest.extractDates("27-3-1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun ddMMyyyyWithSlashes() {

        // when
        val result = underTest.extractDates("27/03/1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun ddMMyyWithSlashes() {

        // when
        val result = underTest.extractDates("27/03/88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun dMMyyyyWithSlashes() {

        // when
        val result = underTest.extractDates("7/03/1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun ddMyyyyWithSlashes() {

        // when
        val result = underTest.extractDates("27/3/1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun ddMMyyyyWithSpaces() {

        // when
        val result = underTest.extractDates("27 03 1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun ddMMyyWithSpaces() {

        // when
        val result = underTest.extractDates("27 03 88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun dMMyyyyWithSpaces() {

        // when
        val result = underTest.extractDates("7 03 1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun ddMyyyyWithSpaces() {

        // when
        val result = underTest.extractDates("27 3 1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }



    @Test
    fun MMddyyyyWithDots() {

        // when
        val result = underTest.extractDates("03.27.1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun MMddyyWithDots() {

        // when
        val result = underTest.extractDates("03.27.88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun MMdyyyyWithDots() {

        // when
        val result = underTest.extractDates("03.7.1988")

        // then
        assertThat(result).hasSize(1)

        // TODO: this cannot be distingued from 'dd.M.yyyy'
        assertThat(result.get(0)).isEqualTo(DateData(3, 7, 1988, "", ""))
    }

    @Test
    fun MddyyyyWithDots() {

        // when
        val result = underTest.extractDates("3.27.1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun MMddyyyyWithDashes() {

        // when
        val result = underTest.extractDates("03-27-1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun MMddyyWithDashes() {

        // when
        val result = underTest.extractDates("03-27-88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun MMdyyyyWithDashes() {

        // when
        val result = underTest.extractDates("03-7-1988")

        // then
        assertThat(result).hasSize(1)

        // TODO: this cannot be distingued from 'dd-M-yyyy'
        assertThat(result.get(0)).isEqualTo(DateData(3, 7, 1988, "", ""))
    }

    @Test
    fun MddyyyyWithDashes() {

        // when
        val result = underTest.extractDates("3-27-1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun MMddyyyyWithSlashes() {

        // when
        val result = underTest.extractDates("03/27/1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun MMddyyWithSlashes() {

        // when
        val result = underTest.extractDates("03/27/88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun MMdyyyyWithSlashes() {

        // when
        val result = underTest.extractDates("03/7/1988")

        // then
        assertThat(result).hasSize(1)

        // TODO: this cannot be distingued from 'dd/M/yyyy'
        assertThat(result.get(0)).isEqualTo(DateData(3, 7, 1988, "", ""))
    }

    @Test
    fun MddyyyyWithSlashes() {

        // when
        val result = underTest.extractDates("3/27/1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun MMddyyyyWithSpaces() {

        // when
        val result = underTest.extractDates("03 27 1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun MMddyyWithSpaces() {

        // when
        val result = underTest.extractDates("03 27 88")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun MMdyyyyWithSpaces() {

        // when
        val result = underTest.extractDates("03 7 1988")

        // then
        assertThat(result).hasSize(1)

        // TODO: this cannot be distingued from 'dd M yyyy'
        assertThat(result.get(0)).isEqualTo(DateData(3, 7, 1988, "", ""))
    }

    @Test
    fun MddyyyyWithSpaces() {

        // when
        val result = underTest.extractDates("3 27 1988")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }



    @Test
    fun yyyyMMddWithDots() {

        // when
        val result = underTest.extractDates("1988.03.27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun yyMMddWithDots() {

        // when
        val result = underTest.extractDates("88.03.27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun yyyyMMdWithDots() {

        // when
        val result = underTest.extractDates("1988.03.7")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun yyyyMddWithDots() {

        // when
        val result = underTest.extractDates("1988.3.27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun yyyyMMddWithDashes() {

        // when
        val result = underTest.extractDates("1988-03-27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun yyMMddWithDashes() {

        // when
        val result = underTest.extractDates("88-03-27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun yyyyMMdWithDashes() {

        // when
        val result = underTest.extractDates("1988-03-7")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun yyyyMddWithDashes() {

        // when
        val result = underTest.extractDates("1988-3-27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun yyyyMMddWithSlashes() {

        // when
        val result = underTest.extractDates("1988/03/27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun yyMMddWithSlashes() {

        // when
        val result = underTest.extractDates("88/03/27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun yyyyMMdWithSlashes() {

        // when
        val result = underTest.extractDates("1988/03/7")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun yyyyMddWithSlashes() {

        // when
        val result = underTest.extractDates("1988/3/27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }


    @Test
    fun yyyyMMddWithSpaces() {

        // when
        val result = underTest.extractDates("1988 03 27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

    @Test
    fun yyMMddWithSpaces() {

        // when
        val result = underTest.extractDates("88 03 27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortYear)
    }

    @Test
    fun yyyyMMdWithSpaces() {

        // when
        val result = underTest.extractDates("1988 03 7")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResultShortDay)
    }

    @Test
    fun yyyyMddWithSpaces() {

        // when
        val result = underTest.extractDates("1988 3 27")

        // then
        assertThat(result).hasSize(1)

        assertThat(result.get(0)).isEqualTo(ExpectedResult)
    }

}