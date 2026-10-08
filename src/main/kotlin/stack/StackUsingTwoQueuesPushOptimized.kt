package stack

import queue.QueueUsingDoublyLinkedList

class StackUsingTwoQueuesPushOptimized<T : Any> {
    private val queue1 = QueueUsingDoublyLinkedList<T>()
    private val queue2 = QueueUsingDoublyLinkedList<T>()

    fun size(): Int = queue1.size() + queue2.size()

    fun isEmpty(): Boolean = queue1.isEmpty() && queue2.isEmpty()

    // O(1): append to the end of the active queue, top is its last item
    fun push(item: T) {
        activeQueue().enqueue(item)
    }

    // O(n)
    fun pop(): T {
        // capture before the move: after it, both queues are non-empty
        val active = activeQueue()
        val other = otherQueue()

        // move all items except last (top) to the other queue
        while (active.size() > 1) {
            other.enqueue(active.dequeue())
        }

        // last item of the active queue is top
        return active.dequeue()
    }

    // O(n)
    fun peek(): T {
        // capture before the move: after it, both queues are non-empty
        val active = activeQueue()
        val other = otherQueue()

        // move all items except last (top) to the other queue
        while (active.size() > 1) {
            other.enqueue(active.dequeue())
        }

        // peek last (top), then move it behind the others in the other queue
        val result = active.peek()
        other.enqueue(active.dequeue())
        return result
    }

    // old items stay in the active queue with top at its end
    private fun activeQueue(): QueueUsingDoublyLinkedList<T> =
        if (queue1.isEmpty()) queue2 else queue1

    private fun otherQueue(): QueueUsingDoublyLinkedList<T> =
        if (queue1.isEmpty()) queue1 else queue2
}