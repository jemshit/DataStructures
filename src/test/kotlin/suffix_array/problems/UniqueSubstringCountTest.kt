package suffix_array.problems

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import suffix_array.SuffixArray
import suffix_array.SuffixArrayMed

internal class UniqueSubstringCountTest{

    @Test
    fun test1() {
        val text = "azaza"
        val suffixArray: SuffixArray = SuffixArrayMed(text)

        val result = suffixArray.findUniqueSubstringCount()

        assertEquals(9, result)
    }

}