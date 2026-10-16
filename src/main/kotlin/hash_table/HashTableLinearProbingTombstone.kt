package hash_table

import java.util.ConcurrentModificationException

private const val DEFAULT_CAPACITY = 50
private const val LOAD_FACTOR = 0.75f   // grow when size + 1 > LOAD_FACTOR * capacity
private const val TOMBSTONE_FACTOR = 0.25f   // rebuild when tombstones > TOMBSTONE_FACTOR * capacity

// Probe termination invariant: LOAD_FACTOR + TOMBSTONE_FACTOR <= 1 keeps at least one
// slot EMPTY at all times (max live = 0.75 * capacity, max dead = 0.25 * capacity).
// These constants are coupled — changing one alone can void the guarantee.

/**
 * A hash map with open addressing (linear probing) and tombstone deletion: removing a key
 * marks its slot DELETED instead of nulling it — O(1) removal, no cluster repair. A lookup
 * probe must keep walking past tombstones (a tombstone may hide whatever probed past it),
 * so live entries stay findable; the cost is that dead slots lengthen probe chains until
 * a rebuild rehashes and discards them.
 *
 * Contrast with HashTableLinearProbingShift, whose remove walks the cluster and moves
 * entries back into the hole — slower delete, but chains stay compact and no rebuild is
 * ever needed. O(1) expected put/get/remove. Capacity doubles above the load factor and
 * halves when nearly empty (never below the requested capacity).
 */
class HashTableLinearProbingTombstone<K : Any, V : Any>(
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

    // Slot states: empty = null, live = Entry, deleted = TOMBSTONE — one object for all
    // deleted slots so identity (===) tells it apart from any live entry.
    private object TOMBSTONE

    private var capacity: Int = capacity
    private val minCapacity: Int = capacity
    private var size: Int = 0            // live entries only

    /** Dead slots currently in the table; drives the periodic rebuild. */
    private var tombstones: Int = 0

    /**
     * Bumped by every structural change: insert, remove, clear, rebuild (NOT value updates).
     * Iterators snapshot it and throw ConcurrentModificationException if it moves.
     */
    private var modCount: Int = 0
    private var table: Array<Any?>

    init {
        require(capacity > 0) { "capacity must be positive, was $capacity" }
        table = arrayOfNulls(capacity)
    }

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    /** Sign-bit mask instead of negation: -Int.MIN_VALUE overflows back negative. */
    private fun indexFor(hash: Int): Int = (hash and 0x7fffffff) % capacity

    fun containsKey(key: K): Boolean = table[findSlot(key, key.hashCode())] is Entry<*, *>

    /** Adds key -> value, or updates the existing entry. Returns the old value, or null if the key was new. */
    fun put(key: K, value: V): V? {
        val hash = key.hashCode()              // computed once per operation
        var index = indexFor(hash)
        var firstTombstone = -1                 // remember the earliest reusable slot

        // probe for the key; an update path stays fully non-structural — no grow, no
        // modCount bump, iterators stay valid even with the table sitting at the boundary
        while (true) {
            when (val slot = table[index]) {
                null -> break                   // empty: the key is absent
                TOMBSTONE -> {                 // keep probing: the key may live past it
                    if (firstTombstone == -1)
                        firstTombstone = index
                }
                else -> {
                    val entry = slot as Entry<*, *>
                    if (entry.hash == hash && entry.key.equals(key)) {   // cheap hash first
                        @Suppress("UNCHECKED_CAST")
                        entry as Entry<K, V>

                        // update path: value swap only — not a structural change, iterators stay valid
                        val oldValue = entry.value
                        entry.value = value
                        return oldValue
                    }
                }
            }
            index = (index + 1) % capacity
        }

        // insert path: grow BEFORE placing — placing requires a guaranteed empty slot,
        // and probes terminate only on a null slot. Tombstones occupy slots exactly like
        // live entries, so the load factor counts them both (a full table = infinite walk).
        // Rebuild discards tombstones and invalidates iterators — correct, this IS structural.
        if (size + tombstones + 1 > LOAD_FACTOR * capacity) {
            rebuild(capacity * 2)

            // everything moved: re-probe for a slot. No tombstones exist after a rebuild,
            // so this walk is a plain null hunt
            index = indexFor(hash)
            while (table[index] != null)
                index = (index + 1) % capacity
            firstTombstone = -1
        }

        // place at the tombstone remembered on the way (shortens future probes) if the
        // probe passed one, else at the null slot the probe stopped at
        val reuse = if (firstTombstone != -1) firstTombstone else index
        if (firstTombstone != -1)
            tombstones -= 1        // the reused tombstone becomes live again
        table[reuse] = Entry(key, value)
        size += 1
        modCount += 1

        return null
    }

    fun get(key: K): V? {
        val slot = table[findSlot(key, key.hashCode())]
        if (slot !is Entry<*, *>) return null
        @Suppress("UNCHECKED_CAST")
        return (slot as Entry<K, V>).value
    }

    /**
     * Probe from home for [key]: the slot holding it, or the first EMPTY slot (proof of
     * absence). Walks PAST tombstones, since a tombstone may hide whatever probed past
     * it. Terminates because the load factor < 1 guarantees at least one empty slot.
     * Lookup-only: put has its own loop, which also remembers the first reusable tombstone.
     */
    private fun findSlot(key: K, hash: Int): Int {
        var index = indexFor(hash)
        while (true) {
            val slot = table[index]
            if (slot == null)
                return index                   // empty slot: absent, probe ends
            if (slot !== TOMBSTONE) {
                val entry = slot as Entry<*, *>
                if (entry.hash == hash && entry.key.equals(key))   // cheap hash first
                    return index
            }
            index = (index + 1) % capacity     // tombstone or mismatch: keep probing
        }
    }

    // O(1) removal: mark the slot deleted. No cluster repair — the tombstone keeps
    // whatever probed past this slot findable. Dead slots extend probe chains until
    // a rebuild collects them.
    fun remove(key: K): V? {
        val hash = key.hashCode()
        val slot = findSlot(key, hash)
        val entry = table[slot] as? Entry<*, *> ?: return null

        @Suppress("UNCHECKED_CAST")
        val value = (entry as Entry<K, V>).value
        table[slot] = TOMBSTONE            // not null: null would cut the probe chain
        size -= 1
        tombstones += 1
        modCount += 1

        // occupancy check (size + tombstones): a tombstone fills a slot exactly like a live
        // entry — one more tombstone can be the one that leaves no null slot for probes
        if (size + tombstones > LOAD_FACTOR * capacity && capacity * 2 > 0)
            rebuild(capacity * 2)

        // halve when at/below half the load factor — no grow/shrink ping-pong. The shrink
        // rebuild doubles as the tombstone rebuild: it discards dead slots too
        if (size + tombstones <= LOAD_FACTOR / 2 * capacity && capacity / 2 >= minCapacity)
            rebuild(capacity / 2)

        // dead slots eventually dominate the chains — rebuild them away
        if (tombstones > TOMBSTONE_FACTOR * capacity)
            rebuild(capacity)

        return value
    }

    fun clear() {
        if (size == 0 && tombstones == 0)
            return

        // a fresh table instead of a rebuild: nothing to rehash, and grown tables
        // shouldn't keep peak capacity forever
        table = arrayOfNulls(minCapacity)
        capacity = minCapacity
        size = 0
        tombstones = 0
        modCount += 1
    }

    /**
     * Rehashes every live entry into a fresh table of [newCapacity] slots, discarding all
     * tombstones. Entries are reused as-is; cached hashes mean no key.hashCode() recomputation.
     */
    private fun rebuild(newCapacity: Int) {
        capacity = newCapacity

        val newTable = arrayOfNulls<Any?>(capacity)
        for (slot in table) {
            val entry = slot as? Entry<*, *> ?: continue   // skip null and tombstones
            @Suppress("UNCHECKED_CAST")
            entry as Entry<K, V>

            var index = indexFor(entry.hash)
            while (newTable[index] != null)          // probe for a free slot
                index = (index + 1) % capacity
            newTable[index] = entry
        }
        table = newTable
        tombstones = 0
        modCount += 1
    }

    /** LinkedHashSet: keys are unique by construction, and the linked variant keeps the table's scan order. */
    fun keys(): Set<K> {
        val result = LinkedHashSet<K>()
        for (slot in table)
            if (slot is Entry<*, *>)
                result.add((slot as Entry<K, V>).key)
        return result
    }

    fun values(): List<V> {
        val result = mutableListOf<V>()
        for (slot in table)
            if (slot is Entry<*, *>)
                result.add((slot as Entry<K, V>).value)
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
                val entry = table[slotIndex++] as Entry<*, *>
                @Suppress("UNCHECKED_CAST")
                return (entry as Entry<K, V>).key
            }

            /** skip empty and tombstone slots; stops at capacity once the table is consumed */
            private fun advance() {
                while (slotIndex < capacity && table[slotIndex] !is Entry<*, *>)
                    slotIndex += 1
            }
        }
    }
}