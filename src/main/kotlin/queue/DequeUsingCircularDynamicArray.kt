package queue

import array.CircularDynamicArray
import linked_list.DoublyLinkedList

/**
 * A deque (double-ended queue): insertion, removal and peek at both ends, all O(1).
 *
 * The class is a thin wrapper over [CircularDynamicArray]'s six end-operations — that
 * is the point: a deque is just "a list with cheap operations at both ends", and the
 * backing structure supplies those primitives directly.
 *
 * [DoublyLinkedList] exposes exactly the same six primitives (its peek methods are
 * named [DoublyLinkedList.peekFirst]/[DoublyLinkedList.peekLast] where the array names
 * them getFirst/getLast), so a linked-list deque is this same class with only the field
 * type swapped. The field declaration and each call show that swap as a comment.
 */
class DequeUsingCircularDynamicArray<T : Any> {

    private val arr = CircularDynamicArray<T>()
    // private val list = DoublyLinkedList<T>()

    fun size(): Int = arr.size
    // fun size(): Int = list.size

    fun isEmpty(): Boolean = arr.isEmpty()
    // fun isEmpty(): Boolean = list.isEmpty()

    fun addFirst(item: T) {
        arr.addFirst(item)
        // list.addFirst(item)
    }

    fun addLast(item: T) {
        arr.addLast(item)
        // list.addLast(item)
    }

    fun removeFirst(): T {
        return arr.removeFirst()
        // return list.removeFirst()
    }

    fun removeLast(): T {
        return arr.removeLast()
        // return list.removeLast()
    }

    fun peekFirst(): T {
        return arr.getFirst()
        // return list.peekFirst()
    }

    fun peekLast(): T {
        return arr.getLast()
        // return list.peekLast()
    }
}