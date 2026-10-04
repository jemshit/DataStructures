package linked_list

import java.util.ConcurrentModificationException

/**
 * A singly linked list built around a dummy head node and a stored tail.
 * The dummy head gives every real item a predecessor, so all insertions and
 * removals funnel through insertAfter/removeAfter with no head special cases;
 * the stored tail makes addLast/peekLast O(1). Removal from the tail is O(n)
 * because there is no back pointer.
 */
class SinglyLinkedList<T : Any> : Iterable<T> {

    // data is null only for the dummy head. Real items are never null (T : Any),
    // so reading a real node's data with !! is always safe.
    private class Node<T : Any>(
        var data: T?,
        var next: Node<T>? = null
    )

    // The dummy head is an empty node that always sits in front of the first item.
    // Every real node therefore has a predecessor, so inserting/removing never needs
    // an "is it the head?" special case.
    private val dummyHead = Node<T>(data = null)

    // Invariants:
    //   empty:     dummyHead.next == null, tail === dummyHead, size == 0
    //   non-empty: dummyHead.next is the first item, tail is the last item, tail.next == null
    // tail is never null: it points at the dummy head while the list is empty.
    private var tail: Node<T> = dummyHead

    var size: Int = 0
        private set

    // Bumped by every structural change: add/remove/clear/reverse (NOT set, which swaps data in place).
    // Iterators snapshot this at creation; if it moves, they throw ConcurrentModificationException.
    private var modCount = 0

    fun isEmpty(): Boolean = size == 0

    fun clear() {
        // not required on the JVM (dropping dummyHead.next frees the chain); this just breaks the links explicitly
        var node = dummyHead.next
        while (node != null) {
            val next = node.next
            node.next = null
            node = next
        }
        dummyHead.next = null
        tail = dummyHead
        size = 0
        modCount += 1
    }

    fun indexOf(item: T): Int {
        var index = 0
        var node = dummyHead.next
        while (node != null) {
            if (node.data == item)
                return index

            index += 1
            node = node.next
        }

        return -1
    }

    operator fun contains(item: T): Boolean = indexOf(item) != -1

    operator fun get(index: Int): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        return nodeAt(index).data!!
    }

    /** Replaces the item at [index] and returns the previous one. */
    operator fun set(index: Int, item: T): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        val node = nodeAt(index)
        val oldData = node.data!!
        node.data = item
        return oldData
    }

    // O(1): the dummy head is the predecessor of the first item
    fun addFirst(item: T) = insertAfter(dummyHead, item)

    // O(1) because tail is stored: insert right after the last item
    fun addLast(item: T) = insertAfter(tail, item)

    fun add(index: Int, item: T) {
        if (index !in 0..size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        // an append goes through the stored tail, so it stays O(1)
        if (index == size)
            return addLast(item)

        // index 0 -> insert after the dummy head
        insertAfter(nodeBefore(index), item)
    }

    fun peekFirst(): T {
        if (isEmpty())
            throw NoSuchElementException("List is empty")

        return dummyHead.next!!.data!!
    }

    fun peekLast(): T {
        if (isEmpty())
            throw NoSuchElementException("List is empty")

        return tail.data!!
    }

    fun removeFirst(): T {
        if (isEmpty())
            throw NoSuchElementException("List is empty")

        return removeAfter(dummyHead)
    }

    fun removeLast(): T {
        if (isEmpty())
            throw NoSuchElementException("List is empty")

        // O(n): no back pointer, so walk from the dummy head to find the node before the tail
        return removeAfter(nodeBefore(size - 1))
    }

    fun removeAt(index: Int): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        // a node can only be unlinked if we hold its predecessor
        return removeAfter(nodeBefore(index))
    }

    /** Removes the first occurrence of [item]. Returns false if it isn't in the list. */
    fun remove(item: T): Boolean {
        // keep prev while searching, because unlinking needs the predecessor.
        // The first match wins, same as java.util.List.remove(element).
        var prev = dummyHead
        var node = dummyHead.next
        while (node != null && node.data != item) {
            prev = node
            node = node.next
        }
        if (node == null)
            return false

        removeAfter(prev)
        return true
    }

    override operator fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var traverser: Node<T>? = dummyHead.next
            private val expectedModCount = modCount // snapshot at creation

            override fun hasNext(): Boolean = traverser != null

            override fun next(): T {
                // fail-fast: a structural change since creation means the traversal is now meaningless
                if (modCount != expectedModCount)
                    throw ConcurrentModificationException()

                val node = traverser ?: throw NoSuchElementException()
                traverser = node.next
                return node.data!!
            }
        }
    }

    /** Reverses the list in place in O(n). No-op for size <= 1. */
    fun reverse() {
        if (size <= 1)
            return

        // the first item becomes the new tail
        tail = dummyHead.next!!

        // the dummy head is not part of the reversal: it stays in front
        var prev: Node<T>? = null
        var current: Node<T>? = dummyHead.next
        while (current != null) {
            val next = current.next     // 1. remember the rest before we overwrite the link
            current.next = prev         // 2. flip the pointer backwards
            prev = current              // 3. step forward
            current = next
        }

        // when the loop ends, prev is the old last item, which is now the first
        dummyHead.next = prev
        modCount += 1
    }

    override fun toString(): String = joinToString(separator = ", ", prefix = "[", postfix = "]")

    private fun insertAfter(prev: Node<T>, item: T) {
        // point the new node at prev's successor FIRST;
        // overwriting prev.next first would lose the rest of the list
        val newNode = Node(item, next = prev.next)
        prev.next = newNode

        // inserted after the last item (or into an empty list, where prev is the dummy head == tail)
        if (prev === tail)
            tail = newNode

        size += 1
        modCount += 1
    }

    /**
     * The only place nodes are removed, so tail and size are kept consistent in one spot.
     * Removes the node after [prev] ([prev] is the dummy head when removing the first item).
     */
    private fun removeAfter(prev: Node<T>): T {
        val node = prev.next!!

        // 1. bypass the node: prev now points at the node's successor
        prev.next = node.next

        // 2. if the tail was removed, prev is the new tail (the dummy head when the list became empty)
        if (node === tail)
            tail = prev

        // 3. detach the removed node completely
        node.next = null
        size -= 1
        modCount += 1
        return node.data!!
    }

    /** The node right before position [index] (the dummy head for 0). [index] must be in 0..size. */
    private fun nodeBefore(index: Int): Node<T> {
        var node = dummyHead
        repeat(index) { node = node.next!! }
        return node
    }

    /** O(index): walks from the dummy head. [index] must be in 0 until size. */
    private fun nodeAt(index: Int): Node<T> = nodeBefore(index).next!!
}
