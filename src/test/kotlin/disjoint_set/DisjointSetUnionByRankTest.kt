package disjoint_set

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DisjointSetUnionByRankTest{
    
    @Test
    fun `test number of subsets`() {
        val disjointSet = DisjointSetUnionByRank(5)
        assertEquals(disjointSet.subsetCount(), 5)

        disjointSet.union(0, 1)
        assertEquals(disjointSet.subsetCount(), 4)

        disjointSet.union(1, 0)
        assertEquals(disjointSet.subsetCount(), 4)

        disjointSet.union(1, 2)
        assertEquals(disjointSet.subsetCount(), 3)

        disjointSet.union(0, 2)
        assertEquals(disjointSet.subsetCount(), 3)

        disjointSet.union(2, 1)
        assertEquals(disjointSet.subsetCount(), 3)

        disjointSet.union(3, 4)
        assertEquals(disjointSet.subsetCount(), 2)

        disjointSet.union(4, 3)
        assertEquals(disjointSet.subsetCount(), 2)

        disjointSet.union(1, 3)
        assertEquals(disjointSet.subsetCount(), 1)

        disjointSet.union(4, 0)
        assertEquals(disjointSet.subsetCount(), 1)
    }

    @Test
    fun `test subset size`() {
        val disjointSet = DisjointSetUnionByRank(5)
        assertEquals(disjointSet.subsetSize(0), 1)
        assertEquals(disjointSet.subsetSize(1), 1)
        assertEquals(disjointSet.subsetSize(2), 1)
        assertEquals(disjointSet.subsetSize(3), 1)
        assertEquals(disjointSet.subsetSize(4), 1)

        disjointSet.union(0, 1)
        assertEquals(disjointSet.subsetSize(0), 2)
        assertEquals(disjointSet.subsetSize(1), 2)
        assertEquals(disjointSet.subsetSize(2), 1)
        assertEquals(disjointSet.subsetSize(3), 1)
        assertEquals(disjointSet.subsetSize(4), 1)

        disjointSet.union(1, 0)
        assertEquals(disjointSet.subsetSize(0), 2)
        assertEquals(disjointSet.subsetSize(1), 2)
        assertEquals(disjointSet.subsetSize(2), 1)
        assertEquals(disjointSet.subsetSize(3), 1)
        assertEquals(disjointSet.subsetSize(4), 1)

        disjointSet.union(1, 2)
        assertEquals(disjointSet.subsetSize(0), 3)
        assertEquals(disjointSet.subsetSize(1), 3)
        assertEquals(disjointSet.subsetSize(2), 3)
        assertEquals(disjointSet.subsetSize(3), 1)
        assertEquals(disjointSet.subsetSize(4), 1)

        disjointSet.union(0, 2)
        assertEquals(disjointSet.subsetSize(0), 3)
        assertEquals(disjointSet.subsetSize(1), 3)
        assertEquals(disjointSet.subsetSize(2), 3)
        assertEquals(disjointSet.subsetSize(3), 1)
        assertEquals(disjointSet.subsetSize(4), 1)

        disjointSet.union(2, 1)
        assertEquals(disjointSet.subsetSize(0), 3)
        assertEquals(disjointSet.subsetSize(1), 3)
        assertEquals(disjointSet.subsetSize(2), 3)
        assertEquals(disjointSet.subsetSize(3), 1)
        assertEquals(disjointSet.subsetSize(4), 1)

        disjointSet.union(3, 4)
        assertEquals(disjointSet.subsetSize(0), 3)
        assertEquals(disjointSet.subsetSize(1), 3)
        assertEquals(disjointSet.subsetSize(2), 3)
        assertEquals(disjointSet.subsetSize(3), 2)
        assertEquals(disjointSet.subsetSize(4), 2)

        disjointSet.union(4, 3)
        assertEquals(disjointSet.subsetSize(0), 3)
        assertEquals(disjointSet.subsetSize(1), 3)
        assertEquals(disjointSet.subsetSize(2), 3)
        assertEquals(disjointSet.subsetSize(3), 2)
        assertEquals(disjointSet.subsetSize(4), 2)

        disjointSet.union(1, 3)
        assertEquals(disjointSet.subsetSize(0), 5)
        assertEquals(disjointSet.subsetSize(1), 5)
        assertEquals(disjointSet.subsetSize(2), 5)
        assertEquals(disjointSet.subsetSize(3), 5)
        assertEquals(disjointSet.subsetSize(4), 5)

        disjointSet.union(4, 0)
        assertEquals(disjointSet.subsetSize(0), 5)
        assertEquals(disjointSet.subsetSize(1), 5)
        assertEquals(disjointSet.subsetSize(2), 5)
        assertEquals(disjointSet.subsetSize(3), 5)
        assertEquals(disjointSet.subsetSize(4), 5)
    }

    @Test
    fun `test connectivity`() {
        val size = 7
        val disjointSet = DisjointSetUnionByRank(size)

        for (node in 0 until size)
            assertTrue(disjointSet.connected(node, node))

        disjointSet.union(0, 2)

        assertTrue(disjointSet.connected(0, 2))
        assertTrue(disjointSet.connected(2, 0))

        assertFalse(disjointSet.connected(0, 1))
        assertFalse(disjointSet.connected(3, 1))
        assertFalse(disjointSet.connected(6, 4))
        assertFalse(disjointSet.connected(5, 0))

        for (node in 0 until size)
            assertTrue(disjointSet.connected(node, node))

        disjointSet.union(3, 1)

        assertTrue(disjointSet.connected(0, 2))
        assertTrue(disjointSet.connected(2, 0))
        assertTrue(disjointSet.connected(1, 3))
        assertTrue(disjointSet.connected(3, 1))

        assertFalse(disjointSet.connected(0, 1))
        assertFalse(disjointSet.connected(1, 2))
        assertFalse(disjointSet.connected(2, 3))
        assertFalse(disjointSet.connected(1, 0))
        assertFalse(disjointSet.connected(2, 1))
        assertFalse(disjointSet.connected(3, 2))

        assertFalse(disjointSet.connected(1, 4))
        assertFalse(disjointSet.connected(2, 5))
        assertFalse(disjointSet.connected(3, 6))

        for (node in 0 until size)
            assertTrue(disjointSet.connected(node, node))

        disjointSet.union(2, 5)
        assertTrue(disjointSet.connected(0, 2))
        assertTrue(disjointSet.connected(2, 0))
        assertTrue(disjointSet.connected(1, 3))
        assertTrue(disjointSet.connected(3, 1))
        assertTrue(disjointSet.connected(0, 5))
        assertTrue(disjointSet.connected(5, 0))
        assertTrue(disjointSet.connected(5, 2))
        assertTrue(disjointSet.connected(2, 5))

        assertFalse(disjointSet.connected(0, 1))
        assertFalse(disjointSet.connected(1, 2))
        assertFalse(disjointSet.connected(2, 3))
        assertFalse(disjointSet.connected(1, 0))
        assertFalse(disjointSet.connected(2, 1))
        assertFalse(disjointSet.connected(3, 2))

        assertFalse(disjointSet.connected(4, 6))
        assertFalse(disjointSet.connected(4, 5))
        assertFalse(disjointSet.connected(1, 6))

        for (node in 0 until size)
            assertTrue(disjointSet.connected(node, node))

        // Connect everything
        disjointSet.union(1, 2)
        disjointSet.union(3, 4)
        disjointSet.union(4, 6)

        for (node1 in 0 until size) {
            for (node2 in 0 until size) {
                assertTrue(disjointSet.connected(node1, node2))
            }
        }
    }

    @Test
    fun `test size`() {
        val disjointSet = DisjointSetUnionByRank(5)
        assertEquals(disjointSet.size(), 5)
        disjointSet.union(0, 1)
        disjointSet.find(3)
        assertEquals(disjointSet.size(), 5)
        disjointSet.union(1, 2)
        assertEquals(disjointSet.size(), 5)
        disjointSet.union(0, 2)
        disjointSet.find(1)
        assertEquals(disjointSet.size(), 5)
        disjointSet.union(2, 1)
        assertEquals(disjointSet.size(), 5)
        disjointSet.union(3, 4)
        disjointSet.find(0)
        assertEquals(disjointSet.size(), 5)
        disjointSet.union(4, 3)
        disjointSet.find(3)
        assertEquals(disjointSet.size(), 5)
        disjointSet.union(1, 3)
        assertEquals(disjointSet.size(), 5)
        disjointSet.find(2)
        disjointSet.union(4, 0)
        assertEquals(disjointSet.size(), 5)
    }

    @Test
    fun `test bad UnionFind Creation1`() {
        assertThrows(Exception::class.java) {
            DisjointSetUnionByRank(-1)
        }
    }

    @Test
    fun `test bad UnionFind Creation2`() {
        assertThrows(Exception::class.java) {
            DisjointSetUnionByRank(-3463)
        }
    }

    @Test
    fun `test bad UnionFind Creation3`() {
        assertThrows(Exception::class.java) {
            DisjointSetUnionByRank(0)
        }
    }
    
}