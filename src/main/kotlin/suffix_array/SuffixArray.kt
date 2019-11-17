package suffix_array

abstract class SuffixArray {
    val text: String
    val textLength: Int
    val suffixArray: IntArray
    val lcpArray: IntArray // todo

    constructor(text: String) {
        this.text = text
        this.suffixArray = IntArray(text.length)
        this.lcpArray = IntArray(text.length)
        this.textLength = text.length

        this.buildSuffixArray()
    }

    protected abstract fun buildSuffixArray()

}