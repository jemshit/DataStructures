package heap

import array.DynamicArray

private const val DEFAULT_INITIAL_CAPACITY = 16

/**
 * Array-backed binary min-heap.
 *
 * Shape invariant: the zero-based array represents a complete binary tree without node objects.
 * A node at index `i` has children at `2i + 1` and `2i + 2`; when `i > 0`, its parent is at
 * `(i - 1) / 2`.
 *
 * Order invariant: every parent is less than or equal to each child according to
 * [Comparable.compareTo], so [peek] returns a minimum item.
 */
class BinaryMinHeap<T : Comparable<T>>(initialCapacity: Int = DEFAULT_INITIAL_CAPACITY) {

    private var items: DynamicArray<T>

    init {
        require(initialCapacity >= 0) {
            "initialCapacity must be >= 0, was $initialCapacity"
        }

        this.items = DynamicArray<T>(initialCapacity)
    }

    /** Creates a heap using Floyd's bottom-up build-heap algorithm. */
    constructor(unsortedItems: Iterable<T>) : this() {
        heapify(unsortedItems)
    }

    /** Creates a heap using Floyd's bottom-up build-heap algorithm. */
    constructor(unsortedItems: Array<T>) : this() {
        // Kotlin Array does not implement Iterable.
        heapify(unsortedItems.asIterable())
    }

    /**
     * Discards the current contents and rebuilds this heap with Floyd's bottom-up build-heap
     * algorithm in Θ(n) time.
     *
     * Here, "heapify" means building the complete heap; [sink] repairs one node by moving it
     * down. Leaves already satisfy the heap property, so only internal nodes are sunk from the
     * last parent to the root. Nodes at height `h` become exponentially less numerous, making
     * the total work Θ(n), rather than Θ(n log n) for inserting every item separately.
     */
    fun heapify(unsortedItems: Iterable<T>) {
        val initialCapacity = if (unsortedItems is Collection<*>) {
            maxOf(unsortedItems.size, DEFAULT_INITIAL_CAPACITY)
        } else {
            DEFAULT_INITIAL_CAPACITY
        }
        this.items = DynamicArray<T>(initialCapacity)
        for (element in unsortedItems) {
            this.items.add(element)
        }

        // all parent nodes are in the first half of the array
        if (size < 2)
            return

        val lastLeafIndex = size - 1
        val lastParentIndex = parentIndexOf(lastLeafIndex)
        for (index in lastParentIndex downTo 0) {
            sink(index)
        }
    }

    /** Convenience overload because Kotlin [Array] does not implement [Iterable]. */
    fun heapify(unsortedItems: Array<T>) {
        heapify(unsortedItems.asIterable())
    }

    /** O(1) */
    val size: Int
        get() = items.size

    /** O(1)  */
    val isEmpty: Boolean
        get() = items.isEmpty()

    /** O(n) */
    fun contains(item: T): Boolean = items.contains(item)

    /** O(n) */
    fun clear() = items.clear()

    /** Returns a minimum item, or `null` when empty, in O(1) time. */
    fun peek(): T? {
        if (isEmpty)
            return null

        return items.get(0)
    }

    /** Adds [item] in O(log n) time. */
    fun add(item: T) {
        items.add(item)
        swim(size - 1)
    }

    /** Removes one equal item in O(n) time: linear search plus O(log n) heap repair. */
    fun remove(item: T): Boolean {
        val index = items.indexOf(item)
        if (index == -1)
            return false

        removeAt(index)
        return true
    }

    /** Removes and returns a minimum item, or `null` when empty, in O(log n) time. */
    fun poll(): T? {
        if (isEmpty)
            return null

        return removeAt(0)
    }

    private fun less(item: T, other: T): Boolean {
        return item.compareTo(other) < 0
    }

    private fun leftChildIndexOf(nodeIndex: Int): Int = 2 * nodeIndex + 1

    private fun rightChildIndexOf(nodeIndex: Int): Int = 2 * nodeIndex + 2

    private fun parentIndexOf(childIndex: Int): Int {
        require(childIndex > 0) { "The root has no parent" }
        // childIndex is positive, so integer division gives floor((childIndex - 1) / 2).
        return (childIndex - 1) / 2
    }

    /** Swaps two valid heap indexes in O(1) time. */
    private fun swap(index: Int, otherIndex: Int) {
        check(index in 0 until size && otherIndex in 0 until size) {
            "Heap indexes must be in 0 until $size: $index, $otherIndex"
        }

        val temp = items.get(index)
        items.set(index, items.get(otherIndex))
        items.set(otherIndex, temp)
    }

    /** Removes an item at a valid index and repairs the heap in O(log n) time. */
    private fun removeAt(index: Int): T {
        check(index in 0 until size) { "Index: $index, Size: $size" }

        val result = items.get(index)
        val lastIndex = size - 1

        // last item does not need to be swapped or repaired.
        if (index == lastIndex) {
            items.removeAt(lastIndex)
            return result
        }

        swap(index, lastIndex)
        items.removeAt(lastIndex)

        if (index > 0) {
            val parentIndex = parentIndexOf(index)
            if (less(items.get(index), items.get(parentIndex))) {
                swim(index)
                return result
            }
        }

        sink(index)

        return result
    }

    /** Moves an item toward the root until the heap property holds, in O(log n) time. */
    private fun swim(index: Int) {
        var currentIndex = index

        while (currentIndex > 0) {
            val parentIndex = parentIndexOf(currentIndex)
            if (!less(items.get(currentIndex), items.get(parentIndex)))
                return

            swap(currentIndex, parentIndex)
            currentIndex = parentIndex
        }
    }

    /** Moves an item toward the leaves until the heap property holds, in O(log n) time. */
    private fun sink(index: Int) {
        var currentIndex = index

        while (true) {
            val leftChildIndex = leftChildIndexOf(currentIndex)
            // A node without a left child has no children in a complete binary heap.
            if (leftChildIndex >= size)
                return

            val rightChildIndex = rightChildIndexOf(currentIndex)
            // Start with the left-child index, then use the right-child index only when it is smaller.
            var smallestChildIndex = leftChildIndex
            if (rightChildIndex < size &&
                less(items.get(rightChildIndex), items.get(leftChildIndex))
            ) {
                smallestChildIndex = rightChildIndex
            }

            // <= is correct because equal values satisfy the min-heap property; it also avoids swapping equals.
            if (items.get(currentIndex).compareTo(items.get(smallestChildIndex)) <= 0)
                return

            swap(currentIndex, smallestChildIndex)
            currentIndex = smallestChildIndex
        }
    }
}
