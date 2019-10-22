package stack

import queue.QueueUsingDoublyLinkedList

class StackUsingTwoQueuesPushOptimized<T : Any> {
    private val queue1 = QueueUsingDoublyLinkedList<T>()
    private val queue2 = QueueUsingDoublyLinkedList<T>()

    fun size(): Int = queue1.size() + queue2.size()

    fun isEmpty(): Boolean = queue1.isEmpty() && queue2.isEmpty()

    fun push(item: T) {
        if (!queue1.isEmpty())
            queue1.enqueue(item)
        else
            queue2.enqueue(item)
    }

    fun pop(): T {
        if (isEmpty())
            throw IllegalAccessException()

        if (!queue1.isEmpty()) {
            // push everything from queue1 to queue2, except last item
            while (queue1.size() > 1) {
                queue2.enqueue(queue1.dequeue())
            }
            return queue1.dequeue()

        } else {
            // push everything from queue2 to queue1, except last item
            while (queue2.size() > 1) {
                queue1.enqueue(queue2.dequeue())
            }
            return queue2.dequeue()
        }
    }

    fun peek(): T {
        if (isEmpty())
            throw IllegalAccessException()

        if (!queue1.isEmpty()) {
            // push everything from queue1 to queue2, except last item
            while (queue1.size() > 1) {
                queue2.enqueue(queue1.dequeue())
            }
            val result = queue1.peek()
            queue2.enqueue(queue1.dequeue())
            return result

        } else {
            // push everything from queue2 to queue1, except last item
            while (queue2.size() > 1) {
                queue1.enqueue(queue2.dequeue())
            }
            val result = queue2.peek()
            queue1.enqueue(queue2.dequeue())
            return result
        }
    }
}