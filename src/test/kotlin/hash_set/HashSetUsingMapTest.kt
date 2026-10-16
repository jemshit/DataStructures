package hash_set

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class HashSetUsingMapTest {

    private var set: HashSetUsingMap<Int> = HashSetUsingMap()

    @Test
    fun `test empty set`() {
        assertTrue(set.isEmpty())
        assertEquals(0, set.size())
        assertFalse(set.contains(1))
    }

    @Test
    fun `test put and contains`() {
        set.put(1)
        set.put(2)

        assertEquals(2, set.size())
        assertTrue(set.contains(1))
        assertTrue(set.contains(2))
        assertFalse(set.contains(3))
    }

    @Test
    fun `test duplicate put is a no-op`() {
        // a set holds each key once — putting again only replaces the DUMMY value
        set.put(1)
        set.put(1)
        set.put(1)

        assertEquals(1, set.size())
        assertTrue(set.contains(1))
    }

    @Test
    fun `test remove`() {
        set.put(1)
        set.put(2)
        set.put(3)

        set.remove(2)

        assertEquals(2, set.size())
        assertFalse(set.contains(2))
        assertTrue(set.contains(1))
        assertTrue(set.contains(3))

        // removing an absent key: no side effect
        set.remove(99)
        assertEquals(2, set.size())

        set.remove(1)
        set.remove(3)
        assertTrue(set.isEmpty())
    }

    @Test
    fun `test set semantics vs java HashSet`() {
        val jset = HashSet<Int>()
        val random = Random(20261007L)

        for (round in 0 until 1000) {
            val key = random.nextInt(50)

            if (random.nextBoolean()) {
                set.put(key)
                jset.add(key)
            } else {
                set.remove(key)
                jset.remove(key)
            }

            assertEquals(jset.contains(key), set.contains(key))
        }

        // full parity after the churn
        assertEquals(jset.size, set.size())
        for (key in jset)
            assertTrue(set.contains(key))
        for (key in set)
            assertTrue(jset.contains(key))
    }

    @Test
    fun `test iterator yields every key once`() {
        for (key in 0 until 100)
            set.put(key)

        val seen = HashSet<Int>()
        for (key in set)
            assertTrue(seen.add(key))          // uniqueness: iteration must not repeat

        assertEquals(100, seen.size)
        assertEquals(set.size(), seen.size)
    }

    @Test
    fun `test iterator is fail-fast on put`() {
        set.put(1)
        set.put(2)

        assertThrows<ConcurrentModificationException> {
            for (key in set)
                set.put(3)                     // new key mid-iteration: structural
        }
    }

    @Test
    fun `test iterator is fail-fast on remove`() {
        set.put(1)
        set.put(2)

        assertThrows<ConcurrentModificationException> {
            for (key in set)
                set.remove(1)                  // removal mid-iteration: structural
        }
    }

    @Test
    fun `test iterator survives value-only churn`() {
        // putting an EXISTING key swaps the DUMMY value — not structural
        set.put(1)
        set.put(2)

        var count = 0
        for (key in set) {
            set.put(key)                       // re-put the same key
            count++
        }

        assertEquals(2, count)
        assertEquals(2, set.size())
    }

    @Test
    fun `test works with Int MIN VALUE hashCode keys`() {
        class MinHashKey(val id: Int) {
            override fun hashCode(): Int = Int.MIN_VALUE
            override fun equals(other: Any?): Boolean = other is MinHashKey && id == other.id
        }

        val minHashSet = HashSetUsingMap<MinHashKey>()
        val key = MinHashKey(1)
        minHashSet.put(key)

        assertTrue(minHashSet.contains(key))
        assertEquals(1, minHashSet.size())
        minHashSet.remove(key)
        assertTrue(minHashSet.isEmpty())
    }
}