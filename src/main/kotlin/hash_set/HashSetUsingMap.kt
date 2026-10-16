package hash_set

import hash_table.HashTableSeparateChaining

private object DUMMY

class HashSetUsingMap<K : Any>(
    private val map: HashTableSeparateChaining<K, Any> = HashTableSeparateChaining()
) : Iterable<K> {

    fun put(key: K) {
        map.put(key, DUMMY)
    }

    fun remove(key: K) {
        map.remove(key)
    }

    fun contains(key: K): Boolean {
        return map.containsKey(key)
    }

    fun size(): Int {
        return map.size()
    }

    fun isEmpty(): Boolean = map.isEmpty()

    override fun iterator(): Iterator<K> = map.iterator()
}
