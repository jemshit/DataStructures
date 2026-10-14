package hash_table

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


internal class HashTableSeparateChainingTest {

    internal class HashObject(val hash: Int, val data: Int) {

        override fun hashCode(): Int = hash

        override fun equals(o: Any?): Boolean {
            if (o !is HashObject) return false          // `as` would throw on foreign types
            return hashCode() == o.hashCode() && data == o.data
        }
    }

    // A key whose hashCode() is Int.MIN_VALUE — arithmetic negation of it overflows,
    // so this key reproduces the sign bug that the `and 0x7fffffff` mask fixes
    internal class MinHashKey(val id: Int) {

        override fun hashCode(): Int = Int.MIN_VALUE

        override fun equals(other: Any?): Boolean =
            other is MinHashKey && id == other.id
    }

    private val randomGenerator = Random(20261004L)   // fixed seed: reproducible failures
    private val loopCount: Int = randInt(25, 75)
    private val maxSize: Int = randInt(1, 75)
    private val maxRandomNum: Int = randInt(1, 350)

    private var map: HashTableSeparateChaining<Int, Int> = HashTableSeparateChaining()

    @Test
    fun `test Illegal Creation 1`() {
        assertThrows<java.lang.IllegalArgumentException> {
            HashTableSeparateChaining<Int, Int>(-3)
        }
    }

    @Test
    fun `test Legal Creation`() {
        HashTableSeparateChaining<Int, Int>(6)
    }

    @Test
    fun `test Updating Value`() {
        map.put(1, 1)
        assertTrue(1 == map.get(1))

        map.put(1, 5)
        assertTrue(5 == map.get(1))

        map.put(1, -7)
        assertTrue(-7 == map.get(1))
    }

    @Test
    fun `random Remove`() {
        var map: HashTableSeparateChaining<Int, Int>

        for (loopIndex in 0 until loopCount) {
            map = HashTableSeparateChaining()
            map.clear()

            // Add some random values
            val keySet = HashSet<Int>()
            for (index in 0 until maxSize) {
                val randomVal = randInt(-maxRandomNum, maxRandomNum)
                keySet.add(randomVal)
                map.put(randomVal, 5)
            }

            assertEquals(map.size(), keySet.size)

            val keys = map.keys()
            for (key in keys) map.remove(key)

            assertTrue(map.isEmpty())
        }
    }

    @Test
    fun `remove Test`() {
        val map = HashTableSeparateChaining<Int, Int>(7)

        // Add three elements
        map.put(11, 0)
        map.put(12, 0)
        map.put(13, 0)
        assertEquals(3, map.size())

        // Add ten more
        for (key in 1..10)
            map.put(key, 0)
        assertEquals(13, map.size())

        // Remove ten
        for (key in 1..10)
            map.remove(key)
        assertEquals(3, map.size())

        // remove three
        map.remove(11)
        map.remove(12)
        map.remove(13)
        assertEquals(0, map.size())
    }

    @Test
    fun `remove Test Complex1`() {
        val map = HashTableSeparateChaining<HashObject, Int>()

        val o1 = HashObject(88, 1)
        val o2 = HashObject(88, 2)
        val o3 = HashObject(88, 3)
        val o4 = HashObject(88, 4)

        map.put(o1, 111)
        map.put(o2, 111)
        map.put(o3, 111)
        map.put(o4, 111)

        map.remove(o2)
        map.remove(o3)
        map.remove(o1)
        map.remove(o4)

        assertEquals(0, map.size())
    }

    @Test
    fun `test Iterator`() {
        val map2 = HashMap<Int, Int>()
        for (loopIndex in 0 until loopCount) {
            map.clear()
            map2.clear()
            assertTrue(map.isEmpty())

            map = HashTableSeparateChaining()

            val randNumbers = genRandList(maxSize)
            for (key in randNumbers)
                assertEquals(map.put(key, key), map2.put(key, key))

            var count = 0
            for (key in map) {
                assertEquals(key, map.get(key))
                assertEquals(map.get(key), map2[key])
                assertTrue(map.containsKey(key))
                assertTrue(randNumbers.contains(key))
                count++
            }

            for (key in map2.keys) {
                assertEquals(key, map.get(key))
            }

            val set = HashSet<Int>()
            for (randomN in randNumbers)
                set.add(randomN)

            assertEquals(set.size, count)
            assertEquals(map2.size, count)
        }
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
    fun `test Random Map Operations`() {
        val jmap = HashMap<Int, Int>()
        for (loopIndex in 0 until loopCount) {
            map.clear()
            jmap.clear()
            assertEquals(jmap.size, map.size())

            map = HashTableSeparateChaining()

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
        }
    }

    @Test
    fun `test key with Int MIN VALUE hashCode`() {
        val map = HashTableSeparateChaining<MinHashKey, Int>()

        val key = MinHashKey(1)
        map.put(key, 10)

        assertEquals(10, map.get(key))
        assertTrue(map.containsKey(key))

        assertEquals(10, map.remove(key))
        assertTrue(map.isEmpty())
        assertNull(map.get(key))
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
    fun `test resize grow and shrink cycles`() {
        val map = HashTableSeparateChaining<Int, Int>(8)

        // grow through several doublings
        for (key in 0 until 200)
            map.put(key, key)
        assertEquals(200, map.size())

        // drain far below the shrink threshold (capacity halves, floor at 8)
        for (key in 0 until 200)
            assertEquals(key, map.remove(key))
        assertTrue(map.isEmpty())

        // still correct after all the rehashing
        for (key in 200 until 260)
            map.put(key, key)
        for (key in 200 until 260)
            assertEquals(key, map.get(key))
        assertEquals(60, map.size())
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
    fun `test values`() {
        map.put(1, 10)
        map.put(2, 20)

        assertEquals(listOf(10, 20), map.values())
    }

    @Test
    fun `test shrink does not ping-pong with grow`() {
        // alternating put/remove at the boundary must not trigger a resize per operation:
        // grow at LOAD_FACTOR * capacity, shrink at half that — the windows don't overlap
        val smallMap = HashTableSeparateChaining<Int, Int>(10)

        for (round in 0 until 100) {
            smallMap.put(round, round)
            assertEquals(round, smallMap.remove(round))
        }

        // sanity: correct throughout the churn, and reusable after draining
        assertTrue(smallMap.isEmpty())
        smallMap.put(999, 9)
        assertEquals(9, smallMap.get(999))
    }

    @Test
    fun `test grow threshold boundary`() {
        // capacity 8: grow fires when size crosses 0.75 * 8 = 6
        val boundaryMap = HashTableSeparateChaining<Int, Int>(8)
        for (key in 0 until 5)              // size 5: below the load factor
            boundaryMap.put(key, key)

        boundaryMap.put(10, 10)            // size 6 == 0.75 * 8: grows to 16
        for (key in 0 until 5)
            assertEquals(key, boundaryMap.get(key))
        assertEquals(10, boundaryMap.get(10))
        assertEquals(6, boundaryMap.size())
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