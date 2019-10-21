package stack

class StackUsingStaticArray<T : Any> {

    private var top: Int = -1
    private var items: Array<T>
    var size: Int = 0
        private set
    private var capacity: Int = 100

    constructor(capacity: Int = 100) {
        if (capacity <= 0)
            throw IllegalArgumentException()

        this.capacity = capacity
        items = arrayOfNulls<Any>(capacity) as Array<T>
    }

    fun isEmpty(): Boolean = size == 0

    fun peek(): T {
        if (isEmpty())
            throw IllegalArgumentException()

        return items[top]
    }

    fun pop(): T {
        if (isEmpty())
            throw IllegalArgumentException()

        val result = items[top]
        // items[top]=null
        top -= 1
        size -= 1
        return result
    }

    fun push(item: T) {
        if (size == capacity)
            throw IndexOutOfBoundsException()

        top += 1
        size += 1
        items[top] = item
    }
}