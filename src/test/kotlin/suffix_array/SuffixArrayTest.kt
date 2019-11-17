package suffix_array

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SuffixArrayTest {

    @Test
    fun test1() {
        val text = "ABBABAABAA"

        val suffixArraySlow = SuffixArraySlow(text)
        val suffixArrayMed = SuffixArrayMed(text)

        for (index in text.indices)
            assertEquals(suffixArraySlow.suffixArray[index], suffixArrayMed.suffixArray[index])
    }

    @Test
    fun test2() {
        val text = "BAAAAB0ABAAAAB1BABA2ABA3AAB4BBBB5BB"

        val suffixArraySlow = SuffixArraySlow(text)
        val suffixArrayMed = SuffixArrayMed(text)

        for (index in text.indices)
            assertEquals(suffixArraySlow.suffixArray[index], suffixArrayMed.suffixArray[index])
    }

    @Test
    fun testLcpBuild1() {
        val text = "ababbab"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val lcpArray = suffixArray.lcpArray

        assertEquals(0, lcpArray[0])
        assertEquals(2, lcpArray[1])
        assertEquals(2, lcpArray[2])
        assertEquals(0, lcpArray[3])
        assertEquals(1, lcpArray[4])
        assertEquals(3, lcpArray[5])
        assertEquals(1, lcpArray[6])
    }

    @Test
    fun lcsUniqueCharacters() {
        val ASCII_LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val suffixArray: SuffixArray = SuffixArrayMed(ASCII_LETTERS)

        for (index in suffixArray.suffixArray.indices)
            assertEquals(0, suffixArray.lcpArray[index])
    }

    @Test
    fun increasingLCPTest() {
        val UNIQUE_CHARS = "KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK"
        val suffixArray: SuffixArray = SuffixArrayMed(UNIQUE_CHARS)

        for (index in suffixArray.suffixArray.indices)
            assertEquals(index, suffixArray.lcpArray[index])
    }

    @Test
    fun lcpTest1() {
        val text = "ABBABAABAA"
        val lcpValues = intArrayOf(0, 1, 2, 1, 4, 2, 0, 3, 2, 1)
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        for (index in suffixArray.suffixArray.indices)
            assertEquals(lcpValues[index], suffixArray.lcpArray[index])
    }

    @Test
    fun lcpTest2() {
        val text = "ABABABAABB"
        val lcpValues = intArrayOf(0, 1, 3, 5, 2, 0, 1, 2, 4, 1)
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        for (index in suffixArray.suffixArray.indices)
            assertEquals(lcpValues[index], suffixArray.lcpArray[index])
    }

}