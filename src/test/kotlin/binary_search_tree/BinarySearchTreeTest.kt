package binary_search_tree

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class TestTreeNode(
    var data: Int,
    var left: TestTreeNode?,
    var right: TestTreeNode?
) {
    companion object {
        fun add(node: TestTreeNode?, data: Int): TestTreeNode {
            val internalNode: TestTreeNode
            if (node == null) {
                internalNode = TestTreeNode(data, null, null)
            } else {
                // Place lower elem values on left
                if (data < node.data) {
                    node.left = add(node.left, data)
                } else {
                    node.right = add(node.right, data)
                }
                internalNode = node
            }
            return internalNode
        }

        fun preOrder(items: MutableList<Int>, node: TestTreeNode?) {
            if (node == null)
                return

            items.add(node.data)
            if (node.left != null) preOrder(items, node.left)
            if (node.right != null) preOrder(items, node.right)
        }

        fun inOrder(items: MutableList<Int>, node: TestTreeNode?) {
            if (node == null) return

            if (node.left != null) inOrder(items, node.left)
            items.add(node.data)
            if (node.right != null) inOrder(items, node.right)
        }

        fun postOrder(items: MutableList<Int>, node: TestTreeNode?) {
            if (node == null) return

            if (node.left != null) postOrder(items, node.left)
            if (node.right != null) postOrder(items, node.right)
            items.add(node.data)
        }

        fun levelOrder(lst: MutableList<Int>, node: TestTreeNode?) {
            val dequeue = ArrayDeque<TestTreeNode>()
            if (node != null)
                dequeue.offer(node)

            while (!dequeue.isEmpty()) {
                val internalNode = dequeue.poll()
                lst.add(internalNode!!.data)
                if (internalNode.left != null) dequeue.offer(internalNode.left!!)
                if (internalNode.right != null) dequeue.offer(internalNode.right!!)
            }
        }
    }
}

internal class BinarySearchTreeTest {
    private val loopCount = 9

    @Test
    fun `test is empty`() {
        val tree = BinarySearchTree<String>()
        assertTrue(tree.isEmpty())

        tree.add("Hello World!")
        assertFalse(tree.isEmpty())
    }

    @Test
    fun `test size`() {
        val tree = BinarySearchTree<String>()
        assertEquals(tree.size(), 0)

        tree.add("Hello World!")
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
        tree.add("M")
        assertEquals(tree.height(), 1)

        // Layer Two
        tree.add("J")
        assertEquals(tree.height(), 2)
        tree.add("S")
        assertEquals(tree.height(), 2)

        // Layer Three
        tree.add("B")
        assertEquals(tree.height(), 3)
        tree.add("N")
        assertEquals(tree.height(), 3)
        tree.add("Z")
        assertEquals(tree.height(), 3)

        // Layer 4
        tree.add("A")
        assertEquals(tree.height(), 4)
    }

    @Test
    fun `test add`() {
        // Add element which does not yet exist
        val tree = BinarySearchTree<Char>()
        assertTrue(tree.add('A'))
        assertEquals(1, tree.size())

        // Add duplicate element
        assertFalse(tree.add('A'))
        assertEquals(1, tree.size())

        // Add a second element which is not a duplicate
        assertTrue(tree.add('B'))
        assertEquals(2, tree.size())
    }

    @Test
    fun `test remove`() {
        // Try removing an element which doesn't exist
        val tree = BinarySearchTree<Char>()
        tree.add('A')
        assertEquals(tree.size(), 1)
        assertFalse(tree.remove('B'))
        assertEquals(tree.size(), 1)

        // Try removing an element which does exist
        tree.add('B')
        assertEquals(tree.size(), 2)
        assertTrue(tree.remove('B'))
        assertEquals(tree.size(), 1)
        assertEquals(1, tree.height())

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

        tree.add("M")
        assertTrue(tree.remove("M"))
        assertFalse(tree.remove("M"))

        tree.add("M")
        tree.add("J")
        tree.add("S")
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
        tree.add(6)
        tree.add(7)
        tree.add(3)
        tree.add(2)
        tree.add(5)
        tree.add(4)
        assertEquals(6, tree.size())

        tree.remove(6)
        assertTrue(tree.contains(2))
        assertTrue(tree.contains(3))
    }

    @Test
    fun `test contains`() {
        // Setup tree
        val tree = BinarySearchTree<Char>()
        tree.add('B')
        tree.add('A')
        tree.add('C')

        // Try looking for an element which doesn't exist
        assertFalse(tree.contains('D'))

        // Try looking for an element which exists in the root
        assertTrue(tree.contains('B'))

        // Try looking for an element which exists as the left child of the root
        assertTrue(tree.contains('A'))

        // Try looking for an element which exists as the right child of the root
        assertTrue(tree.contains('C'))
    }

    @Test
    fun `concurrentModificationError PreOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.add(1)
        tree.add(2)
        tree.add(3)

        val iterator = tree.traverse(TreeTraversalOrder.PRE_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.add(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError InOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.add(1)
        tree.add(2)
        tree.add(3)

        val iterator = tree.traverse(TreeTraversalOrder.IN_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.add(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError PostOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.add(1)
        tree.add(2)
        tree.add(3)

        val iterator = tree.traverse(TreeTraversalOrder.POST_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.add(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError LevelOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.add(1)
        tree.add(2)
        tree.add(3)

        val iterator = tree.traverse(TreeTraversalOrder.LEVEL_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.add(0)
                iterator.next()
            }
        }
    }

    @Test
    fun `concurrentModificationError Removing PreOrder`() {
        val tree = BinarySearchTree<Int>()
        tree.add(1)
        tree.add(2)
        tree.add(3)

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
        tree.add(1)
        tree.add(2)
        tree.add(3)

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
        tree.add(1)
        tree.add(2)
        tree.add(3)

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
        tree.add(1)
        tree.add(2)
        tree.add(3)

        val iterator = tree.traverse(TreeTraversalOrder.LEVEL_ORDER)

        assertThrows(ConcurrentModificationException::class.java) {
            while (iterator.hasNext()) {
                tree.remove(2)
                iterator.next()
            }
        }
    }

    @Test
    fun `random Remove Tests`() {
        for (size in 0 until loopCount) {
            val tree = BinarySearchTree<Int>()
            val randomList = genRandList(size)
            for (value in randomList) {
                tree.add(value)
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
            tree.add(value)
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