package unionfind

/**
 * Disjoint-set data structure using path compression and union by rank.
 *
 * Rank is a balancing heuristic: it is not necessarily the exact tree height after path compression.
 * Elements are the integers from `0` until [size].
 * Construction takes `O(n)`; find, union, connected, and componentSize
 * take `O(α(n))` amortized time, which is effectively constant time in practice.
 */
class UnionFindByRank(elementCount: Int) {

    private val parent: IntArray
    private val rank: IntArray

    // Used only for componentSize queries; union decisions use rank.
    private val componentSizes: IntArray

    val size: Int
        get() = parent.size

    var componentCount: Int = elementCount
        private set

    init {
        require(elementCount >= 0) { "elementCount must not be negative" }
        // Initially, every element is its own parent and therefore its own component.
        parent = IntArray(elementCount) { element -> element }
        // Singleton trees start at rank 0.
        rank = IntArray(elementCount)
        // One element in each initial component.
        componentSizes = IntArray(elementCount) { 1 }
    }

    /** Returns the representative of [element]'s component. */
    fun find(element: Int): Int {
        requireElement(element)

        var root = element
        while (root != parent[root]) {
            root = parent[root]
        }

        // Point every element on the traversed path directly at the root.
        var current = element
        while (current != root) {
            val next = parent[current]
            parent[current] = root
            current = next
        }

        return root
    }

    /** Returns true when [first] and [second] belong to the same component. */
    fun connected(first: Int, second: Int): Boolean {
        return find(first) == find(second)
    }

    /** Returns the number of elements in [element]'s component. */
    fun componentSize(element: Int): Int = componentSizes[find(element)]

    /**
     * Merges the components containing [first] and [second].
     *
     * Returns true if two components were merged, or false if they were already connected.
     */
    fun union(first: Int, second: Int): Boolean {
        var firstRoot = find(first)
        var secondRoot = find(second)
        if (firstRoot == secondRoot) return false

        // Attach the lower-rank tree below the higher-rank tree's root.
        if (rank[firstRoot] < rank[secondRoot]) {
            val temporary = firstRoot
            firstRoot = secondRoot
            secondRoot = temporary
        }

        // After the possible swap, firstRoot represents the higher-rank tree.
        parent[secondRoot] = firstRoot
        componentSizes[firstRoot] += componentSizes[secondRoot]
        componentSizes[secondRoot] = 0
        componentCount--

        if (rank[firstRoot] == rank[secondRoot]) {
            rank[firstRoot]++
        }
        return true
    }

    private fun requireElement(element: Int) {
        // This is an O(1) bounds check, not a search through the parent array.
        require(element in parent.indices) { "Unknown element: $element" }
    }
}
