package linked_list

import java.util.ConcurrentModificationException

/**
 * A doubly linked list built around two sentinel nodes, dummyHead and dummyTail, which hold
 * no data and always exist (even when empty). Every real node therefore always has a
 * predecessor and a successor, so insert/remove need no null checks or "first/last item"
 * special cases. addFirst/addLast/peekFirst/peekLast are O(1); nodeAt walks from whichever
 * end is nearer, so get/set/removeAt/add-at-index are O(min(index, size - index)).
 */
class DoublyLinkedList<T : Any> : Iterable<T> {

    // data is null only for the two sentinels. Real items are never null (T : Any),
    // so reading a real node's data with !! is always safe.
    // prev/next look nullable, but they are never null on a real node: dummyHead caps the
    // front and dummyTail caps the back, so a real node's prev is dummyHead or another real
    // node, and its next is another real node or dummyTail.
    private class Node<T : Any>(
        var data: T?,
        var prev: Node<T>? = null,
        var next: Node<T>? = null
    )

    // The sentinels hold no data. dummyHead.next is the first real node (dummyTail when
    // empty); dummyTail.prev is the last real node (dummyHead when empty).
    private val dummyHead = Node<T>(data = null)
    private val dummyTail = Node<T>(data = null)

    init {
        dummyHead.next = dummyTail
        dummyTail.prev = dummyHead
    }

    // Invariants:
    //   empty:     dummyHead.next === dummyTail, dummyTail.prev === dummyHead, size == 0
    //   non-empty: dummyHead.next is the first item, dummyTail.prev is the last item
    //   every real node has non-null prev and next (a real node or a sentinel)
    var size: Int = 0
        private set

    // Bumped by every structural change: add/remove/clear/reverse (NOT set, which swaps data in place).
    // Iterators snapshot this at creation; if it moves, they throw ConcurrentModificationException.
    private var modCount = 0

    fun isEmpty(): Boolean = size == 0

    fun clear() {
        // no structural change on an already-empty list, so leave modCount alone
        if (isEmpty())
            return

        // breaking the links explicitly is not required on the JVM (dropping dummyHead.next
        // frees the chain); it just makes the detach explicit
        var node = dummyHead.next!!      // dummyTail when the list is empty
        while (node != dummyTail) {
            val next = node.next!!
            node.prev = null
            node.next = null
            node = next
        }
        // the sentinels stay; just re-point them at each other
        dummyHead.next = dummyTail
        dummyTail.prev = dummyHead
        size = 0
        modCount += 1
    }

    fun indexOf(item: T): Int {
        var index = 0
        var node = dummyHead.next!!      // dummyTail when empty
        while (node != dummyTail) {
            if (node.data == item)
                return index

            index += 1
            node = node.next!!
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

    // O(1): insert right after dummyHead
    fun addFirst(item: T) = insertAfter(dummyHead, item)

    // O(1): insert right after the last item (dummyTail.prev is dummyHead when empty)
    fun addLast(item: T) = insertAfter(dummyTail.prev!!, item)

    fun add(index: Int, item: T) {
        if (index !in 0..size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        // an append goes through addLast so it stays O(1)
        if (index == size)
            return addLast(item)

        // index 0 -> insert after dummyHead
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

        return dummyTail.prev!!.data!!
    }

    fun removeFirst(): T {
        if (isEmpty())
            throw NoSuchElementException("List is empty")

        return removeNode(dummyHead.next!!)
    }

    // O(1): dummyTail.prev is the last item, and removal is direct because the node knows both neighbors
    fun removeLast(): T {
        if (isEmpty())
            throw NoSuchElementException("List is empty")

        return removeNode(dummyTail.prev!!)
    }

    fun removeAt(index: Int): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        // unlike a singly list, no need to hold the predecessor: remove the node itself
        return removeNode(nodeAt(index))
    }

    /** Removes the first occurrence of [item]. Returns false if it isn't in the list. */
    fun remove(item: T): Boolean {
        var node = dummyHead.next!!       // dummyTail when empty
        while (node != dummyTail && node.data != item) {
            node = node.next!!
        }
        if (node == dummyTail)
            return false

        removeNode(node)
        return true
    }

    override operator fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            // current points at the next node to return; the dummyTail sentinel marks the end
            private var current = dummyHead.next!!
            private val expectedModCount = modCount // snapshot at creation

            override fun hasNext(): Boolean = current != dummyTail

            override fun next(): T {
                // fail-fast: a structural change since creation means the traversal is now meaningless
                if (modCount != expectedModCount)
                    throw ConcurrentModificationException()

                if (current == dummyTail)
                    throw NoSuchElementException()

                val node = current
                current = node.next!!
                return node.data!!
            }
        }
    }

    /**
     * Reverses the list in place in O(n): swap prev/next on every real node,
     * then re-point the sentinels at the nodes that are now first and last.
     */
    fun reverse() {
        // no structural change for 0 or 1 items, so leave modCount alone
        if (size <= 1)
            return

        var node = dummyHead.next!!
        while (node != dummyTail) {
            val next = node.next!!     // remember the old successor before swapping
            node.next = node.prev
            node.prev = next
            node = next                // next still points forward through the OLD order
        }
        // every real node is now flipped in place; only the sentinel links need repair

        // the old first/last are now the outer real nodes; fix the sentinel links
        val oldFirst = dummyHead.next!!
        val oldLast = dummyTail.prev!!

        dummyHead.next = oldLast
        oldLast.prev = dummyHead

        dummyTail.prev = oldFirst
        oldFirst.next = dummyTail

        modCount += 1
    }

    override fun toString(): String = joinToString(separator = ", ", prefix = "[", postfix = "]")

    /**
     * Inserts [item] right after [prev]. Every insertion funnels through here, so size and
     * modCount are maintained in one place. [prev] is dummyHead or a real node.
     */
    private fun insertAfter(prev: Node<T>, item: T) {
        val next = prev.next!!          // never null: a real node or dummyTail
        val newNode = Node(item, prev = prev, next = next)

        prev.next = newNode
        next.prev = newNode

        size += 1
        modCount += 1
    }

    /**
     * The only place nodes are removed, so size and modCount are maintained in one place.
     * Removes [node] in O(1) because it already knows both neighbors. [node] must be a real
     * node (never a sentinel). This direct removal is the doubly list's advantage: a singly
     * list needs the predecessor (removeAfter), a doubly list needs only the node.
     */
    private fun removeNode(node: Node<T>): T {
        val prev = node.prev!!          // never null: dummyHead or a real node
        val next = node.next!!          // never null: a real node or dummyTail

        // 1. bypass the node
        prev.next = next
        next.prev = prev

        // 2. detach the removed node completely
        node.prev = null
        node.next = null

        size -= 1
        modCount += 1
        return node.data!!
    }

    /** The node right before position [index] (dummyHead for 0). [index] must be in 0..size. */
    private fun nodeBefore(index: Int): Node<T> =
        if (index == 0) dummyHead else nodeAt(index - 1)

    /**
     * The node at [index] (must be in 0 until size). Walks from whichever end is nearer —
     * forward for the first half, backward for the second — giving O(min(index, size - index))
     * instead of the O(index) a singly linked list is stuck with.
     */
    private fun nodeAt(index: Int): Node<T> {
        if (index < size / 2) {
            // nearer the front: walk forward
            var node = dummyHead.next!!
            repeat(index) { node = node.next!! }
            return node
        } else {
            // nearer the back: walk backward
            var node = dummyTail.prev!!
            repeat(size - 1 - index) { node = node.prev!! }
            return node
        }
    }
}