package suffix_array

import java.util.*

// O(N*LogN*LogN): sort O(N*LogN) is called O(LogN) times in loop
// If Radix Sort is used, then this becomes O(N*LogN)
class SuffixArrayMed(text: String) : SuffixArray(text) {

    // Does not store text
    class Suffix(var index: Int, var rank: Int, var nextRank: Int) : Comparable<Suffix> {

        override fun compareTo(other: Suffix): Int {
            return if (rank != other.rank)
                rank.compareTo(other.rank)
            else
                nextRank.compareTo(other.nextRank)
        }
    }

    // https://www.geeksforgeeks.org/suffix-array-set-2-a-nlognlogn-algorithm/
    override fun buildSuffixArray() {
        // All Suffixes with original start indexes of suffix + rank(a=0,b=1..), nextRank (same for second character)
        val suffixes = Array<Suffix>(textLength) { index ->
            val rank = text[index] - '$'
            return@Array Suffix(index, rank, 0)
        }
        for (index in 0 until textLength)
            suffixes[index].nextRank = if (index + 1 < textLength)
                suffixes[index + 1].rank
            else
                -1

        // Sort suffix strings: O(N*LogN). All suffixes are sorted according to first 2 chars.
        Arrays.sort(suffixes)

        // This array is needed to get the index in suffixes[] from original index. Used to get nextRank
        val indexToSuffixPosition = IntArray(textLength)
        var length = 4
        // O(LogN): Now sort suffixes according to first 4 chars, then first 8 and so on
        while (length < 2 * textLength) {
            // Update ranks (starts with 0)
            var rank = 0
            suffixes[0].rank = rank
            indexToSuffixPosition[suffixes[0].index] = 0
            var prevRank = suffixes[0].rank // used to compare when operating on next suffix
            for (charIndex in 1 until textLength) {
                // If rank, nextRank are same of previous suffix, assign the same new rank to this suffix
                if (suffixes[charIndex].rank == prevRank && suffixes[charIndex].nextRank == suffixes[charIndex - 1].nextRank) {
                    prevRank = suffixes[charIndex].rank
                    suffixes[charIndex].rank = rank
                } else {
                    // Otherwise increment rank and assign
                    prevRank = suffixes[charIndex].rank
                    rank += 1
                    suffixes[charIndex].rank = rank
                }
                indexToSuffixPosition[suffixes[charIndex].index] = charIndex
            }

            // Update nextRanks
            for (charIndex in 0 until textLength) {
                val nextCharIndex = suffixes[charIndex].index + length / 2
                // For every suffix, store nextRank as rank of suffix whose index = (suffixes[i].index + length / 2)
                suffixes[charIndex].nextRank = if (nextCharIndex < textLength)
                    suffixes[indexToSuffixPosition[nextCharIndex]].rank
                else
                    -1
            }

            // O(N*LogN): Sort the suffixes according to first k characters
            Arrays.sort(suffixes)

            length *= 2
        }

        // Get only original suffix start indexes from sorted suffix string array
        for (index in 0 until textLength)
            suffixArray[index] = suffixes[index].index

    }

}