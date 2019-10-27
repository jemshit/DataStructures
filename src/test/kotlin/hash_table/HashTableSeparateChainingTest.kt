package hash_table

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


internal class HashTableSeparateChainingTest {

    internal class HashObject(val hash: Int, val data: Int) {

        override fun hashCode(): Int = hash

        override fun equals(o: Any?): Boolean {
            val otherObject = o as HashObject
            return hashCode() == otherObject.hashCode() && data == otherObject.data
        }
    }

    private val randomGenerator = Random()
    private val loopCount: Int = randInt(25, 75)
    private val maxSize: Int = randInt(1, 75)
    private val maxRandomNum: Int = randInt(1, 350)

    private var map: HashTableSeparateChaining<Int, Int> = HashTableSeparateChaining()

    @Test
    fun `test Illegal Creation 1`() {
        assertThrows<java.lang.IllegalArgumentException> {
            HashTableSeparateChaining<Int, Int>(-3, 0.5f)
        }
    }

    @Test
    fun `test Illegal Creation 2`() {
        assertThrows<IllegalArgumentException> {
            HashTableSeparateChaining<Int, Int>(5, -0.5f)
        }
    }

    @Test
    fun `test Illegal Creation 3`() {
        assertThrows<java.lang.IllegalArgumentException> {
            HashTableSeparateChaining<Int, Int>(5, 0.01f)
        }
    }

    @Test
    fun `test Legal Creation`() {
        HashTableSeparateChaining<Int, Int>(6, 0.9f)
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