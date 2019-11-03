package linked_list

class SinglyLinkedList<T : Any> : Iterable<T> {

    class Node<T : Any> {
        val data: T
        var next: Node<T>? = null

        constructor(
            data: T,
            next: Node<T>? = null
        ) {
            this.data = data
            this.next = next
        }
    }

    var size: Int = 0
        private set
    private var head: Node<T>? = null
    private var tail: Node<T>? = null

    fun isEmpty(): Boolean = size == 0

    fun clear() {
        if (isEmpty())
            return

        while (head != null) {
            val oldHead = head!!
            head = head!!.next
            oldHead.next = null
        }

        size = 0
    }

    fun indexOf(item: T): Int {
        if (isEmpty())
            return -1

        var index = 0
        var traverser = head
        while (traverser != null) {
            if (traverser.data.equals(item))
                return index

            index += 1
            traverser = traverser.next
        }

        return -1
    }

    fun contains(item: T): Boolean = indexOf(item) != -1

    fun addFirst(item: T) {
        if (isEmpty()) {
            head = Node(item)
            tail = head
        } else {
            val newNode = Node(item, next = head)
            head = newNode
        }

        size += 1
    }

    fun addLast(item: T) {
        if (isEmpty()) {
            head = Node(item)
            tail = head
        } else {
            val newNode = Node(item, null)
            tail!!.next = newNode
            tail = newNode
        }

        size += 1
    }

    fun add(index: Int, item: T) {
        if (index !in 0..size)
            throw IndexOutOfBoundsException()

        if (index == 0)
            return addFirst(item)
        if (index == size)
            return addLast(item)

        var nodePrev: Node<T>? = head
        for (tIndex in 1 until index)
            nodePrev = nodePrev!!.next

        val newNode = Node(item, next = nodePrev!!.next)
        nodePrev.next = newNode
        size += 1
    }

    fun peekFirst(): T {
        if (head == null)
            throw IllegalAccessException()

        return head!!.data
    }

    fun peekLast(): T {
        if (tail == null)
            throw IllegalAccessException()

        return tail!!.data
    }

    fun removeFirst(): T {
        if (isEmpty())
            throw IllegalAccessException()

        val oldHead = head!!
        head = head!!.next
        oldHead.next = null

        size -= 1
        return oldHead.data
    }

    fun removeLast(): T {
        if (isEmpty())
            throw IllegalAccessException()

        if (size == 1)
            return removeFirst()

        var tailPrev = head!!
        while (tailPrev.next != tail)
            tailPrev = tailPrev.next!!

        val oldTail = tail!!
        tailPrev.next = null
        tail = tailPrev

        size -= 1
        return oldTail.data
    }

    fun remove(node: Node<T>): T {
        if (isEmpty())
            throw IllegalAccessException()

        if (node.next == head!!.next)
            return removeFirst()
        if (node.next == null)
            return removeLast()

        var nodePrev: Node<T>? = head!!
        while (nodePrev != null && nodePrev.next != node)
            nodePrev = nodePrev.next

        if (nodePrev == null)
            throw IllegalAccessException()

        nodePrev.next = node.next
        node.next = null
        size -= 1
        return node.data
    }

    fun removeAt(index: Int): T {
        if (index !in 0 until size)
            throw IllegalAccessException()

        if (index == 0)
            return removeFirst()
        if (index == size - 1)
            return removeLast()

        var nodePrev: Node<T> = head!!
        for (prevIndex in 1 until index)
            nodePrev = nodePrev.next!!

        val node = nodePrev.next!!
        nodePrev.next = node.next
        node.next = null
        size -= 1
        return node.data
    }

    fun remove(item: T): Boolean {
        if (isEmpty())
            throw IllegalAccessException()

        if (head!!.data.equals(item)) {
            removeFirst()
            return true
        }
        if (tail!!.data.equals(item)) {
            removeLast()
            return true
        }

        var nodePrev = head!!
        while (nodePrev.next != null && !nodePrev.next!!.data.equals(item)) {
            nodePrev = nodePrev.next!!
        }
        if (nodePrev.next == null)
            return false

        val node = nodePrev.next!!
        nodePrev.next = node.next
        node.next = null
        size -= 1
        return true
    }

    override operator fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var traverser: Node<T>? = head

            override fun hasNext(): Boolean {
                return traverser != null
            }

            override fun next(): T {
                val data = traverser!!.data
                traverser = traverser!!.next
                return data
            }
        }
    }

    fun reverseLinkedList() {
        if (size == 0 || size == 1)
            return

        var head: Node<T>? = this.head
        var current: Node<T>? = this.head!!.next
        var next: Node<T>? = current?.next

        this.tail = this.head
        head?.next = null

        while (head != null) {
            current?.next = head

            // if reached end
            if (next != null && next.next == null) {
                next.next = current
                head = next
                break
            }

            // move (head, current, next) to right
            val temp = next
            next = next?.next
            head = current
            current = temp
        }

        this.head = head
    }

    fun reverseLinkedList2() {
        if (size == 0 || size == 1)
            return

        this.tail = this.head
        var prev: Node<T>? = null
        var current: Node<T>? = this.head

        while (current != null) {
            val next = current.next
            current.next = prev
            prev = current
            current = next
        }

        this.head = prev
    }

    fun printList() {
        var node: Node<T>? = this.head
        while (node != null) {
            print("${node.data} ")
            node = node.next
        }
        println()
    }
}

