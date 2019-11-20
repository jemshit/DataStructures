package suffix_array.problems

import suffix_array.SuffixArray
import suffix_array.SuffixArrayMed

// O(N^2): Build DP matrix to find repeating suffixes (non overlapping) for each char
fun longestRepeatingNonOverlappingSubstring(text: String): String {
    val suffixLen = Array<IntArray>(text.length + 1) {
        IntArray(text.length + 1) { 0 }
    }

    var maxLen = 0
    var maxSuffixEnd = 0

    for (leftPtr in 1..text.length) {
        // second pointer is like traversing another string (starts at left+1)
        for (rightPtr in (leftPtr + 1)..text.length) {
            // if suffixes end with same char
            if (text[leftPtr - 1] == text[rightPtr - 1]
                // to prevent overlapping suffixes
                && rightPtr - leftPtr > suffixLen[leftPtr - 1][rightPtr - 1]
            ) {
                // increment suffix length (add prev chars into account)
                suffixLen[leftPtr][rightPtr] = 1 + suffixLen[leftPtr - 1][rightPtr - 1]
                if (suffixLen[leftPtr][rightPtr] > maxLen) {
                    maxLen = suffixLen[leftPtr][rightPtr]
                    maxSuffixEnd = leftPtr
                }
            } else {
                suffixLen[leftPtr][rightPtr] = 0
            }
        }
    }

    var result = ""
    val maxSuffixStart = maxSuffixEnd - maxLen + 1
    for (counter in maxSuffixEnd downTo maxSuffixStart) {
        result = text[counter - 1] + result
    }
    return result
}

// O(N*LogN) or O(N) depending on how to build LCP array. BUT this can only give overlapping-repeating substring
fun longestRepeatingOverlappingSubstringLCP(text: String): String {
    val suffixArray: SuffixArray = SuffixArrayMed(text)

    var maxLcpValue = 0
    var maxLcpIndex = -1
    suffixArray.lcpArray.forEachIndexed { lcpIndex, lcpValue ->
        if (lcpValue > maxLcpValue) {
            maxLcpValue = lcpValue
            maxLcpIndex = lcpIndex
        }
    }
    if (maxLcpValue == 0)
        return ""
    else {
        var result = ""
        val maxSuffixStart = suffixArray.suffixArray[maxLcpIndex]
        val maxSuffixEnd = maxSuffixStart + maxLcpValue - 1
        for (counter in maxSuffixStart..maxSuffixEnd) {
            result = result + text[counter]
        }
        return result
    }
}
