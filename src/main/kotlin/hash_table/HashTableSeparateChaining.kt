package hash_table

import linked_list.DoublyLinkedList

private const val DEFAULT_CAPACITY = 50
private const val LOAD_FACTOR = 0.75f   // grow when size >= LOAD_FACTOR * capacity

/**
 * A hash map with separate chaining: an array of buckets where each bucket is a
 * DoublyLinkedList of the entries whose hash mapped to that index. O(1) expected
 * put/get/remove — average chain length is the load factor. Capacity doubles above
 * the load factor and halves when nearly empty (never below the requested capacity);
 * unlike java.util.HashMap we use plain % indexing and no treeification.
 */
class HashTableSeparateChaining<K : Any, V : Any>(
    capacity: Int = DEFAULT_CAPACITY
) : Iterable<K> {

    /**
     * key.hashCode() cached once: lookups compare it (cheap) before the expensive
     * key.equals, and resize never recomputes it. Identity equals, like HashMap.Node.
     */
    class Entry<K : Any, V : Any>(val key: K, var value: V) {
        val hash: Int = key.hashCode()

        override fun toString(): String = "$key -> $value"
    }

    private var capacity: Int = capacity
    private val minCapacity: Int = capacity
    private var size: Int = 0

    /**
     * Bumped by every structural change: insert, remove, clear (NOT value updates).
     * Iterators snapshot it and throw ConcurrentModificationException if it moves.
     */
    private var modCount: Int = 0
    private var table: Array<DoublyLinkedList<Entry<K, V>>?>

    init {
        require(capacity > 0) { "capacity must be positive, was $capacity" }
        table = arrayOfNulls(capacity)
    }

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    /** Sign-bit mask instead of negation: -Int.MIN_VALUE overflows back negative. */
    private fun indexFor(hash: Int): Int = (hash and 0x7fffffff) % capacity

    fun containsKey(key: K): Boolean = getEntry(key, key.hashCode()) != null

    /** Adds key -> value, or updates the existing entry. Returns the old value, or null if the key was new. */
    fun put(key: K, value: V): V? {
        val hash = key.hashCode()              // computed once per operation
        val index = indexFor(hash)
        val bucket = table[index]

        // update path: value swap only — not a structural change, iterators stay valid
        if (bucket != null) {
            for (entry in bucket) {
                if (entry.hash == hash && entry.key.equals(key)) {   // cheap hash first
                    val oldValue = entry.value
                    entry.value = value
                    return oldValue
                }
            }
        }

        // insert path: append to the chain, creating the bucket lazily
        val chain = bucket ?: DoublyLinkedList<Entry<K, V>>().also { table[index] = it }
        chain.addLast(Entry(key, value))

        size += 1
        modCount += 1

        // grow when size crosses the load factor (live comparison, nothing cached to floor)
        if (size >= LOAD_FACTOR * capacity && capacity * 2 > 0)
            resize(capacity * 2)

        return null
    }

    fun get(key: K): V? = getEntry(key, key.hashCode())?.value

    /** The entry for [key], or null when absent. */
    private fun getEntry(key: K, hash: Int): Entry<K, V>? {
        val bucket = table[indexFor(hash)] ?: return null
        for (entry in bucket) {
            if (entry.hash == hash && entry.key.equals(key))
                return entry
        }
        return null
    }

    /** Single pass: walk with the index, removeAt unlinks directly — Entry needs no equals. */
    fun remove(key: K): V? {
        val hash = key.hashCode()
        val bucket = table[indexFor(hash)] ?: return null

        var entryIndex = 0
        for (entry in bucket) {
            if (entry.hash == hash && entry.key.equals(key)) {
                val value = entry.value
                bucket.removeAt(entryIndex)   // unlink by position — the walk already found it,
                // no second equals-based search (Entry needs no equals)
                size -= 1
                modCount += 1

                // halve when at/below half the load factor: the halved table then sits below
                // the grow threshold — no grow/shrink ping-pong — and never below minCapacity
                if (size * 2 <= LOAD_FACTOR * capacity && capacity / 2 >= minCapacity)
                    resize(capacity / 2)

                return value
            }
            entryIndex += 1
        }
        return null
    }

    fun clear() {
        if (size == 0)
            return

        for (index in table.indices)
            table[index] = null              // chains become unreachable; GC collects them
        size = 0
        modCount += 1
        if (capacity > minCapacity)          // grown tables don't keep peak capacity forever
            resize(minCapacity)
    }

    /** Entries are reused as-is; cached hashes mean no key.hashCode() recomputation. */
    private fun resize(newCapacity: Int) {
        capacity = newCapacity

        val newTable = arrayOfNulls<DoublyLinkedList<Entry<K, V>>>(capacity)
        for (bucket in table) {
            if (bucket == null || bucket.isEmpty())
                continue

            for (entry in bucket) {
                val index = indexFor(entry.hash)
                val newBucket = newTable[index] ?: DoublyLinkedList<Entry<K, V>>().also { newTable[index] = it }
                newBucket.addLast(entry)
            }
        }
        table = newTable
    }

    /**
     * LinkedHashSet: keys are unique by construction, and the linked variant keeps
     * the table's scan order — the same order [iterator] produces.
     */
    fun keys(): Set<K> {
        val result = LinkedHashSet<K>()
        for (bucket in table)
            if (bucket != null)
                for (entry in bucket)
                    result.add(entry.key)
        return result
    }

    fun values(): List<V> {
        val result = mutableListOf<V>()
        for (bucket in table)
            if (bucket != null)
                for (entry in bucket)
                    result.add(entry.value)
        return result
    }

    /**
     * Fail-fast cursor over the keys. modCount is checked in both hasNext and next
     * (stricter than java.util, which checks only next).
     */
    override fun iterator(): Iterator<K> {
        return object : Iterator<K> {
            private val expectedModCount = modCount   // snapshot at creation
            private var bucketIndex = 0              // next bucket to draw from
            private var entryIterator: Iterator<Entry<K, V>>? = null

            override fun hasNext(): Boolean {
                if (modCount != expectedModCount)
                    throw ConcurrentModificationException()
                advance()
                return entryIterator != null
            }

            override fun next(): K {
                if (modCount != expectedModCount)
                    throw ConcurrentModificationException()
                advance()
                return entryIterator?.next()?.key ?: throw NoSuchElementException()
            }

            /** moves entryIterator to the next bucket that still has entries; null once consumed */
            private fun advance() {
                while (entryIterator == null || !entryIterator!!.hasNext()) {
                    if (bucketIndex >= capacity) {
                        entryIterator = null
                        return
                    }
                    val bucket = table[bucketIndex]
                    bucketIndex += 1
                    if (bucket != null && !bucket.isEmpty())
                        entryIterator = bucket.iterator()
                }
            }
        }
    }
}