package suffix_array

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SearchPatternTest {

    @Test
    fun test1() {
        val text = "banana"
        val pattern = "nan"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val patternIndex = suffixArray.searchPattern(pattern)

        assertEquals(2, patternIndex)
    }

    @Test
    fun test2() {
        val text = "camel"
        val pattern = "ca"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val patternIndex = suffixArray.searchPattern(pattern)

        assertEquals(0, patternIndex)
    }

    @Test
    fun test3() {
        val text = "camel"
        val pattern = "a"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val patternIndex = suffixArray.searchPattern(pattern)

        assertEquals(1, patternIndex)
    }

    @Test
    fun test4() {
        val text = "camel"
        val pattern = "me"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val patternIndex = suffixArray.searchPattern(pattern)

        assertEquals(2, patternIndex)
    }

    @Test
    fun test5() {
        val text = "camel"
        val pattern = "mel"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val patternIndex = suffixArray.searchPattern(pattern)

        assertEquals(2, patternIndex)
    }

    @Test
    fun test6() {
        val text = "camel"
        val pattern = "ml"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val patternIndex = suffixArray.searchPattern(pattern)

        assertNull(patternIndex)
    }

}