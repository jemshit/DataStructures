package queue

import stack.StackUsingDoublyLinkedList

// Enqueue Optimized: Enqueue is O(1), Dequeue & Peek are Amortized O(1).
// Uses the classic "inbox / outbox" pattern.
class QueueUsingTwoStacksEnOptimized<T : Any> {
    private val inStack = StackUsingDoublyLinkedList<T>()  // Enqueue destination
    private val outStack = StackUsingDoublyLinkedList<T>() // Dequeue & peek source

    val size: Int
        get() = inStack.size() + outStack.size()

    fun size(): Int = size

    fun isEmpty(): Boolean = inStack.isEmpty() && outStack.isEmpty()

    // O(1): Directly push to inStack
    fun enqueue(item: T) {
        inStack.push(item)
    }

    // Amortized O(1): Pops from outStack. When outStack is empty, transfers all items
    // from inStack in reverse order (making the oldest item the new top of outStack).
    fun dequeue(): T {
        if (isEmpty()) throw NoSuchElementException("Queue is empty")
        shiftToOutStackIfNeeded()
        return outStack.pop()
    }

    // Amortized O(1)
    fun peek(): T {
        if (isEmpty()) throw NoSuchElementException("Queue is empty")
        shiftToOutStackIfNeeded()
        return outStack.peek()
    }

    // Transfers all elements from inStack to outStack only when outStack is depleted.
    private fun shiftToOutStackIfNeeded() {
        if (outStack.isEmpty()) {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop())
            }
        }
    }
}