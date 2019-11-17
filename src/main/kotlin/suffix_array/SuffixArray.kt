package suffix_array

abstract class SuffixArray {
    val text: String
    val textLength: Int
    val suffixArray: IntArray
    val lcpArray: IntArray

    constructor(text: String) {
        this.text = text
        this.suffixArray = IntArray(text.length)
        this.lcpArray = IntArray(text.length)
        this.textLength = text.length

        this.buildSuffixArray()
        this.buildLcpArrayKasai()
    }

    protected abstract fun buildSuffixArray()

    // My implementation of Kasai's algorithm, O(N).
    private fun buildLcpArrayKasai() {
        var lcpLength = 0
        for (lcpIndex in 1 until textLength) {
            val prevSuffixStart = suffixArray[lcpIndex - 1]
            val currentSuffixStart = suffixArray[lcpIndex]

            while (currentSuffixStart + lcpLength < textLength
                && prevSuffixStart + lcpLength < textLength
                && text[currentSuffixStart + lcpLength] == text[prevSuffixStart + lcpLength]
            ) {
                lcpLength += 1
            }

            lcpArray[lcpIndex] = lcpLength

            lcpLength = 0
        }
    }

    // Kasai's algorithm, don't know why everybody implements in this crazy complex way
    private fun buildLcpArrayKasai2() {
        val invSuffixArray = IntArray(textLength)
        // if suffixArr[0] is 5, invSuff[5] would store 0
        for (i in 0 until textLength)
            invSuffixArray[suffixArray[i]] = i

        var lcpLength = 0
        for (nextSufIndex in 0 until textLength) {
            if (invSuffixArray[nextSufIndex] > 0) {
                val sufIndex = suffixArray[invSuffixArray[nextSufIndex] - 1]
                while (nextSufIndex + lcpLength < textLength
                    && sufIndex + lcpLength < textLength
                    && text[nextSufIndex + lcpLength] == text[sufIndex + lcpLength]
                ) {
                    lcpLength += 1
                }

                lcpArray[invSuffixArray[nextSufIndex]] = lcpLength

                if (lcpLength > 0)
                    lcpLength -= 1
            } else
                lcpLength = 0
        }
    }
}