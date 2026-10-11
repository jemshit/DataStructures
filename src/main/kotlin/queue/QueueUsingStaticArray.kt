package queue

private const val DEFAULT_CAPACITY = 8

// Using static array in a circular ring-buffer manner.
class QueueUsingStaticArray<T : Any>(private val capacity: Int = DEFAULT_CAPACITY) {

    init {
        require(capacity > 0) { "capacity must be positive, was $capacity" }
    }

    private var rear: Int = 0   // position to add new item to
    private var front: Int = 0  // position to remove item from

    var size: Int = 0
        private set

    @Suppress("UNCHECKED_CAST")
    private val items: Array<T?> = arrayOfNulls<Any?>(capacity) as Array<T?>

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    fun isFull(): Boolean = size == capacity

    fun enqueue(item: T) {
        check(!isFull()) { "queue overflow: capacity $capacity reached" }

        items[rear] = item           // 1. write to current rear
        rear = (rear + 1) % capacity  // 2. advance rear
        size += 1
    }

    fun dequeue(): T {
        check(!isEmpty()) { "queue is empty" }

        val result = items[front]!!     // 1. read from current front
        items[front] = null             // 2. clear for GC, Null out reference
        front = (front + 1) % capacity  // 3. advance front
        size -= 1

        return result
    }

    fun peek(): T {
        check(!isEmpty()) { "queue is empty" }
        return items[front]!!
    }
}