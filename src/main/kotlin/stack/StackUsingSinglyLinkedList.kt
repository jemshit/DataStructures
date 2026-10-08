package stack

import linked_list.SinglyLinkedList

class StackUsingSinglyLinkedList<T : Any> {
    private val items = SinglyLinkedList<T>()

    fun size(): Int = items.size

    fun isEmpty(): Boolean = items.isEmpty()

    fun push(item: T) {
        items.addFirst(item)
    }

    fun pop(): T {
        return items.removeFirst()
    }

    fun peek(): T {
        return items.peekFirst()
    }
}