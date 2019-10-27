package hash_table

import linked_list.DoublyLinkedList


private const val defaultCapacity = 50
private const val defaultLoadFactor: Float = 0.75f

class HashTableSeparateChaining<K : Any, V : Any> : Iterable<K> {

    class Entry<K : Any, V : Any> {
        val hash: Int
        val key: K
        var value: V

        constructor(key: K, value: V) {
            this.key = key
            this.value = value
            this.hash = key.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other == null)
                return false

            val otherEntry = other as Entry<K, V>
            if (otherEntry.hash != this.hash)
                return false

            return this.key.equals(other.key)
        }

        override fun toString(): String {
            return "$key -> $value"
        }
    }

    private var loadFactor: Float = defaultLoadFactor
    private var capacity: Int = defaultCapacity
    private var sizeThreshold: Int = (loadFactor * capacity).toInt()
    private var size: Int = 0
    private lateinit var table: Array<DoublyLinkedList<Entry<K, V>>?>

    constructor(
        capacity: Int = defaultCapacity,
        loadFactor: Float = defaultLoadFactor
    ) {
        if (capacity <= 0)
            throw IllegalArgumentException()
        if (loadFactor <= 0.1f)
            throw IllegalArgumentException()

        this.capacity = capacity
        this.loadFactor = loadFactor
        this.sizeThreshold = (loadFactor * capacity).toInt()
        this.table = arrayOfNulls(capacity)
    }

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    private fun getIndex(key: K): Int {
        val hash = key.hashCode()
        val positiveHash = if (hash >= 0) hash else hash * (-1)
        return positiveHash % capacity
    }

    fun containsKey(key: K): Boolean = internalGet(key) != null

    fun clear() {
        if (size == 0) return

        for (bucketIndex in 0 until capacity) {
            val bucket = table.get(bucketIndex)
            if (bucket == null || bucket.isEmpty())
                continue
            bucket.clear()
            table[bucketIndex] = null
        }
        size = 0
    }

    fun put(key: K, value: V): V? {
        val oldEntry: Entry<K, V>? = internalGet(key)
        if (oldEntry != null) {
            val oldValue = oldEntry.value
            oldEntry.value = value
            return oldValue
        }

        val bucketIndex = getIndex(key)
        var bucket = table.get(bucketIndex)
        if (bucket == null)
            bucket = DoublyLinkedList<Entry<K, V>>()
        bucket.addLast(Entry(key, value))
        table[bucketIndex] = bucket

        size += 1
        if (size >= sizeThreshold)
            resizeTable()

        return null
    }

    fun get(key: K): V? = internalGet(key)?.value

    private fun internalGet(key: K): Entry<K, V>? {
        if (size == 0) return null

        val bucketIndex = getIndex(key)
        val bucket = table.get(bucketIndex)
        if (bucket == null || bucket.isEmpty())
            return null

        val iterator = bucket.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (key.equals(entry.key))
                return entry
        }

        return null
    }

    fun remove(key: K): V? {
        if (size == 0) return null

        val entry = internalGet(key)
        if (entry != null) {
            val bucketIndex = getIndex(key)
            val bucket = table.get(bucketIndex)
            bucket!!.remove(entry)
            size -= 1
        }

        return entry?.value
    }

    private fun resizeTable() {
        this.capacity *= 2
        this.sizeThreshold = (loadFactor * capacity).toInt()
        val newTable: Array<DoublyLinkedList<Entry<K, V>>?> = arrayOfNulls(capacity)

        for (bucketIndex in 0 until capacity / 2) {
            val bucket = table.get(bucketIndex)
            if (bucket != null && !bucket.isEmpty()) {
                val iterator = bucket.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    val newIndex = getIndex(entry.key)
                    var newBucket = newTable.get(newIndex)
                    if (newBucket == null)
                        newBucket = DoublyLinkedList()
                    newBucket.addLast(entry)
                    newTable[newIndex] = bucket
                }
            }
        }

        table = newTable
    }

    fun keys(): List<K> {
        if (size == 0) return emptyList()
        val result = mutableListOf<K>()

        for (bucketIndex in 0 until capacity) {
            val bucket = table.get(bucketIndex)
            if (bucket != null && !bucket.isEmpty()) {
                val iterator = bucket.iterator()
                while (iterator.hasNext())
                    result.add(iterator.next().key)
            }
        }

        return result
    }

    fun values(): List<V> {
        if (size == 0) return emptyList()
        val result = mutableListOf<V>()

        for (bucketIndex in 0 until capacity) {
            val bucket = table.get(bucketIndex)
            if (bucket != null && !bucket.isEmpty()) {
                val iterator = bucket.iterator()
                while (iterator.hasNext())
                    result.add(iterator.next().value)
            }
        }

        return result
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("{")

        for (bucketIndex in 0 until capacity) {
            val bucket = table.get(bucketIndex)
            if (bucket != null && !bucket.isEmpty()) {
                val iterator = bucket.iterator()
                while (iterator.hasNext())
                    stringBuilder.append(iterator.next().toString() + ", ")
            }
        }

        stringBuilder.append("}")
        return stringBuilder.toString()
    }

    // borrowed iterator code
    override fun iterator(): Iterator<K> {
        val initialSize = size()

        return object : Iterator<K> {
            var bucketIndex = 0
            var bucketIterator = if (table[0] == null) null else table[0]!!.iterator()

            override fun hasNext(): Boolean {
                if (initialSize != size)
                    throw ConcurrentModificationException()

                if (bucketIterator == null || !bucketIterator!!.hasNext()) {
                    bucketIndex += 1
                    while (bucketIndex < capacity) {
                        if (table[bucketIndex] != null) {
                            val nextIterator = table[bucketIndex]!!.iterator()
                            if (nextIterator.hasNext()) {
                                bucketIterator = nextIterator
                                break
                            }
                        }
                        bucketIndex += 1
                    }
                }
                return bucketIndex < capacity
            }

            override fun next(): K {
                return bucketIterator!!.next().key
            }
        }
    }
}