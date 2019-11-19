package suffix_array.problems

import kotlin.math.max

// O(M*N). Go through each character and find how many chars are common until indexes.
// [i][j] means number of common sequential chars until i(excluding) and j(excluding) character of x, y
// if there is non common chars in middle, it will restart. Sequential common control is done through current=[prevX][prevY]+1
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
    val commonSuffixLength: Array<IntArray> = Array<IntArray>(text1.length + 1) { IntArray(text2.length) + 1 }
    var maxCommonSubstring = 0
    var maxIndex1 = 0
    var maxIndex2 = 0

    for (t1Until in 0..text1.length) {
        for (t2Until in 0..text2.length) {
            if (t1Until == 0 || t2Until == 0)
                commonSuffixLength[t1Until][t2Until] = 0
            else if (text1[t1Until - 1] == text2[t2Until - 1]) {
                // if t1Until == t2Until (exclusive)
                commonSuffixLength[t1Until][t2Until] = commonSuffixLength[t1Until - 1][t2Until - 1] + 1
                if (commonSuffixLength[t1Until][t2Until] > maxCommonSubstring) {
                    maxCommonSubstring = commonSuffixLength[t1Until][t2Until]
                    maxIndex1 = t1Until
                    maxIndex2 = t2Until
                }
            } else {
                // not equal
                commonSuffixLength[t1Until][t2Until] = 0
            }
        }
    }

    // Construct common substring
    var longestCommon = ""
    while (commonSuffixLength[maxIndex1][maxIndex2] != 0) {
        longestCommon = text1[maxIndex1 - 1] + longestCommon
        maxIndex1 -= 1
        maxIndex2 -= 1
    }

    return longestCommon
}
