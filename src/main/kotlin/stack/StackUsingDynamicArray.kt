package stack

import array.DynamicArray

class StackUsingDynamicArray<T : Any> {

    // top = -1 means empty, otherwise it is the index of the top item
    private var top: Int = -1

    private val items = DynamicArray<T>()

    val size: Int
        get() = top + 1

    fun isEmpty(): Boolean = top == -1

    fun peek(): T {
        return items.get(top)
    }

    fun pop(): T {
        val result = items.removeAt(top)
        top -= 1
        return result
    }

    fun push(item: T) {
        top += 1
        items.add(item)
    }
}