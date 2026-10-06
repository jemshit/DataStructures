@file:Suppress("UNCHECKED_CAST")

package array

private const val DEFAULT_CAPACITY = 2

/**
 * A dynamically resized circular array with amortized constant-time operations at both ends.
 *
 * The logical contents occupy the circular, left-closed/right-open interval
 * `[start, end)`. Because that interval can wrap past the physical end of [arr],
 * [count] distinguishes an empty array from a full one when `start == end`.
 *
 * @param T the non-null type of element stored in this array
 * @param initialCapacity the initial number of slots in the backing array; must be at least
 * [DEFAULT_CAPACITY]
 * @throws IllegalArgumentException if [initialCapacity] is smaller than [DEFAULT_CAPACITY]
 */
class CircularDynamicArray<T : Any>(initialCapacity: Int = DEFAULT_CAPACITY) {

    /**
     * Nullable backing storage for the elements.
     *
     * Kotlin cannot directly create an array of a non-reified generic type. Empty
     * and vacated slots are represented by `null`, while the public API accepts
     * only non-null [T] values.
     */
    private var arr: Array<Any?>

    /** Index of the first valid element: the closed boundary of `[start, end)`. */
    private var start: Int = 0

    /**
     * Index of the next slot after the last valid element: the open boundary of
     * `[start, end)`.
     */
    private var end: Int = 0

    /** Number of valid elements currently stored in [arr]. */
    private var count: Int = 0

    /** Current physical capacity of [arr], not the number of stored elements. */
    private var capacity: Int

    /** Number of elements currently stored in this array. */
    val size: Int
        get() = count

    init {
        require(initialCapacity >= DEFAULT_CAPACITY) {
            "Initial capacity must be at least $DEFAULT_CAPACITY: $initialCapacity"
        }
        arr = arrayOfNulls(initialCapacity)
        capacity = initialCapacity
    }

    /**
     * Changes the backing-array capacity while preserving logical element order.
     *
     * After resizing, the logical interval must still obey the `[start, end)`
     * convention.
     *
     * @param newCapacity the capacity of the replacement backing array
     */
    private fun resize(newCapacity: Int) {
        val newArr = arrayOfNulls<Any?>(newCapacity)
        for (index in 0 until count) {
            newArr[index] = arr[getCircularIndex(start + index)]
        }
        arr = newArr

        // reset
        start = 0
        end = count
        capacity = newCapacity
    }

    /**
     * Adds [value] before the current first element.
     *
     * The backing array should grow when no free slot remains.
     */
    fun addFirst(value: T) {
        // resize
        if (isFull()) {
            resize(capacity * 2)
        }

        // add
        val indexToAdd = getCircularIndex(start - 1)
        arr[indexToAdd] = value
        start = indexToAdd
        count += 1
    }

    /**
     * Removes and returns the first element.
     *
     * The backing array may shrink when sufficiently underused.
     *
     * @throws NoSuchElementException if this array is empty
     */
    fun removeFirst(): T {
        if (isEmpty()) {
            throw NoSuchElementException("Array is empty")
        }

        // remove
        val itemToRemove = arr[start]
        arr[start] = null
        start = getCircularIndex(start + 1)
        count -= 1

        // resize
        if (count <= (capacity / 4) && (capacity / 2) >= DEFAULT_CAPACITY) {
            resize(capacity / 2)
        }

        return itemToRemove as T
    }

    /**
     * Adds [value] after the current last element.
     *
     * The backing array should grow when no free slot remains.
     */
    fun addLast(value: T) {
        // resize
        if (isFull()) {
            resize(capacity * 2)
        }

        // add
        arr[end] = value
        end = getCircularIndex(end + 1)
        count += 1
    }

    /**
     * Removes and returns the last element.
     *
     * The backing array may shrink when sufficiently underused.
     *
     * @throws NoSuchElementException if this array is empty
     */
    fun removeLast(): T {
        if (isEmpty()) {
            throw NoSuchElementException("Array is empty")
        }

        // remove
        val indexToRemove = getCircularIndex(end - 1)
        val itemToRemove = arr[indexToRemove]
        arr[indexToRemove] = null
        end = indexToRemove
        count -= 1

        // resize
        if (count <= (capacity / 4) && (capacity / 2) >= DEFAULT_CAPACITY) {
            resize(capacity / 2)
        }

        return itemToRemove as T
    }

    /**
     * Returns the first element without removing it.
     *
     * @throws NoSuchElementException if this array is empty
     */
    fun getFirst(): T {
        if (isEmpty()) {
            throw NoSuchElementException("Array is empty")
        }
        return arr[start] as T
    }

    /**
     * Returns the last element without removing it.
     *
     * @throws NoSuchElementException if this array is empty
     */
    fun getLast(): T {
        if (isEmpty()) {
            throw NoSuchElementException("Array is empty")
        }
        return arr[getCircularIndex(end - 1)] as T
    }

    /** Returns `true` when every physical slot contains a valid element. */
    fun isFull(): Boolean {
        return count == capacity
    }

    /** Returns `true` when this array contains no elements. */
    fun isEmpty(): Boolean {
        return count == 0
    }

    private fun getCircularIndex(index: Int): Int {
        return (index + capacity) % capacity
    }
}
