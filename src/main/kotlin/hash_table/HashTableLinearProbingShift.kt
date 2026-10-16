package hash_table

import java.util.ConcurrentModificationException

private const val DEFAULT_CAPACITY = 50
private const val LOAD_FACTOR = 0.75f   // grow when size >= LOAD_FACTOR * capacity

/**
 * A hash map with open addressing (linear probing): one entry per array slot — a key
 * colliding with an occupied slot walks forward to the next empty one (wrapping).
 * Unlike separate chaining there are no bucket lists, so the table must always keep
 * empty slots: the load factor stays <= LOAD_FACTOR < 1, which is what guarantees
 * probe loops terminate.
 *
 * Deletion repairs the probe chain: null the slot, then re-insert the contiguous
 * cluster after it — a key placed past a gap would be unfindable from its home.
 * O(1) expected put/get/remove. Capacity doubles above the load factor and halves
 * when nearly empty (never below the requested capacity).
 */
class HashTableLinearProbingShift<K : Any, V : Any>(
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
    private var table: Array<Entry<K, V>?>

    init {
        require(capacity > 0) { "capacity must be positive, was $capacity" }
        table = arrayOfNulls(capacity)
    }

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    /** Sign-bit mask instead of negation: -Int.MIN_VALUE overflows back negative. */
    private fun indexFor(hash: Int): Int = (hash and 0x7fffffff) % capacity

    fun containsKey(key: K): Boolean = table[findSlot(key, key.hashCode())] != null

    /** Adds key -> value, or updates the existing entry. Returns the old value, or null if the key was new. */
    fun put(key: K, value: V): V? {
        // grow BEFORE probing: probing requires a guaranteed empty slot — growing first
        // keeps the load factor <= LOAD_FACTOR < 1 (a full table = infinite probe loop).
        // No yield point before modCount += 1 below, so a live iterator can never
        // observe the resized-but-not-yet-bumped intermediate state.
        if (size >= LOAD_FACTOR * capacity)
            resize(capacity * 2)

        val hash = key.hashCode()              // computed once per operation
        val index = findSlot(key, hash)
        val existing = table[index]

        // update path: value swap only — not a structural change, iterators stay valid
        if (existing != null) {
            val oldValue = existing.value
            existing.value = value
            return oldValue
        }

        // insert path: findSlot already stopped at the first empty slot from home
        table[index] = Entry(key, value)
        size += 1
        modCount += 1

        return null
    }

    fun get(key: K): V? = table[findSlot(key, key.hashCode())]?.value

    /**
     * Probe from home: the slot holding the key, or the first empty slot after it
     * (directly usable for insertion). Terminates because the load factor < 1
     * guarantees at least one empty slot.
     */
    private fun findSlot(key: K, hash: Int): Int {
        var index = indexFor(hash)
        while (table[index] != null) {
            val entry = table[index]!!
            if (entry.hash == hash && entry.key.equals(key))   // cheap hash first
                return index
            index = (index + 1) % capacity
        }
        return index
    }

    // single pass: findSlot either finds the key or proves it absent (probe hit an empty slot).
    // Backward-shift deletion: walk the cluster after the hole, moving each entry back
    // into the hole while its home is at/below it — the classic linear-probing delete,
    // O(cluster), no re-probing of the disassembled cluster.
    fun remove(key: K): V? {
        val hash = key.hashCode()
        val index = findSlot(key, hash)
        val entry = table[index] ?: return null

        val value = entry.value
        table[index] = null
        size -= 1
        modCount += 1

        var hole = index
        var probe = (index + 1) % capacity
        while (table[probe] != null) {
            val displaced = table[probe]!!
            val home = indexFor(displaced.hash)

            // home inside (hole, probe] iff 1 <= distToHome <= distToProbe, as forward
            // circular distances from the hole — the wraparound needs no special case
            val distToHome = (home - hole + capacity) % capacity
            val distToProbe = (probe - hole + capacity) % capacity

            // move only if the probe walk crossed the hole to get here (otherwise it
            // lands before its own home and becomes unreachable): home outside (hole, probe]
            if (distToHome !in 1..distToProbe) {
                table[hole] = displaced
                table[probe] = null
                hole = probe
            }
            probe = (probe + 1) % capacity
        }

        // halve when at/below half the load factor — no grow/shrink ping-pong
        if (size * 2 <= LOAD_FACTOR * capacity && capacity / 2 >= minCapacity)
            resize(capacity / 2)

        return value
    }

    fun clear() {
        if (size == 0)
            return

        // a fresh table instead of a resize: there is nothing to rehash, and grown
        // tables shouldn't keep peak capacity forever
        table = arrayOfNulls(minCapacity)
        capacity = minCapacity
        size = 0
        modCount += 1
    }

    /** Entries are reused as-is; cached hashes mean no key.hashCode() recomputation. */
    private fun resize(newCapacity: Int) {
        capacity = newCapacity

        val newTable = arrayOfNulls<Entry<K, V>>(capacity)
        for (entry in table) {
            if (entry == null)
                continue

            var index = indexFor(entry.hash)
            while (newTable[index] != null)          // probe for a free slot
                index = (index + 1) % capacity
            newTable[index] = entry
        }
        table = newTable
    }

    /** LinkedHashSet: keys are unique by construction, and the linked variant keeps the table's scan order. */
    fun keys(): Set<K> {
        val result = LinkedHashSet<K>()
        for (entry in table)
            if (entry != null)
                result.add(entry.key)
        return result
    }

    fun values(): List<V> {
        val result = mutableListOf<V>()
        for (entry in table)
            if (entry != null)
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
            private var slotIndex = 0

            override fun hasNext(): Boolean {
                if (modCount != expectedModCount)
                    throw ConcurrentModificationException()
                advance()
                return slotIndex < capacity
            }

            override fun next(): K {
                if (modCount != expectedModCount)
                    throw ConcurrentModificationException()
                advance()
                if (slotIndex >= capacity)
                    throw NoSuchElementException()
                return table[slotIndex++]!!.key
            }

            /** skip empty slots; stops at capacity once the table is consumed */
            private fun advance() {
                while (slotIndex < capacity && table[slotIndex] == null)
                    slotIndex += 1
            }
        }
    }
}