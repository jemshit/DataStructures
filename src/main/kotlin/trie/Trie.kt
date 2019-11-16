package trie

class Trie(private val alphabetSize: Int = 26) {

    private var rootNode: TrieNode = TrieNode()
    private var intCharMap: HashMap<Int, Char> = HashMap(alphabetSize)

    inner class TrieNode {
        var isEndOfWord = false
        val children: Array<TrieNode?> = arrayOfNulls(alphabetSize)

        fun isEmpty(): Boolean {
            for (child in children)
                if (child != null)
                    return false

            return true
        }
    }

    fun insert(key: String) {
        var nextNode: TrieNode = rootNode
        for (level in key.indices) {
            val indexOfChar = key[level] - 'a'

            if (nextNode.children[indexOfChar] == null)
                nextNode.children[indexOfChar] = TrieNode()

            nextNode = nextNode.children[indexOfChar]!!
        }

        nextNode.isEndOfWord = true
    }

    fun search(key: String): Boolean {
        var nextNode: TrieNode? = rootNode
        for (level in key.indices) {
            val indexOfChar = key[level] - 'a'

            if (nextNode!!.children[indexOfChar] == null)
                return false

            nextNode = nextNode.children[indexOfChar]!!
        }

        return nextNode != null && nextNode.isEndOfWord
    }

    fun getAll(node: TrieNode = rootNode, wordPrefix: String = ""): List<String> {
        val result = mutableListOf<String>()
        if (node.isEndOfWord)
            result.add(wordPrefix)

        for (charIndex in node.children.indices) {
            if (node.children[charIndex] != null) {
                val charNode: Char = (charIndex + 'a'.toInt()).toChar()
                val updatedWordPrefix = wordPrefix + charNode

                result.addAll(getAll(node.children[charIndex]!!, updatedWordPrefix))
            }
        }

        return result
    }

    private fun removeNode(node: TrieNode?, key: String, level: Int): TrieNode? {
        // (1) key does not exist
        if (node == null)
            return null

        // found key
        if (level == key.length) {
            // (3)
            node.isEndOfWord = false
            // (2)(4) if no children, delete this node (until beginning or endOfWord of shorter word)
            if (node.isEmpty() && !node.isEndOfWord) return null else return node
        }

        // if not last char, recursively call for next char in key
        val indexOfChar = key[level] - 'a'
        node.children[indexOfChar] = removeNode(node.children[indexOfChar], key, level + 1)

        // if node does not have any children (its children got deleted above) and it is not end of another word
        if (node.isEmpty() && !node.isEndOfWord) return null else return node
    }

    fun remove(key: String) {
        if (key.isBlank()) return
        rootNode = removeNode(rootNode, key, 0) ?: TrieNode()
    }
}