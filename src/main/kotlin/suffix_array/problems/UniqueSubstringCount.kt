package suffix_array.problems

import suffix_array.SuffixArray

fun SuffixArray.findUniqueSubstringCount(): Int {
    // total number of substrings: n*(n+1)/2
    val totalSubstringCount = lcpArray.size * (lcpArray.size + 1) / 2

    // duplicate substring count = sum of LCP items
    val duplicateSubstringCount = lcpArray.sum()

    // unique substring count
    return totalSubstringCount - duplicateSubstringCount
}