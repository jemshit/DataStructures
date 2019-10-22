package queue

import linked_list.DoublyLinkedList

class QueueUsingDoublyLinkedList<T : Any> {
    private val items = DoublyLinkedList<T>()

    fun size(): Int = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    fun enqueue(item: T) {
        items.addLast(item)
    }

    fun dequeue(): T {
        if (isEmpty())
            throw IllegalAccessException()

        return items.removeFirst()
    }

    fun peek(): T {
        if (isEmpty())
            throw IllegalAccessException()

        return items.peekFirst()
    }
}