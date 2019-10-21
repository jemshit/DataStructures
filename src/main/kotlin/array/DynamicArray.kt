package array

private const val startCapacity = 8

class DynamicArray<T : Any> : Iterable<T> {
    var size: Int = 0
        private set
    private var capacity: Int
    private var array: Array<T>

    constructor(initialCapacity: Int = startCapacity) {
        if (initialCapacity < 0)
            throw IllegalArgumentException()

        this.capacity = initialCapacity
        this.array = arrayOfNulls<Any>(this.capacity) as Array<T>
    }

    fun isEmpty(): Boolean = size == 0

    fun get(index: Int): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException()

        return array[index]
    }

    fun clear() {
        size = 0
        capacity = startCapacity
        array = arrayOfNulls<Any>(this.capacity) as Array<T>
    }

    fun indexOf(element: T): Int {
        for (index in 0 until size)
            if (array[index].equals(element))
                return index

        return -1
    }

    fun contains(element: T): Boolean = indexOf(element) != -1

    fun removeAt(index: Int): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException()

        // alternative: if not to shrink, you can shift right items of index to left by one and assign null to last
        val toShrink = ((size - 1) == capacity / 2) && (capacity >= startCapacity)
        capacity = if (toShrink) capacity / 2 else capacity
        val newArray = arrayOfNulls<Any>(capacity) as Array<T>
        var newIndex = 0
        var item: T? = null

        for (oldIndex in 0 until size) {
            if (oldIndex == index) {
                item = array[index]
            } else {
                newArray[newIndex] = array[oldIndex]
                newIndex += 1
            }
        }

        array = newArray
        size -= 1
        return item!!
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

    fun insert(index: Int, element: T) {
        if (index !in 0..size)
            throw IndexOutOfBoundsException()

        // expand
        if (size + 1 >= capacity) {
            capacity = if (capacity == 0) 1 else capacity * 2

            val newArray = arrayOfNulls<Any>(capacity) as Array<T>
            for (newIndex in 0 until size) {
                newArray[newIndex] = array[newIndex]
            }
            array = newArray
        }

        array[index] = element
        if (index >= size)
            size += 1
    }

    fun set(index: Int, element: T): T {
        if (index !in 0 until size)
            throw IndexOutOfBoundsException()

        val previousItem = array[index]
        array[index] = element
        return previousItem
    }

    override operator fun iterator(): ListIterator<T> {
        return object : ListIterator<T> {
            var index = 0

            override fun hasNext(): Boolean = index < size

            override fun hasPrevious(): Boolean = index > 0

            override fun next(): T = array[index]

            override fun nextIndex(): Int = index + 1

            override fun previous(): T = array[index - 1]

            override fun previousIndex(): Int = index - 11
        }
    }
}
