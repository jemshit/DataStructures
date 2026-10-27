package unionfind

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class UnionFindByRankTest {

    @Test
    fun `each element starts in its own component`() {
        val unionFind = UnionFindByRank(4)

        assertEquals(4, unionFind.size)
        assertEquals(4, unionFind.componentCount)
        for (element in 0 until unionFind.size) {
            assertEquals(element, unionFind.find(element))
            assertEquals(1, unionFind.componentSize(element))
            assertTrue(unionFind.connected(element, element))
        }
    }

    @Test
    fun `union reports whether two components were merged`() {
        val unionFind = UnionFindByRank(5)

        assertTrue(unionFind.union(0, 1))
        assertFalse(unionFind.union(1, 0))
        assertTrue(unionFind.union(1, 2))

        assertEquals(3, unionFind.componentCount)
        assertEquals(3, unionFind.componentSize(0))
        assertEquals(3, unionFind.componentSize(1))
        assertEquals(3, unionFind.componentSize(2))
        assertTrue(unionFind.connected(0, 2))
        assertFalse(unionFind.connected(0, 3))
    }

    @Test
    fun `union by rank prefers higher rank even when its component is smaller`() {
        val unionFind = UnionFindByRank(9)

        // Root 0 reaches rank 2 with four elements.
        unionFind.union(0, 1)
        unionFind.union(2, 3)
        unionFind.union(0, 2)
        val higherRankRoot = unionFind.find(0)

        // Root 4 remains rank 1 while its component grows to five elements.
        unionFind.union(4, 5)
        unionFind.union(4, 6)
        unionFind.union(4, 7)
        unionFind.union(4, 8)

        unionFind.union(4, 0)

        assertEquals(higherRankRoot, unionFind.find(4))
        assertEquals(9, unionFind.componentSize(8))
        assertEquals(1, unionFind.componentCount)
    }

    @Test
    fun `empty union find is valid`() {
        val unionFind = UnionFindByRank(0)

        assertEquals(0, unionFind.size)
        assertEquals(0, unionFind.componentCount)
    }

    @Test
    fun `negative element count and unknown elements are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            UnionFindByRank(-1)
        }

        val unionFind = UnionFindByRank(3)
        assertThrows(IllegalArgumentException::class.java) { unionFind.find(-1) }
        assertThrows(IllegalArgumentException::class.java) { unionFind.find(3) }
        assertThrows(IllegalArgumentException::class.java) { unionFind.union(-1, 0) }
        assertThrows(IllegalArgumentException::class.java) { unionFind.union(0, 3) }
    }
}
