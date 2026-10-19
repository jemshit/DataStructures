package tree

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import tree.bst.BinarySearchTree

internal class TreeTraversalsTest {
    private val loopCount = 9

    private fun genRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (index in 0 until size)
            list.add(index)
        list.shuffle()
        return list
    }

    @Test
    fun `test file level traverseInOrder`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)

            val tree = BinarySearchTree<Int>()
            var testTree: TestTreeNode? = null
            for (value in input) {
                testTree = TestTreeNode.add(testTree, value)
                tree.insert(value)
            }

            val expected = ArrayList<Int>()
            TestTreeNode.inOrder(expected, testTree)

            // The standalone function is given the root node directly
            val actual = traverseInOrder(tree.root).map { it.data }

            assertEquals(expected, actual)
        }
    }

    @Test
    fun `test file level traverseInOrder empty tree`() {
        assertTrue(traverseInOrder<Int>(null).isEmpty())
    }

    @Test
    fun `test file level traverseInOrder hand built tree`() {
        //   2
        //  / \
        // 1   3
        val root = Node(2, Node(1), Node(3))

        val visited = traverseInOrder(root)

        assertEquals(listOf(1, 2, 3), visited.map { it.data })
        // it returns the actual nodes, not copies of the values
        assertSame(root.left, visited[0])
        assertSame(root, visited[1])
        assertSame(root.right, visited[2])
    }

    @Test
    fun `test file level traversePreOrder`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)

            val tree = BinarySearchTree<Int>()
            var testTree: TestTreeNode? = null
            for (value in input) {
                testTree = TestTreeNode.add(testTree, value)
                tree.insert(value)
            }

            val expected = ArrayList<Int>()
            TestTreeNode.preOrder(expected, testTree)

            assertEquals(expected, traversePreOrder(tree.root).map { it.data })
        }
    }

    @Test
    fun `test file level traversePreOrder empty tree`() {
        assertTrue(traversePreOrder<Int>(null).isEmpty())
    }

    @Test
    fun `test file level traversePreOrder hand built tree`() {
        //   2
        //  / \
        // 1   3
        val root = Node(2, Node(1), Node(3))

        val visited = traversePreOrder(root)

        assertEquals(listOf(2, 1, 3), visited.map { it.data })
        assertSame(root, visited[0])
        assertSame(root.left, visited[1])
        assertSame(root.right, visited[2])
    }

    @Test
    fun `test file level traversePostOrder`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)

            val tree = BinarySearchTree<Int>()
            var testTree: TestTreeNode? = null
            for (value in input) {
                testTree = TestTreeNode.add(testTree, value)
                tree.insert(value)
            }

            val expected = ArrayList<Int>()
            TestTreeNode.postOrder(expected, testTree)

            assertEquals(expected, traversePostOrder(tree.root).map { it.data })
        }
    }

    @Test
    fun `test file level traversePostOrder empty tree`() {
        assertTrue(traversePostOrder<Int>(null).isEmpty())
    }

    @Test
    fun `test file level traversePostOrder hand built tree`() {
        //   2
        //  / \
        // 1   3
        val root = Node(2, Node(1), Node(3))

        val visited = traversePostOrder(root)

        assertEquals(listOf(1, 3, 2), visited.map { it.data })
        assertSame(root.left, visited[0])
        assertSame(root.right, visited[1])
        assertSame(root, visited[2])
    }

    @Test
    fun `test file level traverseLevelOrder`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)

            val tree = BinarySearchTree<Int>()
            var testTree: TestTreeNode? = null
            for (value in input) {
                testTree = TestTreeNode.add(testTree, value)
                tree.insert(value)
            }

            val expected = ArrayList<Int>()
            TestTreeNode.levelOrder(expected, testTree)

            assertEquals(expected, traverseLevelOrder(tree.root).map { it.data })
        }
    }

    @Test
    fun `test file level traverseLevelOrder empty tree`() {
        assertTrue(traverseLevelOrder<Int>(null).isEmpty())
    }

    @Test
    fun `test file level traverseLevelOrder hand built tree`() {
        //       1
        //      / \
        //     2   3
        //    / \   \
        //   4   5   6
        val root = Node(1, Node(2, Node(4), Node(5)), Node(3, right = Node(6)))

        val visited = traverseLevelOrder(root)

        assertEquals(listOf(1, 2, 3, 4, 5, 6), visited.map { it.data })
        assertSame(root, visited[0])
        assertSame(root.left, visited[1])
        assertSame(root.right, visited[2])
    }

    @Test
    fun `test file level traverseLevelOrderWithDepth returns nodes with their depth`() {
        //       1
        //      / \
        //     2   3
        //    / \   \
        //   4   5   6
        val root = Node(1, Node(2, Node(4), Node(5)), Node(3, right = Node(6)))

        val visited = traverseLevelOrderWithDepth(root)

        assertEquals(
            listOf(1 to 1, 2 to 2, 3 to 2, 4 to 3, 5 to 3, 6 to 3),
            visited.map { it.node.data to it.depth }
        )
        assertSame(root, visited[0].node)
        assertSame(root.left, visited[1].node)
        assertSame(root.right, visited[2].node)
    }

    @Test
    fun `test file level traverseLevelOrderWithDepth empty tree`() {
        assertTrue(traverseLevelOrderWithDepth<Int>(null).isEmpty())
    }

    @Test
    fun `test file level traverseLevelOrderByLevel`() {
        //       1
        //      / \
        //     2   3
        //    / \   \
        //   4   5   6
        val root = Node(1, Node(2, Node(4), Node(5)), Node(3, right = Node(6)))

        val visited = traverseLevelOrderByLevel(root)

        assertEquals(listOf(1, 2, 3, 4, 5, 6), visited.map { it.data })
    }

    @Test
    fun `test file level traverseLevelOrderByLevel empty tree`() {
        assertTrue(traverseLevelOrderByLevel<Int>(null).isEmpty())
    }

    @Test
    fun `test file level traverseLevelOrderRecursive`() {
        for (index in 1 until loopCount) {
            val input = genRandList(index)

            val tree = BinarySearchTree<Int>()
            var testTree: TestTreeNode? = null
            for (value in input) {
                testTree = TestTreeNode.add(testTree, value)
                tree.insert(value)
            }

            val expected = ArrayList<Int>()
            TestTreeNode.levelOrder(expected, testTree)

            assertEquals(expected, traverseLevelOrderRecursive(tree.root).map { it.data })
        }
    }

    @Test
    fun `test file level traverseLevelOrderRecursive empty tree`() {
        assertTrue(traverseLevelOrderRecursive<Int>(null).isEmpty())
    }

    @Test
    fun `test file level traverseLevelOrderRecursive hand built tree`() {
        //       1
        //      / \
        //     2   3
        //    / \   \
        //   4   5   6
        val root = Node(1, Node(2, Node(4), Node(5)), Node(3, right = Node(6)))

        val visited = traverseLevelOrderRecursive(root)

        assertEquals(listOf(1, 2, 3, 4, 5, 6), visited.map { it.data })
        assertSame(root, visited[0])
        assertSame(root.left, visited[1])
        assertSame(root.right, visited[2])
    }
}
