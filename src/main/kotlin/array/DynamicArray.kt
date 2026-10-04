package array

private const val startCapacity = 8

class DynamicArray<T : Any> : Iterable<T> {
    var size: Int = 0
        private set

    val capacity: Int
        get() = array.size

    private var array: Array<Any?>

    constructor(initialCapacity: Int = startCapacity) {
        if (initialCapacity < 0)
            throw IllegalArgumentException("Illegal Capacity: $initialCapacity")

        this.array = arrayOfNulls(initialCapacity)
    }

    fun isEmpty(): Boolean = size == 0

    @Suppress("UNCHECKED_CAST")
    fun get(index: Int): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        return array[index] as T
    }

    fun clear() {
        for (i in 0 until size) {
            array[i] = null
        }
        // Alternative approach: array.fill(null, fromIndex = 0, toIndex = size)
        size = 0
    }

    fun trimToSize() {
        if (size < array.size) {
            resize(maxOf(size, startCapacity))
        }
    }

    fun indexOf(element: T): Int {
        for (index in 0 until size)
            if (array[index] == element)
                return index

        return -1
    }

    fun contains(element: T): Boolean = indexOf(element) != -1

    @Suppress("UNCHECKED_CAST")
    fun removeAt(index: Int): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        val item = array[index] as T

        // Shift items to the left in-place
        for (i in index + 1 until size) {
            array[i - 1] = array[i]
        }

        // Null out trailing element to prevent memory leak
        array[size - 1] = null
        size -= 1

        // Shrink when 1/4 full to cap / 2 to prevent thrashing
        if (size <= array.size / 4 && array.size / 2 >= startCapacity) {
            resize(array.size / 2)
        }

        return item
    }

    fun removeFirst(): T {
        if (isEmpty())
            throw NoSuchElementException("Array is empty")
        return removeAt(0)
    }

    fun removeLast(): T {
        if (isEmpty())
            throw NoSuchElementException("Array is empty")
        return removeAt(size - 1)
    }

    fun remove(element: T): Boolean {
        val index = indexOf(element)
        if (index == -1)
            return false

        removeAt(index)
        return true
    }

    fun add(element: T): Boolean {
        insert(size, element)
        return true
    }

    fun addFirst(element: T) = insert(0, element)

    fun addLast(element: T) = insert(size, element)

    fun insert(index: Int, element: T) {
        if (index !in 0..size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        // Expand if capacity is full
        if (size == array.size) {
            resize(if (array.isEmpty()) 1 else array.size * 2)
        }

        // Shift elements to the right to make room
        for (i in size - 1 downTo index) {
            array[i + 1] = array[i]
        }

        array[index] = element
        size += 1
    }

    @Suppress("UNCHECKED_CAST")
    fun set(index: Int, element: T): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        val previousItem = array[index] as T
        array[index] = element
        return previousItem
    }

    private fun resize(newCapacity: Int) {
        val newArray = arrayOfNulls<Any?>(newCapacity)
        for (i in 0 until size) {
            newArray[i] = array[i]
        }
        array = newArray
    }

    override operator fun iterator(): ListIterator<T> {
        return object : ListIterator<T> {
            var index = 0

            override fun hasNext(): Boolean = index < size
            override fun hasPrevious(): Boolean = index > 0

            @Suppress("UNCHECKED_CAST")
            override fun next(): T {
                if (!hasNext())
                    throw NoSuchElementException()
                return array[index++] as T
            }

            override fun nextIndex(): Int = index

            @Suppress("UNCHECKED_CAST")
            override fun previous(): T {
                if (!hasPrevious())
                    throw NoSuchElementException()
                return array[--index] as T
            }

            override fun previousIndex(): Int = index - 1
        }
    }
}
