package suffix_array.problems

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LongestCommonSubstringTest {

    @Test
    fun `LCP 2 strings- test1`() {
        val length = longestCommonSubstringLength("GeeksforGeeks", "GeeksQuiz")
        val substring = longestCommonSubstring("GeeksforGeeks", "GeeksQuiz")

        assertEquals(5, length)
        assertEquals("Geeks", substring)
    }

    @Test
    fun `LCP 2 strings- test2`() {
        val length = longestCommonSubstringLength("abcdxyz", "xyzabcd")
        val substring = longestCommonSubstring("abcdxyz", "xyzabcd")

        assertEquals(4, length)
        assertEquals("abcd", substring)
    }

    @Test
    fun `LCP 2 strings- test3`() {
        val length = longestCommonSubstringLength("zxabcdezy", "yzabcdezx")
        val substring = longestCommonSubstring("zxabcdezy", "yzabcdezx")

        assertEquals(6, length)
        assertEquals("abcdez", substring)
    }

    @Test
    fun `LCP 2 strings- test4`() {
        val length = longestCommonSubstringLength("acb", "deft")
        val substring = longestCommonSubstring("acb", "deft")

        assertEquals(0, length)
        assertEquals("", substring)
    }

    @Test
    fun `LCP 2 strings- test5`() {
        val length = longestCommonSubstringLength("abca", "bca")
        val substring = longestCommonSubstring("abca", "bca")

        assertEquals(3, length)
        assertEquals("bca", substring)
    }

    @Test
    fun `LCP 2 strings- test6`() {
        val length = longestCommonSubstringLength("abd", "cabe")
        val substring = longestCommonSubstring("abd", "cabe")

        assertEquals(2, length)
        assertEquals("ab", substring)
    }


}