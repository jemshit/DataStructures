package stack

import linked_list.DoublyLinkedList

class StackUsingDoublyLinkedList<T : Any> {
    private val items = DoublyLinkedList<T>()

    fun size(): Int = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    fun push(item: T) {
        items.addLast(item)
    }

    fun pop(): T {
        if (isEmpty())
            throw IllegalAccessException()
        return items.removeLast()
    }

    fun peek(): T {
        if (isEmpty())
            throw IllegalAccessException()
        return items.peekLast()
    }
}