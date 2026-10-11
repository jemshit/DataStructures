package stack

import queue.QueueUsingDoublyLinkedList

class StackUsingTwoQueuesPushOptimized<T : Any> {
    private val queue1 = QueueUsingDoublyLinkedList<T>()
    private val queue2 = QueueUsingDoublyLinkedList<T>()

    val size: Int
        get() = queue1.size() + queue2.size()

    fun size(): Int = size

    fun isEmpty(): Boolean = queue1.isEmpty() && queue2.isEmpty()

    // O(1): append to the rear of the active queue, top is its last item
    fun push(item: T) {
        activeQueue().enqueue(item)
    }

    // O(n)
    fun pop(): T {
        if (isEmpty()) throw NoSuchElementException("Stack is empty")

        // capture before the move: after it, both queues are non-empty
        val active = activeQueue()
        val empty = emptyQueue()

        // move all items except last (top) to the empty queue
        while (active.size() > 1) {
            empty.enqueue(active.dequeue())
        }

        // last item of the active queue is top
        return active.dequeue()
    }

    // O(n)
    fun peek(): T {
        if (isEmpty()) throw NoSuchElementException("Stack is empty")

        // capture before the move: after it, both queues are non-empty
        val active = activeQueue()
        val empty = emptyQueue()

        // move all items except last (top) to the empty queue
        while (active.size() > 1) {
            empty.enqueue(active.dequeue())
        }

        // peek last (top), then move it behind the others in the empty queue
        val result = active.peek()
        empty.enqueue(active.dequeue())
        return result
    }

    // Returns the queue currently holding items
    private fun activeQueue(): QueueUsingDoublyLinkedList<T> =
        if (queue1.isEmpty()) queue2 else queue1

    // Returns the queue currently empty and ready to receive items
    private fun emptyQueue(): QueueUsingDoublyLinkedList<T> =
        if (queue1.isEmpty()) queue1 else queue2
}