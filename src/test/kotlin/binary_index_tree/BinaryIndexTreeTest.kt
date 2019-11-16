package binary_index_tree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BinaryIndexTreeTest {

    private val loopCount = 100

    @Test
    fun testNegativeSize() {
        assertThrows<Exception> { BinaryIndexTree(-1) }
    }

    @Test
    fun testZeroSize() {
        assertThrows<Exception> { BinaryIndexTree(0) }
    }

    @Test
    fun testZeroItems() {
        assertThrows<Exception> { BinaryIndexTree(longArrayOf()) }
    }

    @Test
    fun testAdd() {
        val binaryIndexTree = BinaryIndexTree(6)
        binaryIndexTree.add(1, 1)
        assertEquals(1, binaryIndexTree.rangeSum(1, 1))

        binaryIndexTree.add(2, 2)
        assertEquals(3, binaryIndexTree.rangeSum(1, 2))

        binaryIndexTree.add(3, 3)
        assertEquals(6, binaryIndexTree.rangeSum(1, 3))

        binaryIndexTree.add(4, 4)
        assertEquals(10, binaryIndexTree.rangeSum(1, 4))

        binaryIndexTree.add(5, 5)
        assertEquals(15, binaryIndexTree.rangeSum(1, 5))

        binaryIndexTree.add(6, 6)
        assertEquals(20, binaryIndexTree.rangeSum(2, 6))
        assertEquals(21, binaryIndexTree.rangeSum(1, 6))
    }

    @Test
    fun testUpdate() {
        val staticArray = longArrayOf(1, 2, 3, 4, 5, 6)
        val binaryIndexTree = BinaryIndexTree(staticArray)

        binaryIndexTree.update(1, 2)
        //longArrayOf(2, 2, 3, 4, 5, 6)
        assertEquals(2, binaryIndexTree.rangeSum(1, 1))
        assertEquals(4, binaryIndexTree.rangeSum(1, 2))
        assertEquals(7, binaryIndexTree.rangeSum(1, 3))

        binaryIndexTree.update(4, 8)
        //longArrayOf(2, 2, 3, 8, 5, 6)
        assertEquals(2, binaryIndexTree.rangeSum(1, 1))
        assertEquals(8, binaryIndexTree.rangeSum(4, 4))
        assertEquals(13, binaryIndexTree.rangeSum(4, 5))
        assertEquals(19, binaryIndexTree.rangeSum(4, 6))
    }

    @Test
    fun testGet() {
        val staticArray = longArrayOf(1, 2, 3, 4, 5, 6)
        val binaryIndexTree = BinaryIndexTree(staticArray)
        assertEquals(1, binaryIndexTree.get(1))
        assertEquals(2, binaryIndexTree.get(2))
        assertEquals(3, binaryIndexTree.get(3))
        assertEquals(4, binaryIndexTree.get(4))
        assertEquals(5, binaryIndexTree.get(5))
        assertEquals(6, binaryIndexTree.get(6))
    }

    @Test
    fun testRangeSumPositiveValues() {
        val staticArray = longArrayOf(1, 2, 3, 4, 5, 6)
        val binaryIndexTree = BinaryIndexTree(staticArray)

        assertEquals(21, binaryIndexTree.rangeSum(1, 6))
        assertEquals(15, binaryIndexTree.rangeSum(1, 5))
        assertEquals(10, binaryIndexTree.rangeSum(1, 4))
        assertEquals(6, binaryIndexTree.rangeSum(1, 3))
        assertEquals(3, binaryIndexTree.rangeSum(1, 2))
        assertEquals(1, binaryIndexTree.rangeSum(1, 1))
        assertThrows<Exception> { binaryIndexTree.rangeSum(1, 0) }

        assertEquals(7, binaryIndexTree.rangeSum(3, 4))
        assertEquals(20, binaryIndexTree.rangeSum(2, 6))
        assertEquals(9, binaryIndexTree.rangeSum(4, 5))

        assertEquals(6, binaryIndexTree.rangeSum(6, 6))
        assertEquals(5, binaryIndexTree.rangeSum(5, 5))
        assertEquals(4, binaryIndexTree.rangeSum(4, 4))
        assertEquals(3, binaryIndexTree.rangeSum(3, 3))
        assertEquals(2, binaryIndexTree.rangeSum(2, 2))
        assertEquals(1, binaryIndexTree.rangeSum(1, 1))
    }

    @Test
    fun testRangeSumNegativeValues() {
        val staticArray = longArrayOf(-1, -2, -3, -4, -5, -6)
        val binaryIndexTree = BinaryIndexTree(staticArray)

        assertEquals(-21, binaryIndexTree.rangeSum(1, 6))
        assertEquals(-15, binaryIndexTree.rangeSum(1, 5))
        assertEquals(-10, binaryIndexTree.rangeSum(1, 4))
        assertEquals(-6, binaryIndexTree.rangeSum(1, 3))
        assertEquals(-3, binaryIndexTree.rangeSum(1, 2))
        assertEquals(-1, binaryIndexTree.rangeSum(1, 1))

        assertEquals(-6, binaryIndexTree.rangeSum(6, 6))
        assertEquals(-5, binaryIndexTree.rangeSum(5, 5))
        assertEquals(-4, binaryIndexTree.rangeSum(4, 4))
        assertEquals(-3, binaryIndexTree.rangeSum(3, 3))
        assertEquals(-2, binaryIndexTree.rangeSum(2, 2))
        assertEquals(-1, binaryIndexTree.rangeSum(1, 1))
    }

    @Test
    fun testRangeSumNegativeValues2() {
        val staticArray = longArrayOf(-76871, -164790)
        val binaryIndexTree = BinaryIndexTree(staticArray)

        for (index in 0 until loopCount) {
            assertEquals(-76871, binaryIndexTree.rangeSum(1, 1))
            assertEquals(-76871, binaryIndexTree.rangeSum(1, 1))
            assertEquals(-241661, binaryIndexTree.rangeSum(1, 2))
            assertEquals(-241661, binaryIndexTree.rangeSum(1, 2))
            assertEquals(-241661, binaryIndexTree.rangeSum(1, 2))
            assertEquals(-164790, binaryIndexTree.rangeSum(2, 2))
            assertEquals(-164790, binaryIndexTree.rangeSum(2, 2))
            assertEquals(-164790, binaryIndexTree.rangeSum(2, 2))
        }
    }

}