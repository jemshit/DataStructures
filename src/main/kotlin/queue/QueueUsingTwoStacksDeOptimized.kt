package queue

import stack.StackUsingDoublyLinkedList

// Dequeue Optimized: Enqueue is O(N), Dequeue is O(1), Peek is O(1).
// The front of the queue is always maintained at the top of mainStack.
class QueueUsingTwoStacksDeOptimized<T : Any> {
    private val mainStack = StackUsingDoublyLinkedList<T>()   // Front item is always at the top of this
    private val helperStack = StackUsingDoublyLinkedList<T>() // Auxiliary buffer used during enqueue

    val size: Int
        get() = mainStack.size()

    fun size(): Int = size

    fun isEmpty(): Boolean = mainStack.isEmpty()

    // O(N): Move all items to helperStack, push the new item to the bottom of mainStack,
    // then move everything back so the oldest item remains on top.
    fun enqueue(item: T) {
        while (!mainStack.isEmpty()) {
            helperStack.push(mainStack.pop())
        }

        mainStack.push(item)

        while (!helperStack.isEmpty()) {
            mainStack.push(helperStack.pop())
        }
    }

    // O(1): Oldest item is at the top of mainStack
    fun dequeue(): T {
        if (isEmpty()) throw NoSuchElementException("Queue is empty")
        return mainStack.pop()
    }

    // O(1)
    fun peek(): T {
        if (isEmpty()) throw NoSuchElementException("Queue is empty")
        return mainStack.peek()
    }
}