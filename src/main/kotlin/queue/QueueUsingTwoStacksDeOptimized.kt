package queue

import stack.StackUsingDoublyLinkedList

// Dequeue Optimized
class QueueUsingTwoStacksDeOptimized<T : Any> {
    private var stack1 = StackUsingDoublyLinkedList<T>() // Front item will be always at top of this
    private var stack2 = StackUsingDoublyLinkedList<T>() // used as helper

    fun size(): Int = stack1.size()

    fun isEmpty(): Boolean = stack1.isEmpty()

    fun enqueue(item: T) {
        if (stack1.isEmpty())
            stack1.push(item)
        else {
            // Pop from stack1 to stack2.
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop())
            }
            // Then inset to stack1.
            stack1.push(item)
            // Then put everything back from stack2 to stack1
            while (!stack2.isEmpty()) {
                stack1.push(stack2.pop())
            }
        }
    }

    fun dequeue(): T {
        if (stack1.isEmpty())
            throw IllegalAccessException()

        return stack1.pop()
    }

    fun peek(): T {
        if (stack1.isEmpty())
            throw IllegalAccessException()

        return stack1.peek()
    }

}