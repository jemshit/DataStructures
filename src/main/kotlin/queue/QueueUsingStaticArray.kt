package queue

// Using Static Array in circular manner. Other non optimized option would be shifting array elements after dequeue
class QueueUsingStaticArray<T : Any> {
    private var items: Array<T>
    private var front: Int = 0
    private var end: Int = 0
    private var capacity: Int = 100
    private var length: Int = 0

    constructor(capacity: Int = 100) {
        if (capacity <= 0)
            throw IllegalArgumentException()

        this.capacity = capacity
        this.items = arrayOfNulls<Any>(capacity) as Array<T>
    }

    fun size(): Int = length

    fun isEmpty(): Boolean = length == 0

    fun enqueue(item: T) {
        if (length == capacity)
            throw IndexOutOfBoundsException()

        items[end] = item
        length += 1
        end += 1
        // circulate (end never can pass front by circulating, capacity will be reached)
        if (end == capacity)
            end = 0
    }

    fun dequeue(): T {
        if (isEmpty())
            throw IllegalAccessException()

        val result = items[front]
        length -= 1
        front += 1
        // circulate
        if (front == capacity)
            front = 0

        return result
    }

    fun peek(): T {
        if (isEmpty())
            throw IllegalAccessException()

        return items[front]
    }

}