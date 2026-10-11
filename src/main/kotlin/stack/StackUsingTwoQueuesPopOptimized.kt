package stack

import queue.QueueUsingDoublyLinkedList

class StackUsingTwoQueuesPopOptimized<T : Any> {
    private val queue1 = QueueUsingDoublyLinkedList<T>()
    private val queue2 = QueueUsingDoublyLinkedList<T>()

    val size: Int
        get() = queue1.size() + queue2.size()

    fun size(): Int = size

    fun isEmpty(): Boolean = queue1.isEmpty() && queue2.isEmpty()

    // O(n)
    fun push(item: T) {
        val empty = emptyQueue()
        val active = activeQueue()

        // Push into the empty queue so the new item is at the front (top)
        empty.enqueue(item)
        // Move all existing items behind the new item
        while (!active.isEmpty()) {
            empty.enqueue(active.dequeue())
        }
    }

    // O(1): top is always at the front of the active queue
    fun pop(): T {
        if (isEmpty()) throw NoSuchElementException("Stack is empty")
        return activeQueue().dequeue()
    }

    // O(1)
    fun peek(): T {
        if (isEmpty()) throw NoSuchElementException("Stack is empty")
        return activeQueue().peek()
    }

    // Returns the queue currently holding items
    private fun activeQueue(): QueueUsingDoublyLinkedList<T> =
        if (queue1.isEmpty()) queue2 else queue1

    // Returns the queue currently empty and ready to receive items
    private fun emptyQueue(): QueueUsingDoublyLinkedList<T> =
        if (queue1.isEmpty()) queue1 else queue2
}