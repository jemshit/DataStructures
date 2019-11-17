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

}