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
            return@Array Suffix(index, text[index] - '$', 0)
        }
        for (index in 0 until textLength)
            if (index + 1 < textLength)
                suffixes[index].nextRank = suffixes[index + 1].rank
            else
                suffixes[index].nextRank = -1

        // Sort suffix strings: O(N*LogN). All suffixes are sorted according to first 2 chars.
        Arrays.sort(suffixes)

        // This array is needed to get the index in suffixes[] from original index. Used to get nextRank
        val origIndexToSuffixPos = IntArray(textLength)
        var length = 4
        // O(LogN): Now sort suffixes according to first 4 chars, then first 8 and so on
        while (length < 2 * textLength) {
            // Update ranks (starts with 0)
            var prevRank = suffixes[0].rank // used to compare when operating on next suffix
            var newRank = 0
            suffixes[0].rank = newRank
            origIndexToSuffixPos[suffixes[0].index] = 0
            for (charIndex in 1 until textLength) {
                // If rank, nextRank are same of previous suffix, assign the same new rank to this suffix
                val sameAsPrevSuffix = suffixes[charIndex].rank == prevRank
                        && suffixes[charIndex].nextRank == suffixes[charIndex - 1].nextRank

                prevRank = suffixes[charIndex].rank
                if (!sameAsPrevSuffix) newRank += 1
                suffixes[charIndex].rank = newRank
                origIndexToSuffixPos[suffixes[charIndex].index] = charIndex
            }

            // Update nextRanks
            for (charIndex in 0 until textLength) {
                val nextCharIndex = suffixes[charIndex].index + length / 2
                // For every suffix, store nextRank as rank of suffix whose index = (suffixes[i].index + length / 2)
                if (nextCharIndex < textLength)
                    suffixes[charIndex].nextRank = suffixes[origIndexToSuffixPos[nextCharIndex]].rank
                else
                    suffixes[charIndex].nextRank = -1
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