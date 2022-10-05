package queue

class MonotonicQueue {
    private val items: Deque = Deque()

    fun push(item: Int) {
        while (!items.isEmpty() && item > items.getRear()) {
            // increasing order from 'front' -> 'back'
            items.removeRear()
        }

        items.insertRear(item)
    }

    fun max() = items.getFront()

    fun pop(item: Int) {
        // don't know why only checking if 'front' is enough
        if (!items.isEmpty() && items.getFront() == item) {
            items.removeFront()
        }
    }
}