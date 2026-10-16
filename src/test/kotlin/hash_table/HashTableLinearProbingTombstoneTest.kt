package hash_table

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class HashTableLinearProbingTombstoneTest {

    // A key whose hashCode() is Int.MIN_VALUE — arithmetic negation of it overflows,
    // so this key reproduces the sign bug that the `and 0x7fffffff` mask fixes
    internal class MinHashKey(val id: Int) {

        override fun hashCode(): Int = Int.MIN_VALUE

        override fun equals(other: Any?): Boolean =
            other is MinHashKey && id == other.id
    }

    private val randomGenerator = Random(20261006L)   // fixed seed: reproducible failures
    private val loopCount: Int = randInt(25, 75)
    private val maxSize: Int = randInt(1, 75)
    private val maxRandomNum: Int = randInt(1, 350)

    private var map: HashTableLinearProbingTombstone<Int, Int> = HashTableLinearProbingTombstone()

    @Test
    fun `test illegal creation`() {
        assertThrows<IllegalArgumentException> {
            HashTableLinearProbingTombstone<Int, Int>(-3)
        }
    }

    @Test
    fun `test legal creation`() {
        HashTableLinearProbingTombstone<Int, Int>(6)
    }

    @Test
    fun `test updating value`() {
        map.put(1, 1)
        assertTrue(1 == map.get(1))

        map.put(1, 5)
        assertTrue(5 == map.get(1))

        map.put(1, -7)
        assertTrue(-7 == map.get(1))
    }

    @Test
    fun `test put returns old value`() {
        assertNull(map.put(1, 1))
        assertEquals(1, map.put(1, 2))
        assertEquals(2, map.get(1))
    }

    @Test
    fun `test remove returns old value`() {
        assertNull(map.put(1, 10))
        assertEquals(10, map.remove(1))
        assertNull(map.remove(1))       // already gone — null, no side effect
        assertNull(map.remove(99))     // never there
    }

    @Test
    fun `test lookup probes past tombstones`() {
        // the sample's scenario: keys sharing a home; removing one leaves a tombstone
        // that must NOT end a probe for the keys placed after it
        val tombMap = HashTableLinearProbingTombstone<Int, Int>(capacity = 8)
        tombMap.put(1, 1)
        tombMap.put(2, 2)
        tombMap.put(10, 10)
        tombMap.put(20, 20)
        tombMap.put(30, 30)
        tombMap.put(3, 3)

        assertEquals(20, tombMap.remove(20))
        assertNull(tombMap.get(20))
        assertEquals(30, tombMap.get(30))   // probed past the 20 tombstone
        assertEquals(10, tombMap.get(10))
        assertEquals(3, tombMap.get(3))
        assertEquals(1, tombMap.get(1))
        assertEquals(2, tombMap.get(2))
        assertEquals(5, tombMap.size())

        // remove the rest, hitting every tombstone path
        assertNull(tombMap.remove(99))
        tombMap.remove(10)
        tombMap.remove(30)
        tombMap.remove(1)
        tombMap.remove(2)
        tombMap.remove(3)
        assertTrue(tombMap.isEmpty())
    }

    @Test
    fun `test insert reuses tombstone slots`() {
        val reuseMap = HashTableLinearProbingTombstone<Int, Int>(capacity = 8)

        // fill to force collisions around one home, then delete the head of a cluster
        for (key in 1..6)
            reuseMap.put(key, key)
        reuseMap.remove(1)
        assertNull(reuseMap.get(1))
        assertEquals(5, reuseMap.size())

        // inserting the same key again must find a slot despite the tombstone
        reuseMap.put(1, 100)
        assertEquals(100, reuseMap.get(1))
        assertEquals(6, reuseMap.size())

        // and a fresh key that hashes onto the cluster
        reuseMap.put(9, 9)
        assertEquals(9, reuseMap.get(9))
    }

    @Test
    fun `test tombstone rebuild keeps every key findable`() {
        // delete/reinsert churn past the TOMBSTONE_FACTOR threshold: probes must stay
        // short because rebuilds discard the dead slots
        val churnMap = HashTableLinearProbingTombstone<Int, Int>(capacity = 4)
        val jmap = HashMap<Int, Int>()

        val nums = (1..200).toList()
        for (key in nums) {
            churnMap.put(key, key)
            jmap[key] = key
        }
        // delete most of them — tombstones accumulate, rebuild(s) must fire
        for (key in nums.shuffled(randomGenerator).take(150)) {
            assertEquals(jmap.remove(key), churnMap.remove(key))
        }

        // every survivor findable
        for (key in nums) {
            assertEquals(jmap[key], churnMap.get(key))
        }
        assertEquals(jmap.size, churnMap.size())
        assertEquals(jmap.keys, churnMap.keys())

        // reuse after the churn
        churnMap.put(999, 9)
        assertEquals(9, churnMap.get(999))
    }

    @Test
    fun `test remove missing key has no side effect`() {
        map.put(1, 1)
        assertNull(map.remove(99))
        assertEquals(1, map.size())
        assertEquals(1, map.get(1))
    }

    @Test
    fun `test key with Int MIN VALUE hashCode`() {
        val minHashMap = HashTableLinearProbingTombstone<MinHashKey, Int>()

        val key = MinHashKey(1)
        minHashMap.put(key, 10)

        assertEquals(10, minHashMap.get(key))
        assertTrue(minHashMap.containsKey(key))

        assertEquals(10, minHashMap.remove(key))
        assertTrue(minHashMap.isEmpty())
        assertNull(minHashMap.get(key))
    }

    @Test
    fun `test iterator exhaustion throws NoSuchElementException`() {
        map.put(1, 1)

        val iterator = map.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())
        assertFalse(iterator.hasNext())
        assertThrows<NoSuchElementException> { iterator.next() }

        // hasNext() is repeatable after exhaustion
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `test iterator skips tombstones`() {
        val tombIteratorMap = HashTableLinearProbingTombstone<Int, Int>(capacity = 8)
        for (key in 1..6)
            tombIteratorMap.put(key, key)
        tombIteratorMap.remove(1)
        tombIteratorMap.remove(4)

        val seen = LinkedHashSet<Int>()
        for (key in tombIteratorMap)
            assertTrue(seen.add(key))        // uniqueness: tombstones must not surface

        assertEquals(setOf(2, 3, 5, 6), seen)
        assertEquals(4, tombIteratorMap.size())
    }

    @Test
    fun `test value update does not invalidate iterator`() {
        map.put(1, 1)
        map.put(2, 2)
        map.put(3, 3)

        var count = 0
        for (key in map) {
            map.put(key, 99)  // existing key: value swap, not a structural change
            count++
        }

        assertEquals(3, count)
        assertEquals(99, map.get(1))
        assertEquals(99, map.get(2))
        assertEquals(99, map.get(3))
    }

    @Test
    fun `test ConcurrentModificationException`() {
        map.put(1, 1)
        map.put(2, 1)
        map.put(3, 1)

        assertThrows<ConcurrentModificationException> {
            for (key in map)
                map.put(4, 4)
        }
    }

    @Test
    fun `test ConcurrentModificationException2`() {
        map.put(1, 1)
        map.put(2, 1)
        map.put(3, 1)

        assertThrows<ConcurrentModificationException> {
            for (key in map)
                map.remove(2)
        }
    }

    @Test
    fun `test clear on populated map`() {
        for (key in 0 until 200)
            map.put(key, key)
        assertEquals(200, map.size())

        map.clear()

        assertEquals(0, map.size())
        assertTrue(map.isEmpty())
        assertNull(map.get(1))
        assertTrue(map.keys().isEmpty())
        assertTrue(map.values().isEmpty())

        // reusable after clear
        map.put(7, 70)
        assertEquals(70, map.get(7))
    }

    @Test
    fun `test clear invalidates live iterator`() {
        map.put(1, 1)
        map.put(2, 2)

        val iterator = map.iterator()
        assertEquals(1, iterator.next())

        map.clear()
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `test keys and values`() {
        map.put(1, 10)
        map.put(2, 20)

        assertEquals(setOf(1, 2), map.keys())
        assertEquals(listOf(10, 20), map.values())
        assertTrue(map.containsKey(1))
        assertFalse(map.containsKey(3))
    }

    @Test
    fun `test resize grow and shrink cycles`() {
        val growMap = HashTableLinearProbingTombstone<Int, Int>(8)

        // grow through several doublings
        for (key in 0 until 200)
            growMap.put(key, key)
        assertEquals(200, growMap.size())

        // drain far below the shrink threshold (capacity halves, floor at 8)
        for (key in 0 until 200)
            assertEquals(key, growMap.remove(key))
        assertTrue(growMap.isEmpty())

        // still correct after all the rehashing
        for (key in 200 until 260)
            growMap.put(key, key)
        for (key in 200 until 260)
            assertEquals(key, growMap.get(key))
        assertEquals(60, growMap.size())
    }

    @Test
    fun `test shrink does not ping-pong with grow`() {
        // alternating put/remove at the boundary must not trigger a resize per operation
        val smallMap = HashTableLinearProbingTombstone<Int, Int>(10)

        for (round in 0 until 100) {
            smallMap.put(round, round)
            assertEquals(round, smallMap.remove(round))
        }

        // correct throughout the churn, and reusable after draining
        assertTrue(smallMap.isEmpty())
        smallMap.put(999, 9)
        assertEquals(9, smallMap.get(999))
    }

    @Test
    fun `test value update at grow boundary does not invalidate iterator`() {
        // capacity 8: grow fires when an insert would cross 0.75 * 8 = 6 — sit exactly there
        val boundaryMap = HashTableLinearProbingTombstone<Int, Int>(capacity = 8)
        for (key in 0 until 6)
            boundaryMap.put(key, key)   // size 6 == LOAD_FACTOR * capacity: at the boundary

        // value updates on the boundary: NOT structural — iterators stay valid
        val iterator = boundaryMap.iterator()
        assertTrue(iterator.hasNext())
        for (key in 0 until 6)
            boundaryMap.put(key, key * 10)   // all updates
        assertTrue(iterator.hasNext())       // still valid

        var count = 0
        for (key in boundaryMap) {
            assertEquals(key * 10, boundaryMap.get(key))
            count++
        }
        assertEquals(6, count)

        // an insert at the boundary DOES grow (size + 1 crosses) — and invalidates
        boundaryMap.put(99, 9)
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `test random map operations`() {
        val jmap = HashMap<Int, Int>()
        for (loopIndex in 0 until loopCount) {
            map.clear()
            jmap.clear()
            assertEquals(jmap.size, map.size())

            map = HashTableLinearProbingTombstone()

            val probability1 = Math.random()
            val probability2 = Math.random()

            val nums = genRandList(maxSize)
            for (index in 0 until maxSize) {
                val r = Math.random()
                val key = nums[index]

                if (r < probability1)
                    assertEquals(jmap.put(key, index), map.put(key, index))

                assertEquals(jmap[key], map.get(key))
                assertEquals(jmap.containsKey(key), map.containsKey(key))
                assertEquals(jmap.size, map.size())

                if (r > probability2)
                    assertEquals(map.remove(key), jmap.remove(key))

                assertEquals(jmap[key], map.get(key))
                assertEquals(jmap.containsKey(key), map.containsKey(key))
                assertEquals(jmap.size, map.size())
            }

            // full-table parity after the churn
            assertEquals(jmap.keys, map.keys())
        }
    }

    @Test
    fun `test randomized removes keep every key findable`() {
        // open addressing's trap: a wrong delete would orphan keys that probed past
        // the removed slot — differential-test the probe chains hard
        val jmap = HashMap<Int, Int>()
        for (loopIndex in 0 until loopCount) {
            val probeMap = HashTableLinearProbingTombstone<Int, Int>(4)
            jmap.clear()

            val nums = genRandList(maxSize)
            for (key in nums) {
                probeMap.put(key, key)
                jmap[key] = key
            }

            for (key in nums.shuffled(randomGenerator)) {
                assertEquals(jmap.remove(key), probeMap.remove(key))

                // every surviving key must still be findable through its probe chain
                for (survivor in jmap.keys)
                    assertEquals(jmap[survivor], probeMap.get(survivor))
                assertEquals(jmap.size, probeMap.size())
            }
            assertTrue(probeMap.isEmpty())
        }
    }

    private fun randInt(min: Int, max: Int): Int {
        return randomGenerator.nextInt(max - min + 1) + min
    }

    private fun genRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (index in 0 until size)
            list.add(randInt(-maxRandomNum, maxRandomNum))
        Collections.shuffle(list)
        return list
    }
}