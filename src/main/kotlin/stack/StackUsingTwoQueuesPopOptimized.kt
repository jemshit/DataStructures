package stack

import queue.QueueUsingDoublyLinkedList

class StackUsingTwoQueuesPopOptimized<T : Any> {
    private val queue1 = QueueUsingDoublyLinkedList<T>()
    private val queue2 = QueueUsingDoublyLinkedList<T>()

    fun size(): Int = queue1.size() + queue2.size()

    fun isEmpty(): Boolean = queue1.isEmpty() && queue2.isEmpty()

    fun push(item: T) {
        if (queue1.isEmpty()) {
            queue1.enqueue(item)
            // push everything from queue2 to queue1
            while (!queue2.isEmpty()) {
                queue1.enqueue(queue2.dequeue())
            }

        } else {
            queue2.enqueue(item)
            // push everything from queue1 to queue2
            while (!queue1.isEmpty()) {
                queue2.enqueue(queue1.dequeue())
            }
        }
    }

    fun pop(): T {
        if (isEmpty())
            throw IllegalAccessException()

        if (!queue1.isEmpty()) {
            return queue1.dequeue()
        } else {
            return queue2.dequeue()
        }
    }

    fun peek(): T {
        if (isEmpty())
            throw IllegalAccessException()

        if (!queue1.isEmpty()) {
            return queue1.peek()
        } else {
            return queue2.peek()
        }
    }
}