package queue

import linked_list.DoublyLinkedList
import linked_list.SinglyLinkedList

class QueueUsingSinglyLinkedList<T : Any> {
    private val items = SinglyLinkedList<T>()

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