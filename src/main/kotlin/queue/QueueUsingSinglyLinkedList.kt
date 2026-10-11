package queue

import linked_list.SinglyLinkedList

class QueueUsingSinglyLinkedList<T : Any> {
    private val items = SinglyLinkedList<T>()

    fun size(): Int = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    fun enqueue(item: T) {
        items.addLast(item)
    }

    fun dequeue(): T {
        return items.removeFirst()
    }

    fun peek(): T {
        return items.peekFirst()
    }
}