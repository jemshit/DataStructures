package heap

import array.DynamicArray
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class DaryMinHeap<T : Comparable<T>> {

    private lateinit var items: DynamicArray<T>
    private val indexMap = mutableMapOf<T, MutableSet<Int>>()
    private var d: Int = 2

    constructor(
        d: Int = 2,
        initialCapacity: Int = 50,
        unsortedItems: Array<T> = arrayOfNulls<Comparable<T>>(0) as Array<T>
    ) {
        this.d = max(2, d)
        val capacity = max(0, initialCapacity)
        if (unsortedItems.isEmpty())
            this.items = DynamicArray<T>(capacity)
        else
            heapify(unsortedItems)
    }

    fun heapify(unsortedItems: Array<T>) {
        if (unsortedItems.isEmpty()) {
            items = DynamicArray<T>(50)
            return
        }
        items = DynamicArray<T>(unsortedItems.size * 2)
        for (element in unsortedItems) {
            items.add(element)

            // Fill indexMap as well
            if (indexMap.contains(element))
                indexMap.get(element)?.add(size() - 1)
            else
                indexMap.put(element, mutableSetOf(size() - 1))
        }

        // If parent nodes are at correct location, no need to sink/swim leaf nodes
        val lastLeafIndex = size() - 1
        val lastParentNodeIndex: Int = max(0, floor((lastLeafIndex - 1.0) / d).toInt())
        for (index in lastParentNodeIndex downTo 0)
            sink(index)
    }

    fun size(): Int = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    fun indexOf(item: T): Int {
        if (isEmpty())
            return -1

        // return last index in set (last one is at the end of tree)
        return indexMap.get(item)?.lastOrNull() ?: -1
    }

    fun contains(item: T): Boolean = indexOf(item) != -1

    fun clear() {
        items.clear()
        indexMap.clear()
    }

    fun peek(): T? {
        if (isEmpty())
            return null

        return items.get(0)
    }

    // O(logk^n)
    fun poll(): T? {
        if (isEmpty())
            return null

        return removeAt(0)
    }

    fun add(item: T) {
        items.add(item)

        // add to indexMap as well
        if (indexMap.contains(item))
            indexMap.get(item)?.add(size() - 1)
        else
            indexMap.put(item, mutableSetOf(size() - 1))

        swim(size() - 1)
    }

    // O(1) search + O(logk^n) removal
    fun remove(item: T): Boolean {
        val index = indexOf(item)
        if (index == -1)
            return false

        removeAt(index)
        return true
    }

    private fun swap(index: Int, withIndex: Int) {
        indexMapSwap(index, withIndex)
        val item = items.get(index)
        items.set(index, items.get(withIndex))
        items.set(withIndex, item)
    }

    private fun indexMapSwap(index: Int, withIndex: Int) {
        val item = items.get(index)
        val itemWith = items.get(withIndex)

        indexMap.get(item)?.remove(index)
        indexMap.get(itemWith)?.remove(withIndex)

        indexMap.get(item)?.add(withIndex)
        indexMap.get(itemWith)?.add(index)
    }

    // O(logk^n)
    private fun removeAt(index: Int): T? {
        val result = items.get(index)
        swap(index, size() - 1)
        indexMap.get(result)?.remove(size() - 1)
        items.removeAt(size() - 1)

        if (index < size() - 1) {
            // Try sink
            val newItem = items.get(index)
            sink(index)

            // If does not sink, try swim
            if (items.get(index).equals(newItem))
                swim(index)
        }

        return result
    }

    private fun swim(index: Int) {
        var childIndex = index
        var parentIndex: Int = floor((childIndex - 1.0) / d).toInt()

        while (parentIndex >= 0 && items.get(childIndex).compareTo(items.get(parentIndex)) < 0) {
            swap(childIndex, parentIndex)
            childIndex = parentIndex
            parentIndex = floor((childIndex - 1.0) / d).toInt()
        }
    }

    private fun sink(index: Int) {
        var sinkIndex = index
        var smallerChildIndex = smallerChildIndex(sinkIndex)
        while (smallerChildIndex != -1) {
            swap(smallerChildIndex, sinkIndex)
            sinkIndex = smallerChildIndex
            smallerChildIndex = smallerChildIndex(sinkIndex)
        }
    }

    private fun smallerChildIndex(parentNodeIndex: Int): Int {
        // if leaf node reached, no need to look for child
        val lastParentNodeIndex: Int = floor(((size() - 1) - 1.0) / d).toInt()
        if (parentNodeIndex > lastParentNodeIndex)
            return -1

        var smallerChildAt = -1
        var smallestValue = items.get(parentNodeIndex)
        var childIndex: Int = d * parentNodeIndex + 1
        while (childIndex <= min(size() - 1, d * parentNodeIndex + d)) {
            if (items.get(childIndex) < smallestValue) {
                smallestValue = items.get(childIndex)
                smallerChildAt = childIndex
            }
            childIndex += 1
        }

        return smallerChildAt
    }
}