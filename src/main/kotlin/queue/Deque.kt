package queue

data class Node(
    val data: Int,
    var prev: Node? = null,
    var next: Node? = null
)

internal class Deque {
    private var front: Node? = null
    private var rear: Node? = null
    private var size = 0

    fun isEmpty(): Boolean = front == null

    fun size(): Int = size

    fun insertFront(data: Int) {
        val newNode = Node(data)
        if (front == null) {
            // empty
            front = newNode
            rear = front
        } else {
            newNode.next = front
            front!!.prev = newNode
            front = newNode
        }

        size += 1
    }

    fun insertRear(data: Int) {
        val newNode = Node(data)
        if (rear == null) {
            // empty
            rear = newNode
            front = rear
        } else {
            newNode.prev = rear
            rear!!.next = newNode
            rear = newNode
        }

        size += 1
    }

    fun removeFront() {
        if (isEmpty()) return

        val prevFront = front
        front = front!!.next
        front?.prev = null
        prevFront!!.next = null

        // only one element was present
        if (front == null) {
            rear = null
        }

        size -= 1
    }

    fun removeRear() {
        if (isEmpty()) return

        val prevRear = rear
        rear = rear!!.prev
        rear?.next = null
        prevRear!!.prev = null

        // only one element was present
        if (rear == null) {
            front = null
        }

        size -= 1
    }

    fun getFront(): Int {
        return if (isEmpty()) -1 else front!!.data
    }

    fun getRear(): Int {
        return if (isEmpty()) -1 else rear!!.data
    }

}