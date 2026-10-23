package heap

import array.DynamicArray
import kotlin.math.min

private const val MIN_DEGREE = 2
private const val DEFAULT_DEGREE = MIN_DEGREE
private const val DEFAULT_INITIAL_CAPACITY = 16

/**
 * Array-backed d-ary min-heap.
 *
 * Shape invariant: the zero-based array represents a complete d-ary tree without node objects.
 * A node at index `i` has children from `d * i + 1` through `d * i + d`; when `i > 0`, its
 * parent is at `(i - 1) / d`.
 *
 * Order invariant: every parent is less than or equal to each child according to
 * [Comparable.compareTo], so [peek] returns a minimum item.
 */
class DaryMinHeap<T : Comparable<T>>(
    d: Int = DEFAULT_DEGREE,
    initialCapacity: Int = DEFAULT_INITIAL_CAPACITY
) {

    private var items: DynamicArray<T>
    private val indexMap = mutableMapOf<T, MutableSet<Int>>()
    private val d: Int

    init {
        require(d >= MIN_DEGREE) { "d must be >= $MIN_DEGREE, was $d" }
        this.d = d
        require(initialCapacity >= 0) {
            "initialCapacity must be >= 0, was $initialCapacity"
        }
        items = DynamicArray(initialCapacity)
    }

    /** Creates a heap using Floyd's bottom-up build-heap algorithm. */
    constructor(
        unsortedItems: Iterable<T>,
        d: Int = DEFAULT_DEGREE,
        initialCapacity: Int = DEFAULT_INITIAL_CAPACITY
    ) : this(d, initialCapacity) {
        heapify(unsortedItems)
    }

    /** Creates a heap using Floyd's bottom-up build-heap algorithm. */
    constructor(
        unsortedItems: Array<T>,
        d: Int = DEFAULT_DEGREE,
        initialCapacity: Int = DEFAULT_INITIAL_CAPACITY
    ) : this(d, initialCapacity) {
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
        items = DynamicArray(initialCapacity)
        indexMap.clear()

        for (item in unsortedItems) {
            items.add(item)
            addIndex(item, size - 1)
        }

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

    /** O(1) */
    val isEmpty: Boolean
        get() = items.isEmpty()

    /** Returns the index of an equal item in O(1) average time, or `-1` when absent. */
    fun indexOf(item: T): Int = indexMap[item]?.firstOrNull() ?: -1

    /** O(1) */
    fun contains(item: T): Boolean = item in indexMap

    /** O(n) */
    fun clear() {
        items.clear()
        indexMap.clear()
    }

    /** Returns a minimum item, or `null` when empty, in O(1) time. */
    fun peek(): T? = if (isEmpty) null else items.get(0)

    /** Adds [item] in O(log_d n) time. */
    fun add(item: T) {
        items.add(item)
        addIndex(item, size - 1)
        swim(size - 1)
    }

    /** Removes one equal item in O(log_d n) average time. */
    fun remove(item: T): Boolean {
        val index = indexOf(item)
        if (index == -1)
            return false

        removeAt(index)
        return true
    }

    /** Removes and returns a minimum item, or `null` when empty, in O(log_d n) time. */
    fun poll(): T? = if (isEmpty) null else removeAt(0)

    private fun less(item: T, other: T): Boolean = item.compareTo(other) < 0

    private fun parentIndexOf(childIndex: Int): Int {
        require(childIndex > 0) { "The root has no parent" }
        // childIndex is positive, so integer division gives floor((childIndex - 1) / d).
        return (childIndex - 1) / d
    }

    private fun firstChildIndexOf(parentIndex: Int): Int = d * parentIndex + 1

    private fun lastChildIndexOf(parentIndex: Int): Int = min(size - 1, d * parentIndex + d)

    private fun addIndex(item: T, index: Int) {
        indexMap.getOrPut(item) { mutableSetOf() }.add(index)
    }

    private fun removeIndex(item: T, index: Int) {
        val indexes = indexMap[item] ?: return
        indexes.remove(index)
        if (indexes.isEmpty())
            indexMap.remove(item)
    }

    /** Swaps two valid heap indexes in O(1) average time. */
    private fun swap(index: Int, otherIndex: Int) {
        check(index in 0 until size && otherIndex in 0 until size) {
            "Heap indexes must be in 0 until $size: $index, $otherIndex"
        }
        if (index == otherIndex)
            return

        val item = items.get(index)
        val otherItem = items.get(otherIndex)
        removeIndex(item, index)
        removeIndex(otherItem, otherIndex)
        addIndex(item, otherIndex)
        addIndex(otherItem, index)
        items.set(index, otherItem)
        items.set(otherIndex, item)
    }

    /** Removes an item at a valid index and repairs the heap in O(log_d n) time. */
    private fun removeAt(index: Int): T {
        check(index in 0 until size) { "Index: $index, Size: $size" }

        val result = items.get(index)
        val lastIndex = size - 1
        if (index == lastIndex) {
            removeIndex(result, lastIndex)
            items.removeAt(lastIndex)
            return result
        }

        swap(index, lastIndex)
        removeIndex(result, lastIndex)
        items.removeAt(lastIndex)

        if (index > 0 && less(items.get(index), items.get(parentIndexOf(index)))) {
            swim(index)
        } else {
            sink(index)
        }
        return result
    }

    /** Moves an item toward the root until the heap property holds, in O(log_d n) time. */
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

    /** Moves an item toward the leaves until the heap property holds, in O(log_d n) time. */
    private fun sink(index: Int) {
        var currentIndex = index

        while (true) {
            val childIndex = smallerChildIndexIfNeeded(currentIndex)
            if (childIndex == -1)
                return

            swap(currentIndex, childIndex)
            currentIndex = childIndex
        }
    }

    private fun smallerChildIndexIfNeeded(parentIndex: Int): Int {
        val firstChildIndex = firstChildIndexOf(parentIndex)
        if (firstChildIndex >= size)
            return -1

        val lastChildIndex = lastChildIndexOf(parentIndex)
        var smallestChildIndex = firstChildIndex
        for (childIndex in firstChildIndex + 1..lastChildIndex) {
            if (less(items.get(childIndex), items.get(smallestChildIndex)))
                smallestChildIndex = childIndex
        }

        // Strict < preserves the heap property when values are equal and avoids swapping equals.
        return if (less(items.get(smallestChildIndex), items.get(parentIndex))) {
            smallestChildIndex
        } else {
            -1
        }
    }
}
