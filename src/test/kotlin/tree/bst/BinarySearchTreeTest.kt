package tree.bst

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import tree.TestTreeNode
import tree.TreeTraversalOrder

internal class BinarySearchTreeTest {
    private val loopCount = 9

    @Test
    fun `test is empty`() {
        val tree = BinarySearchTree<String>()
        assertTrue(tree.isEmpty())

        tree.insert("Hello World!")
        assertFalse(tree.isEmpty())
    }

    @Test
    fun `test size`() {
        val tree = BinarySearchTree<String>()
        assertEquals(tree.size(), 0)

        tree.insert("Hello World!")
        assertEquals(tree.size(), 1)
    }

    @Test
    fun `test height`() {
        val tree = BinarySearchTree<String>()

        // Tree should look like:
        //        M
        //      J  S
        //    B   N Z
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
        assertEquals(3, tree.height())
    }

    @Test
    fun `test add`() {
        // Add element which does not yet exist
        val tree = BinarySearchTree<Char>()
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
        val tree = BinarySearchTree<Char>()
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
        val tree = BinarySearchTree<String>()
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
        val tree = BinarySearchTree<Int>()
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
    fun `random Remove Tests`() {
        for (size in 0 until loopCount) {
            val tree = BinarySearchTree<Int>()
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
        val tree = BinarySearchTree<Char>()
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

internal class BinarySearchTreeTraversalTest {
    private val loopCount = 9

    @Test
    fun `concurrentModificationError PreOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.PRE_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.insert(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError InOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.IN_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.insert(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError PostOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.POST_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.insert(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError LevelOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.LEVEL_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.insert(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError Removing PreOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.PRE_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.remove(2)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError Removing InOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.IN_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.remove(2)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError Removing PostOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.POST_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.remove(2)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError Removing LevelOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.insert(1)
        tree.insert(2)
        tree.insert(3)

        val iterator = tree.traverse(TreeTraversalOrder.LEVEL_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.remove(2)
                iterator.next()
            }
        }
    }

    @Test
    fun `test PreOrder Traversal`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)
            assertTrue(validateTreeTraversal(TreeTraversalOrder.PRE_ORDER, input))
        }
    }

    @Test
    fun `test InOrder Traversal`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)
            assertTrue(validateTreeTraversal(TreeTraversalOrder.IN_ORDER, input))
        }
    }

    @Test
    fun `test PostOrder Traversal`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)
            assertTrue(validateTreeTraversal(TreeTraversalOrder.POST_ORDER, input))
        }
    }

    @Test
    fun `test LevelOrder Traversal`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)
            assertTrue(validateTreeTraversal(TreeTraversalOrder.LEVEL_ORDER, input))
        }
    }

    private fun validateTreeTraversal(travOrder: TreeTraversalOrder, input: List<Int>): Boolean {
        val out = ArrayList<Int>()
        val expected = ArrayList<Int>()

        var testTree: TestTreeNode? = null
        val tree = BinarySearchTree<Int>()

        // Construct Binary Tree and test tree
        for (value in input) {
            testTree = TestTreeNode.add(testTree, value)
            tree.insert(value)
        }

        // Generate the expected output for the particular traversal
        when (travOrder) {
            TreeTraversalOrder.PRE_ORDER -> TestTreeNode.preOrder(expected, testTree)
            TreeTraversalOrder.IN_ORDER -> TestTreeNode.inOrder(expected, testTree)
            TreeTraversalOrder.POST_ORDER -> TestTreeNode.postOrder(expected, testTree)
            TreeTraversalOrder.LEVEL_ORDER -> TestTreeNode.levelOrder(expected, testTree)
        }

        // Get traversal output
        val iter = tree.traverse(travOrder)
        while (iter.hasNext())
            out.add(iter.next())

        // The output and the expected size better be the same size
        if (out.size != expected.size)
            return false

        // Compare output to expected
        for (i in out.indices)
            if (expected[i] != out[i])
                return false

        return true
    }

    private fun genRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (index in 0 until size)
            list.add(index)
        list.shuffle()
        return list
    }

}
