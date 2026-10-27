package unionfind

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class UnionFindBySizeTest {

    @Test
    fun `each element starts in its own component`() {
        val unionFind = UnionFindBySize(4)

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
        val unionFind = UnionFindBySize(5)

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
    fun `union by size attaches the smaller component below the larger one`() {
        val unionFind = UnionFindBySize(5)
        unionFind.union(0, 1)
        unionFind.union(0, 2)
        unionFind.union(3, 4)
        val largerRoot = unionFind.find(0)

        unionFind.union(3, 0)

        assertEquals(largerRoot, unionFind.find(3))
        assertEquals(5, unionFind.componentSize(4))
        assertEquals(1, unionFind.componentCount)
    }

    @Test
    fun `empty union find is valid`() {
        val unionFind = UnionFindBySize(0)

        assertEquals(0, unionFind.size)
        assertEquals(0, unionFind.componentCount)
    }

    @Test
    fun `negative element count and unknown elements are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            UnionFindBySize(-1)
        }

        val unionFind = UnionFindBySize(3)
        assertThrows(IllegalArgumentException::class.java) { unionFind.find(-1) }
        assertThrows(IllegalArgumentException::class.java) { unionFind.find(3) }
        assertThrows(IllegalArgumentException::class.java) { unionFind.union(-1, 0) }
        assertThrows(IllegalArgumentException::class.java) { unionFind.union(0, 3) }
    }
}
