package suffix_array.problems

import suffix_array.SuffixArray

// O(M * LogN), M is pattern length, LogN is for Binary Search
fun SuffixArray.searchPattern(pattern: String): Int? {
    var leftIndex = 0
    var rightIndex = textLength - 1
    while (leftIndex <= rightIndex) {
        val middleIndex = leftIndex + (rightIndex - leftIndex + 1) / 2

        // does not search in text, but in suffixes
        var suffixAtMiddle = text.substring(suffixArray[middleIndex])
        if (suffixAtMiddle.length > pattern.length)
            suffixAtMiddle = suffixAtMiddle.substring(0, pattern.length)
        val compareResult = pattern.compareTo(suffixAtMiddle)

        if (compareResult == 0)
            return suffixArray[middleIndex]
        else if (compareResult < 0)
            rightIndex = middleIndex - 1
        else
            leftIndex = middleIndex + 1
    }
    return null
}