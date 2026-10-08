package stack

private const val DEFAULT_CAPACITY = 100

class StackUsingStaticArray<T : Any>(private val capacity: Int = DEFAULT_CAPACITY) {

    init {
        require(capacity > 0) { "capacity must be positive, was $capacity" }
    }

    // top = -1 means empty, otherwise it is the index of the top item
    private var top: Int = -1

    @Suppress("UNCHECKED_CAST")
    private val items: Array<T?> = arrayOfNulls<Any?>(capacity) as Array<T?>

    val size: Int
        get() = top + 1

    fun isEmpty(): Boolean = top == -1

    fun peek(): T {
        check(top != -1) { "stack is empty" }
        return items[top]!!
    }

    fun pop(): T {
        check(top != -1) { "stack is empty" }
        val result = items[top]!!
        // for garbage collection
        items[top] = null
        top -= 1
        return result
    }

    fun push(item: T) {
        check(top != capacity - 1) { "stack overflow: capacity $capacity reached" }
        top += 1
        items[top] = item
    }
}