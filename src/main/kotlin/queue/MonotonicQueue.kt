package queue

/**
 * A monotonic queue: a deque whose values never increase from front (oldest) to rear
 * (newest), so [max] is always the front item.
 *
 * Standard names: [push] enqueues at the rear, [pop] marks an item as having left the
 * window, [max] reports the current maximum. Every item is pushed once and removed at
 * most once (discarded by [push] or removed by [pop]), so all operations are O(1)
 * amortized — the reason sliding-window maximum runs in O(n).
 */
class MonotonicQueue<T : Comparable<T>> {

    private val items = DequeUsingCircularDynamicArray<T>()

    /**
     * Adds [item] at the rear, discarding every item it permanently overshadows.
     *
     * A discarded item arrived earlier AND is smaller, so [item] beats it for the rest
     * of its stay in the window: it can never be the maximum again.
     */
    fun push(item: T) {
        while (!items.isEmpty() && item > items.peekLast()) {
            items.removeLast()
        }

        items.addLast(item)
    }

    /** The current maximum: the front item, since items increase from front to rear.
     *
     * Precondition: not empty; callers call max() right after push */
    fun max(): T = items.peekFirst()

    /**
     * Marks [item] as having left the window, removing it if it is still in the queue.
     *
     * If it survived, it can only be at the front: items leave the window in arrival
     * order, so everything older has already been popped. If it is not at the front,
     * it was already discarded by [push] — nothing to do.
     */
    fun pop(item: T) {
        if (!items.isEmpty() && items.peekFirst() == item) {
            items.removeFirst()
        }
    }
}