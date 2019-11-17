package suffix_array

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SuffixArrayMedTest {

    @Test
    fun test1() {
        val input = "banana"
        val suffixArray: SuffixArray = SuffixArrayMed(input)

        val result = suffixArray.suffixArray

        assertEquals(6, result.size)
        assertEquals(5, result[0])
        assertEquals(3, result[1])
        assertEquals(1, result[2])
        assertEquals(0, result[3])
        assertEquals(4, result[4])
        assertEquals(2, result[5])
    }

    @Test
    fun test2() {
        val input = "camel"
        val suffixArray: SuffixArray = SuffixArrayMed(input)

        val result = suffixArray.suffixArray

        assertEquals(5, result.size)
        assertEquals(1, result[0])
        assertEquals(0, result[1])
        assertEquals(3, result[2])
        assertEquals(4, result[3])
        assertEquals(2, result[4])
    }

    @Test
    fun test3() {
        val input = "abaab"
        val suffixArray: SuffixArray = SuffixArrayMed(input)

        val result = suffixArray.suffixArray

        assertEquals(5, result.size)
        assertEquals(2, result[0])
        assertEquals(3, result[1])
        assertEquals(0, result[2])
        assertEquals(4, result[3])
        assertEquals(1, result[4])
    }

}