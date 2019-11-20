package suffix_array.problems

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class LongestRepeatingNonOverlappingSubstringTest {

    @Test
    fun `DP- test0`() {
        val result = longestRepeatingNonOverlappingSubstring("emememem")
        val result2 = longestRepeatingOverlappingSubstringLCP("emememem")

        assertEquals("emem", result)
        assertEquals("ememem", result2)
    }

    @Test
    fun `DP- test1`() {
        val result = longestRepeatingNonOverlappingSubstring("geeksforgeeks")
        val result2 = longestRepeatingOverlappingSubstringLCP("geeksforgeeks")

        assertEquals("geeks", result)
        assertEquals("geeks", result2)
    }

    @Test
    fun `DP- test2`() {
        val result = longestRepeatingNonOverlappingSubstring("aab")
        val result2 = longestRepeatingOverlappingSubstringLCP("aab")

        assertEquals("a", result)
        assertEquals("a", result2)
    }

    @Test
    fun `DP- test3`() {
        val result = longestRepeatingNonOverlappingSubstring("aabaabaaba")
        val result2 = longestRepeatingOverlappingSubstringLCP("aabaabaaba")

        assertEquals("aaba", result)
        assertEquals("aabaaba", result2)
    }

    @Test
    fun `DP- test4`() {
        val result = longestRepeatingNonOverlappingSubstring("aaaaaaaaaaa")
        val result2 = longestRepeatingOverlappingSubstringLCP("aaaaaaaaaaa")

        assertEquals("aaaaa", result)
        assertEquals("aaaaaaaaaa", result2)
    }

    @Test
    fun `DP- test5`() {
        val result = longestRepeatingNonOverlappingSubstring("banana")
        val result2 = longestRepeatingOverlappingSubstringLCP("banana")

        assertTrue(result == "an" || result == "na")
        assertTrue(result2 == "ana")
    }

    @Test
    fun `DP- test6`() {
        val result = longestRepeatingNonOverlappingSubstring("abracadabra")
        val result2 = longestRepeatingOverlappingSubstringLCP("abracadabra")

        assertEquals("abra", result)
        assertEquals("abra", result2)
    }

    @Test
    fun `DP- test7`() {
        val result = longestRepeatingNonOverlappingSubstring("oneonetwotwothreethree")
        val result2 = longestRepeatingOverlappingSubstringLCP("oneonetwotwothreethree")

        assertEquals("three", result)
        assertEquals("three", result2)
    }

}