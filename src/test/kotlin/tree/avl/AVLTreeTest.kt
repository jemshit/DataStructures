package tree.avl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class AVLTreeTest {
    private val loopCount = 9

    @Test
    fun `test is empty`() {
        val tree = AVLTree<String>()
        assertTrue(tree.isEmpty())

        tree.insert("Hello World!")
        assertFalse(tree.isEmpty())
    }

    @Test
    fun `test size`() {
        val tree = AVLTree<String>()
        assertEquals(tree.size(), 0)

        tree.insert("Hello World!")
        assertEquals(tree.size(), 1)
    }

    @Test
    fun `test height`() {
        val tree = AVLTree<String>()

        // Tree should look like:
        //        M
        //      J   S
        //    B    N  Z
        //  A

        // No tree
        assertEquals(tree.height(), 0)

        // Layer One
        tree.insert("M")
        assertEquals(0, tree.height())

        // Layer Two
        tree.insert("J")
        assertEquals(1, tree.height())
        tree.insert("S")
        assertEquals(1, tree.height())

        // Layer Three
        tree.insert("B")
        assertEquals(2, tree.height())
        tree.insert("N")
        assertEquals(2, tree.height())
        tree.insert("Z")
        assertEquals(2, tree.height())

        // Layer 4
        tree.insert("A")
        assertEquals(2, tree.height())
    }

    @Test
    fun `test insert`() {
        // Add element which does not yet exist
        val tree = AVLTree<Char>()
        assertTrue(tree.insert('A'))
        assertEquals(1, tree.size())

        // Add duplicate element
        assertFalse(tree.insert('A'))
        assertEquals(1, tree.size())

        // Add a second element which is not a duplicate
        assertTrue(tree.insert('B'))
        assertEquals(2, tree.size())
    }

    @Test
    fun `test remove`() {
        // Try removing an element which doesn't exist
        val tree = AVLTree<Char>()
        tree.insert('A')
        assertEquals(tree.size(), 1)
        assertFalse(tree.remove('B'))
        assertEquals(tree.size(), 1)

        // Try removing an element which does exist
        tree.insert('B')
        assertEquals(tree.size(), 2)
        assertTrue(tree.remove('B'))
        assertEquals(tree.size(), 1)
        assertEquals(0, tree.height())

        // Try removing the root
        assertTrue(tree.remove('A'))
        assertEquals(0, tree.size())
        assertEquals(tree.height(), 0)
    }

    @Test
    fun `test remove 2`() {
        val tree = AVLTree<String>()
        // Tree should look like:
        //        M
        //      J  S

        // No tree
        assertFalse(tree.remove(""))
        assertFalse(tree.remove("a"))

        tree.insert("M")
        assertTrue(tree.remove("M"))
        assertFalse(tree.remove("M"))

        tree.insert("M")
        tree.insert("J")
        tree.insert("S")
        assertTrue(tree.remove("M"))
        assertTrue(tree.contains("J"))
        assertTrue(tree.contains("S"))
        assertEquals(2, tree.size())

        assertTrue(tree.remove("J"))
        assertTrue(tree.contains("S"))
        assertEquals(1, tree.size())
    }

    @Test
    fun `test remove 3`() {
        val tree = AVLTree<Int>()
        tree.insert(6)
        tree.insert(7)
        tree.insert(3)
        tree.insert(2)
        tree.insert(5)
        tree.insert(4)
        assertEquals(6, tree.size())

        tree.remove(6)
        assertTrue(tree.contains(2))
        assertTrue(tree.contains(3))
    }

    @Test
    fun testLeftLeftCase() {
        val tree = AVLTree<Int>()
        tree.insert(3)
        tree.insert(2)
        tree.insert(1)
        assertEquals(2, tree.root!!.data)
        assertEquals(1, tree.root!!.left!!.data)
        assertEquals(3, tree.root!!.right!!.data)
        assertNull(tree.root!!.left!!.left)
        assertNull(tree.root!!.left!!.right)
        assertNull(tree.root!!.right!!.left)
        assertNull(tree.root!!.right!!.right)
    }

    @Test
    fun testLeftRightCase() {
        val tree = AVLTree<Int>()
        tree.insert(3)
        tree.insert(1)
        tree.insert(2)
        assertEquals(2, tree.root!!.data)
        assertEquals(1, tree.root!!.left!!.data)
        assertEquals(3, tree.root!!.right!!.data)
        assertNull(tree.root!!.left!!.left)
        assertNull(tree.root!!.left!!.right)
        assertNull(tree.root!!.right!!.left)
        assertNull(tree.root!!.right!!.right)
    }

    @Test
    fun testRightRightCase() {
        val tree = AVLTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)
        assertEquals(2, tree.root!!.data)
        assertEquals(1, tree.root!!.left!!.data)
        assertEquals(3, tree.root!!.right!!.data)
        assertNull(tree.root!!.left!!.left)
        assertNull(tree.root!!.left!!.right)
        assertNull(tree.root!!.right!!.left)
        assertNull(tree.root!!.right!!.right)
    }

    @Test
    fun testRightLeftCase() {
        val tree = AVLTree<Int>()
        tree.insert(1)
        tree.insert(3)
        tree.insert(2)
        assertEquals(2, tree.root!!.data)
        assertEquals(1, tree.root!!.left!!.data)
        assertEquals(3, tree.root!!.right!!.data)
        assertNull(tree.root!!.left!!.left)
        assertNull(tree.root!!.left!!.right)
        assertNull(tree.root!!.right!!.left)
        assertNull(tree.root!!.right!!.right)
    }

    @Test
    fun testRandomizedBalanceFactorTest() {
        val tree = AVLTree<Int>()
        for (i in 0 until 20) {
            tree.insert(randValue())
            assertAvlInvariants(tree.root)
        }
    }

    /** Independently verifies BST ordering plus the height and balance-factor caches at every node. */
    private fun assertAvlInvariants(
        node: AVLNode<Int>?,
        lowerBound: Int? = null,
        upperBound: Int? = null,
    ): Int {
        if (node == null) return -1

        if (lowerBound != null) assertTrue(node.data > lowerBound)
        if (upperBound != null) assertTrue(node.data < upperBound)

        val leftHeight = assertAvlInvariants(node.left, lowerBound, node.data)
        val rightHeight = assertAvlInvariants(node.right, node.data, upperBound)
        val expectedHeight = 1 + maxOf(leftHeight, rightHeight)
        val expectedBalanceFactor = rightHeight - leftHeight

        assertEquals(expectedHeight, node.height)
        assertEquals(expectedBalanceFactor, node.balanceFactor)
        assertTrue(node.balanceFactor in -1..1)

        return expectedHeight
    }

    private fun countNodes(node: AVLNode<Int>?): Int {
        if (node == null) return 0
        return 1 + countNodes(node.left) + countNodes(node.right)
    }

    @Test
    fun `random interleaved insert and remove operations preserve AVL invariants`() {
        val random = Random(0)

        repeat(120) {
            val tree = AVLTree<Int>()
            val model = sortedSetOf<Int>()

            repeat(500) {
                val value = random.nextInt(200_001) - 100_000
                val insert = random.nextInt(100) < 55
                val expected = if (insert) model.add(value) else model.remove(value)
                val actual = if (insert) tree.insert(value) else tree.remove(value)

                assertEquals(expected, actual)
                assertEquals(model.size, tree.size())
                assertEquals(model.size, countNodes(tree.root))
                assertAvlInvariants(tree.root)
            }
        }
    }

    private fun randValue(): Int {
        return (Math.random() * +100000 * 2).toInt() + (-100000)
    }

    @Test
    fun `random Remove Tests`() {
        for (size in 0 until loopCount) {
            val tree = AVLTree<Int>()
            val randomList = genRandList(size)
            for (value in randomList) {
                tree.insert(value)
            }

            Collections.shuffle(randomList)
            // Remove all the elements we just placed in the tree
            for (index in 0 until size) {
                val value = randomList.get(index)

                assertTrue(tree.remove(value))
                assertFalse(tree.contains(value))
                assertEquals(size - index - 1, tree.size())
                assertAvlInvariants(tree.root)
            }

            assertTrue(tree.isEmpty())
        }
    }

    @Test
    fun `test contains`() {
        // Setup tree
        val tree = AVLTree<Char>()
        tree.insert('B')
        tree.insert('A')
        tree.insert('C')

        // Try looking for an element which doesn't exist
        assertFalse(tree.contains('D'))

        // Try looking for an element which exists in the root
        assertTrue(tree.contains('B'))

        // Try looking for an element which exists as the left child of the root
        assertTrue(tree.contains('A'))

        // Try looking for an element which exists as the right child of the root
        assertTrue(tree.contains('C'))
    }

    private fun genRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (index in 0 until size)
            list.add(index)
        list.shuffle()
        return list
    }
}
