package stack

import array.DynamicArray

class StackUsingDynamicArray<T : Any> {

    private var top: Int = -1
    private var items: DynamicArray<T> = DynamicArray()
    var size: Int = 0
        private set

    fun isEmpty(): Boolean = size == 0

    fun peek(): T {
        if (isEmpty())
            throw IllegalArgumentException()

        return items.get(top)
    }

    fun pop(): T {
        if (isEmpty())
            throw IllegalArgumentException()

        val result = items.get(top)
        items.removeAt(top)
        top -= 1
        size -= 1
        return result
    }

    fun push(item: T) {
        top += 1
        size += 1
        items.add(item)
    }
}