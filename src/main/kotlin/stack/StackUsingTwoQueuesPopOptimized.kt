package stack

import queue.QueueUsingDoublyLinkedList

class StackUsingTwoQueuesPopOptimized<T : Any> {
    private val queue1 = QueueUsingDoublyLinkedList<T>()
    private val queue2 = QueueUsingDoublyLinkedList<T>()

    fun size(): Int = queue1.size() + queue2.size()

    fun isEmpty(): Boolean = queue1.isEmpty() && queue2.isEmpty()

    // O(n)
    fun push(item: T) {
        // push into the empty queue, so new item is at its front
        if (queue1.isEmpty()) {
            queue1.enqueue(item)
            // move old items behind the new item: top (new item) is always at front
            while (!queue2.isEmpty()) {
                queue1.enqueue(queue2.dequeue())
            }
        } else {
            queue2.enqueue(item)
            while (!queue1.isEmpty()) {
                queue2.enqueue(queue1.dequeue())
            }
        }
    }

    // O(1): top is at the front of the non-empty queue
    fun pop(): T = activeQueue().dequeue()

    // O(1)
    fun peek(): T = activeQueue().peek()

    // exactly one queue is non-empty (or both empty), holding old items with top at front
    private fun activeQueue(): QueueUsingDoublyLinkedList<T> =
        if (queue1.isEmpty()) queue2 else queue1
}