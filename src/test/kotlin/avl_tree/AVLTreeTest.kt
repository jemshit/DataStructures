package avl_tree

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
        assertEquals(1, tree.root!!.leftChild!!.data)
        assertEquals(3, tree.root!!.rightChild!!.data)
        assertNull(tree.root!!.leftChild!!.leftChild)
        assertNull(tree.root!!.leftChild!!.rightChild)
        assertNull(tree.root!!.rightChild!!.leftChild)
        assertNull(tree.root!!.rightChild!!.rightChild)
    }

    @Test
    fun testLeftRightCase() {
        val tree = AVLTree<Int>()
        tree.insert(3)
        tree.insert(1)
        tree.insert(2)
        assertEquals(2, tree.root!!.data)
        assertEquals(1, tree.root!!.leftChild!!.data)
        assertEquals(3, tree.root!!.rightChild!!.data)
        assertNull(tree.root!!.leftChild!!.leftChild)
        assertNull(tree.root!!.leftChild!!.rightChild)
        assertNull(tree.root!!.rightChild!!.leftChild)
        assertNull(tree.root!!.rightChild!!.rightChild)
    }

    @Test
    fun testRightRightCase() {
        val tree = AVLTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)
        assertEquals(2, tree.root!!.data)
        assertEquals(1, tree.root!!.leftChild!!.data)
        assertEquals(3, tree.root!!.rightChild!!.data)
        assertNull(tree.root!!.leftChild!!.leftChild)
        assertNull(tree.root!!.leftChild!!.rightChild)
        assertNull(tree.root!!.rightChild!!.leftChild)
        assertNull(tree.root!!.rightChild!!.rightChild)
    }

    @Test
    fun testRightLeftCase() {
        val tree = AVLTree<Int>()
        tree.insert(1)
        tree.insert(3)
        tree.insert(2)
        assertEquals(2, tree.root!!.data)
        assertEquals(1, tree.root!!.leftChild!!.data)
        assertEquals(3, tree.root!!.rightChild!!.data)
        assertNull(tree.root!!.leftChild!!.leftChild)
        assertNull(tree.root!!.leftChild!!.rightChild)
        assertNull(tree.root!!.rightChild!!.leftChild)
        assertNull(tree.root!!.rightChild!!.rightChild)
    }

    @Test
    fun testRandomizedBalanceFactorTest() {
        val tree = AVLTree<Int>()
        for (i in 0 until 20) {
            tree.insert(randValue())
            assertTrue(validateBalanceFactorValues(tree.root))
        }
    }

    // Make sure all balance factor values are either -1, 0 or +1
    private fun validateBalanceFactorValues(node: AVLTree.AVLNode<Int>?): Boolean {
        if (node == null) return true
        return if (node.balanceFactor > +1 || node.balanceFactor < -1)
            false
        else
            validateBalanceFactorValues(node.leftChild) && validateBalanceFactorValues(
                node.rightChild
            )
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