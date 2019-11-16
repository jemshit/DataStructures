package binary_index_tree

class BinaryIndexTree {

    private var tree: Array<Long>

    constructor(size: Int) {
        if (size <= 0) throw IllegalArgumentException()

        tree = Array<Long>(size + 1) { 0L }
    }

    constructor(items: LongArray) {
        if (items.isEmpty()) throw IllegalArgumentException()

        // index 0 will be 0 and dummy. Used as stop condition. In BIT, position always starts at 1!
        tree = Array(items.size + 1) { index ->
            if (index == 0) 0L else items[index - 1]
        }

        // O(N) in-place construction
        for (childPosition in 1..items.size) {
            val parentPosition = childPosition + lsb(childPosition)
            if (parentPosition < tree.size) {
                tree[parentPosition] += tree[childPosition]
            }
        }
    }

    private fun lsb(index: Int): Int {
        return index and (-index)
    }

    // O(logN) for sum [1-rightIndex] inclusive
    private fun prefixSum(rightIndex: Int): Long {
        var sum = tree[rightIndex]
        var nextIndex = rightIndex - lsb(rightIndex)
        while (nextIndex > 0) {
            sum += tree[nextIndex]
            nextIndex = nextIndex - lsb(nextIndex)
        }

        return sum
    }

    // O(logN) for sum [leftIndex-rightIndex] inclusive
    fun rangeSum(leftIndex: Int, rightIndex: Int): Long {
        if (rightIndex < leftIndex) throw IllegalArgumentException()
        if (rightIndex < 1 || leftIndex < 1) throw IllegalArgumentException()

        return prefixSum(rightIndex) - prefixSum(leftIndex - 1)
    }

    // O(logN). Returns original value in staticArray at index+1
    fun get(index: Int): Long {
        // sum[index] - sum[index-1] = value[index]
        return rangeSum(index, index)
    }

    // O(logN), index 1 is start
    fun add(index: Int, value: Long) {
        if (index < 1 || index > tree.size - 1) throw IllegalArgumentException()

        var parentIndex = index
        while (parentIndex < tree.size) {
            tree[parentIndex] += value
            parentIndex = parentIndex + lsb(parentIndex)
        }
    }

    // O(logN), index 1 is start
    fun update(index: Int, value: Long) {
        if (index < 1 || index > tree.size - 1) throw IllegalArgumentException()

        add(index, value - get(index))
    }
}