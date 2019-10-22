package queue

import stack.StackUsingDoublyLinkedList

// Enqueue Optimized
class QueueUsingTwoStacksEnOptimized<T : Any> {
    private var stack1 = StackUsingDoublyLinkedList<T>() // always add to this
    private var stack2 = StackUsingDoublyLinkedList<T>() // push all from stack1 to here. and pop from here

    fun size(): Int = stack1.size() + stack2.size()

    fun isEmpty(): Boolean = stack1.isEmpty() && stack2.isEmpty()

    fun enqueue(item: T) {
        stack1.push(item)
    }

    fun dequeue(): T {
        if (isEmpty())
            throw IllegalAccessException()

        // If stack2 is empty, push everything from stack1 to stack2.
        if (stack2.isEmpty()) {
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop())
            }
        }

        // Then pop from stack2
        return stack2.pop()
    }

    fun peek(): T {
        if (isEmpty())
            throw IllegalAccessException()

        // If stack2 is empty, push everything from stack1 to stack2.
        if (stack2.isEmpty()) {
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop())
            }
        }

        // Then peek from stack2
        return stack2.peek()
    }

}