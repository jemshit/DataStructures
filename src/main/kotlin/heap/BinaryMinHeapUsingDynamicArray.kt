package heap

import array.DynamicArray
import kotlin.math.floor
import kotlin.math.max

// Complete Heap
class BinaryMinHeapUsingDynamicArray<T : Comparable<T>> {

    private lateinit var items: DynamicArray<T>

    constructor(initialCapacity: Int = 50) {
        if (initialCapacity < 0)
            throw IllegalArgumentException()

        this.items = DynamicArray<T>(initialCapacity)
    }

    constructor(unsortedItems: Array<T>) {
        if (unsortedItems.isEmpty()) {
            this.items = DynamicArray<T>(50)
        } else {
            heapify(unsortedItems)
        }
    }

    // if each item added using add, it will take O(logn) for each item, total O(n*logn)
    // or leaf nodes do not need heapify, so heapify until parent node of last leaf
    // if you heapify parent, children are heapified anyway. Optimized to O(n)?
    fun heapify(unsortedItems: Array<T>) {
        this.items = DynamicArray<T>(unsortedItems.size * 2)
        for (element in unsortedItems) {
            this.items.add(element)
        }

        // try to sink all parents. from last parent to root.
        val lastChildIndex = size() - 1
        val lastParentIndex = max(0, (lastChildIndex - 1) / 2)
        for (index in lastParentIndex downTo 0) {
            sink(index)
        }
    }

    fun size(): Int = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    fun contains(item: T): Boolean = items.contains(item)

    fun clear() = items.clear()

    // O(1)
    fun peek(): T? {
        if (isEmpty())
            return null

        return items.get(0)
    }

    // O(log(n))
    fun add(item: T) {
        items.add(item)
        swim(size() - 1)
    }

    // O(n) for linear search + O(logn) for removing.
    // Can be optimized to O(1)search+O(logn)removal using Map<T,Set<Int>> (T:setOf(indexes T exist)
    fun remove(item: T): Boolean {
        val index = items.indexOf(item)
        if (index == -1)
            return false

        removeAt(index)
        return true
    }

    // O(logn)
    fun poll(): T? {
        if (isEmpty())
            return null

        return removeAt(0)
    }

    private fun less(item: T, compareTo: T): Boolean {
        return item.compareTo(compareTo) < 0
    }

    private fun swap(index: Int, pairIndex: Int) {
        if (index >= size() || pairIndex >= size())
            throw IllegalAccessException()

        val temp = items.get(index)
        items.set(index, items.get(pairIndex))
        items.set(pairIndex, temp)
    }

    // O(log(n))
    private fun removeAt(index: Int): T? {
        if (isEmpty() || index < 0)
            return null

        val result = items.get(index)
        swap(index, size() - 1)
        items.removeAt(size() - 1)

        // last element was removed
        if (index == size())
            return result
        if (index == 0) {
            sink(index)
            return result
        }

        val parentIndex = floor((index - 1) / 2.0).toInt()
        val parent: T? = if (parentIndex < size()) items.get(parentIndex) else null
        val leftChildIndex = 2 * index + 1
        val leftChild: T? = if (leftChildIndex < size()) items.get(leftChildIndex) else null
        val rightChildIndex = 2 * index + 2
        val rightChild: T? = if (rightChildIndex < size()) items.get(rightChildIndex) else null

        if (parent != null && less(items.get(index), parent)) {
            swim(index)
        } else if (leftChild != null && less(leftChild, items.get(index))) {
            sink(index)
        } else if (rightChild != null && less(rightChild, items.get(index))) {
            sink(index)
        }

        return result
    }

    // O(log(n))
    private fun swim(index: Int) {
        if (index == 0)
            return

        val parentIndex = floor((index - 1) / 2.0).toInt()
        val parent = items.get(parentIndex)
        if (less(items.get(index), parent)) {
            swap(index, parentIndex)
            swim(parentIndex)
        }
    }

    // O(logn)
    private fun sink(index: Int) {
        if (index >= (size() - 1))
            return

        val leftChildIndex = 2 * index + 1
        val leftChild: T? = if (leftChildIndex < size()) items.get(leftChildIndex) else null
        val rightChildIndex = 2 * index + 2
        val rightChild: T? = if (rightChildIndex < size()) items.get(rightChildIndex) else null

        // find smallest child
        var smallestChildIndex = leftChildIndex
        if (rightChildIndex < size() && less(rightChild!!, leftChild!!))
            smallestChildIndex = rightChildIndex

        // If no child exists or item is smaller than both children
        if (leftChildIndex >= size() || less(items.get(index), items.get(smallestChildIndex)))
            return

        swap(index, smallestChildIndex)
        sink(smallestChildIndex)
    }
}