package disjoint_set

class DisjointSetUnionByRank {
    private var totalSize: Int = 0
    private var subsetCount: Int = 0
    private var subsetSizes: IntArray
    private var parents: IntArray
    private var ranks: IntArray

    // O(n)
    constructor(size: Int) {
        if (size <= 0)
            throw IllegalArgumentException()

        this.totalSize = size
        this.subsetCount = size
        this.parents = IntArray(size)
        this.subsetSizes = IntArray(size)
        this.ranks = IntArray(size)
        for (node in 0 until size) {
            this.parents[node] = node
            this.subsetSizes[node] = 1
            this.ranks[node] = 0
        }
    }

    fun size(): Int = totalSize

    fun subsetCount(): Int = subsetCount

    // a(n)
    fun subsetSize(node: Int): Int {
        val root = find(node)
        return subsetSizes[root]
    }

    // a(n)
    fun connected(node1: Int, node2: Int): Boolean {
        return find(node1) == find(node2)
    }

    // a(n)
    fun find(findNode: Int): Int {
        if (findNode >= totalSize)
            throw IllegalAccessException()

        var node = findNode
        while (node != parents[node]) {
            node = parents[node]
        }
        val root = node

        // Path compression
        node = findNode
        while (node != root) {
            val nextNode = parents[node]
            parents[node] = root
            node = nextNode
        }

        return root
    }

    // a(n)
    fun union(node1: Int, node2: Int) {
        if (node1 >= totalSize || node2 >= totalSize)
            throw IllegalAccessException()

        var root1 = find(node1)
        var root2 = find(node2)

        if (root1 == root2)
            return

        // make sure root1 is bigger, swap if necessary
        if (ranks[root2] > ranks[root1]) {
            val temp = root2
            root2 = root1
            root1 = temp
        }

        // union by rank
        parents[root2] = root1
        subsetSizes[root1] += subsetSizes[root2]
        subsetSizes[root2] = 0
        subsetCount -= 1

        if (ranks[root2] == ranks[root1])
            ranks[root1] += 1
    }

}