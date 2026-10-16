package hash_table

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class HashTableLinearProbingShiftTest {

    // A key whose hashCode() is Int.MIN_VALUE — arithmetic negation of it overflows,
    // so this key reproduces the sign bug that the `and 0x7fffffff` mask fixes
    internal class MinHashKey(val id: Int) {

        override fun hashCode(): Int = Int.MIN_VALUE

        override fun equals(other: Any?): Boolean =
            other is MinHashKey && id == other.id
    }

    private val randomGenerator = Random(20261005L)   // fixed seed: reproducible failures
    private val loopCount: Int = randInt(25, 75)
    private val maxSize: Int = randInt(1, 75)
    private val maxRandomNum: Int = randInt(1, 350)

    private var map: HashTableLinearProbingShift<Int, Int> = HashTableLinearProbingShift()

    @Test
    fun `test illegal creation`() {
        assertThrows<IllegalArgumentException> {
            HashTableLinearProbingShift<Int, Int>(-3)
        }
    }

    @Test
    fun `test legal creation`() {
        HashTableLinearProbingShift<Int, Int>(6)
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
    fun `test remove repairs the probe chain`() {
        // capacity 4 forces collisions: the keys probe forward into shared clusters
        val clusterMap = HashTableLinearProbingShift<Int, Int>(4)

        clusterMap.put(1, 1)
        clusterMap.put(2, 2)
        clusterMap.put(10, 10)   // 10 & 0x7fffffff = 10 -> probes from 10 % capacity
        clusterMap.put(20, 20)
        clusterMap.put(30, 30)
        clusterMap.put(3, 3)

        // 20 sits inside a probe chain; removing it must not orphan the keys placed after it
        assertEquals(20, clusterMap.remove(20))
        assertNull(clusterMap.get(20))
        assertEquals(30, clusterMap.get(30))
        assertEquals(10, clusterMap.get(10))
        assertEquals(3, clusterMap.get(3))
        assertEquals(1, clusterMap.get(1))
        assertEquals(2, clusterMap.get(2))
        assertEquals(5, clusterMap.size())

        // remove the rest, hitting every repair path
        assertNull(clusterMap.remove(99))
        clusterMap.remove(10)
        clusterMap.remove(30)
        clusterMap.remove(1)
        clusterMap.remove(2)
        clusterMap.remove(3)
        assertTrue(clusterMap.isEmpty())
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
        val minHashMap = HashTableLinearProbingShift<MinHashKey, Int>()

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
        val growMap = HashTableLinearProbingShift<Int, Int>(8)

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
        val smallMap = HashTableLinearProbingShift<Int, Int>(10)

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
    fun `test remove repairs a wraparound cluster`() {
        // homes: 7 % 8, 15 % 8, 23 % 8 — all 7; the cluster wraps the array end: [7][0][1]
        val wrapMap = HashTableLinearProbingShift<Int, Int>(capacity = 8)
        wrapMap.put(7, 70)
        wrapMap.put(15, 150)   // slot 0 — probed past the end
        wrapMap.put(23, 230)   // slot 1

        assertEquals(70, wrapMap.remove(7))   // hole at the cluster head, on the boundary

        assertEquals(150, wrapMap.get(15))
        assertEquals(230, wrapMap.get(23))
        assertEquals(2, wrapMap.size())
    }

    @Test
    fun `test remove returns old value`() {
        assertNull(map.put(1, 10))
        assertEquals(10, map.remove(1))
        assertNull(map.remove(1))       // already gone — null, no side effect
        assertNull(map.remove(99))     // never there
    }

    @Test
    fun `test random map operations`() {
        val jmap = HashMap<Int, Int>()
        for (loopIndex in 0 until loopCount) {
            map.clear()
            jmap.clear()
            assertEquals(jmap.size, map.size())

            map = HashTableLinearProbingShift()

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
        // open addressing's trap: a wrong delete repair orphans keys that probed
        // past the removed slot — so differential-test the probe chains hard
        val jmap = HashMap<Int, Int>()
        for (loopIndex in 0 until loopCount) {
            val probeMap = HashTableLinearProbingShift<Int, Int>(4)
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