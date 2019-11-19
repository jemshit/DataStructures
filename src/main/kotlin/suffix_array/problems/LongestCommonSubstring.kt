package suffix_array.problems

import kotlin.math.max

// O(M*N). Go through each character and find how many chars are common until indexes.
// [i][j] contains longest common suffix (from end) until i and j (both exclusive).
// Sequential common control is done through current=[prevX][prevY]+1
fun longestCommonSubstringLength(text1: String, text2: String): Int {
    // [0][-] and [-][0] are meaningless, because this matrix stores length until i,j. Until 0 doesn't make sense
    val commonLengthUntil: Array<IntArray> = Array<IntArray>(text1.length + 1) { IntArray(text2.length) + 1 }
    var maxCommonSubstring = 0

    for (t1Until in 0..text1.length) {
        for (t2Until in 0..text2.length) {
            if (t1Until == 0 || t2Until == 0)
                commonLengthUntil[t1Until][t2Until] = 0
            else if (text1[t1Until - 1] == text2[t2Until - 1]) {
                // if t1Until == t2Until (exclusive)
                commonLengthUntil[t1Until][t2Until] = commonLengthUntil[t1Until - 1][t2Until - 1] + 1
                maxCommonSubstring = max(maxCommonSubstring, commonLengthUntil[t1Until][t2Until])
            } else {
                // not equal
                commonLengthUntil[t1Until][t2Until] = 0
            }
        }
    }

    return maxCommonSubstring
}

// O(M*N). Above algorithm with actual string result
// https://www.geeksforgeeks.org/print-longest-common-substring/
fun longestCommonSubstring(text1: String, text2: String): String {
    // [0][-] and [-][0] are meaningless, because this matrix stores length until i,j. Until 0 doesn't make sense
    val commonSuffixLen: Array<IntArray> = Array<IntArray>(text1.length + 1) { IntArray(text2.length) + 1 }
    var maxLen = 0
    var maxLenIndex1 = 0
    var maxLenIndex2 = 0

    for (t1 in 0..text1.length) {
        for (t2 in 0..text2.length) {
            if (t1 == 0 || t2 == 0)
                commonSuffixLen[t1][t2] = 0
            else if (text1[t1 - 1] == text2[t2 - 1]) {
                // if t1Until == t2Until (exclusive)
                commonSuffixLen[t1][t2] = commonSuffixLen[t1 - 1][t2 - 1] + 1
                if (commonSuffixLen[t1][t2] > maxLen) {
                    maxLen = commonSuffixLen[t1][t2]
                    maxLenIndex1 = t1
                    maxLenIndex2 = t2
                }
            } else {
                // not equal
                commonSuffixLen[t1][t2] = 0
            }
        }
    }

    // Construct common substring
    var longestCommon = ""
    while (commonSuffixLen[maxLenIndex1][maxLenIndex2] != 0) {
        longestCommon = text1[maxLenIndex1 - 1] + longestCommon
        maxLenIndex1 -= 1
        maxLenIndex2 -= 1
    }

    return longestCommon
}
