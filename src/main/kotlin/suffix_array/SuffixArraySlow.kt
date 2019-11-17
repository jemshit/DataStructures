package suffix_array

import java.util.*

// O(n^2 * LogN) if we use O(N*LogN) for sort, and each string comparison takes O(N) rather than O(1) in sorting
class SuffixArraySlow(text: String) : SuffixArray(text) {

    class Suffix(private val text: String, val suffixIndex: Int) : Comparable<Suffix> {
        override fun compareTo(other: Suffix): Int = text.compareTo(other.text)

        override fun toString(): String = text
    }

    override fun buildSuffixArray() {
        // All Suffixes with original start indexes of suffix
        val suffixes = arrayOfNulls<Suffix>(textLength)
        for (index in 0 until textLength)
            suffixes[index] = Suffix(text.substring(index), index)

        // Sort suffix strings: O(N*N*LogN)
        Arrays.sort(suffixes)

        // Get only original suffix start indexes from sorted suffix string array
        for (index in 0 until textLength) {
            val suffix = suffixes[index]!!
            suffixArray[index] = suffix.suffixIndex
        }
    }

}