package suffix_array

// O(M * LogN), M is pattern length, LogN is for Binary Search
fun SuffixArray.searchPattern(pattern: String): Int? {
    val patternLength = pattern.length

    var leftIndex = 0
    var rightIndex = textLength - 1
    while (leftIndex <= rightIndex) {
        val middleIndex = (rightIndex - leftIndex + 1) / 2 + leftIndex

        var suffix = text.substring(suffixArray[middleIndex])
        if (suffix.length > patternLength)
            suffix = suffix.substring(0, patternLength)
        val compareResult = pattern.compareTo(suffix)

        if (compareResult == 0)
            return suffixArray[middleIndex]
        else if (compareResult < 0)
            rightIndex = middleIndex - 1
        else
            leftIndex = middleIndex + 1
    }
    return null
}