package tree.bst

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import tree.TreeTraversalOrder

internal class BinarySearchTreePreOrderBuildTest {
    @Test
    fun `test buildPreOrder preserves traversal`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf(6, 4, 3, 5, 8, 7, 9)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(items, tree.traverse(TreeTraversalOrder.PRE_ORDER).asSequence().toList())
    }

    @Test
    fun `test buildPreorder empty`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>()

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(0, tree.size())
    }

    @Test
    fun `test buildPreorder 1 element`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(1, tree.size())
        assertTrue(tree.contains(1))
    }

    @Test
    fun `test buildPreorder 2 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 2)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(2, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(2))
    }

    @Test
    fun `test buildPreorder 3 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 0, 2)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(3, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(0))
        assertTrue(tree.contains(2))
    }

    @Test
    fun `test buildPreorder left only elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(4, 3, 2, 1)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(4, tree.size())
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(2))
        assertTrue(tree.contains(1))
    }

    @Test
    fun `test buildPreorder right only elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 2, 3, 4)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(4, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(2))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(4))
    }

    @Test
    fun `test buildPreorder check orders`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 4, 3, 5, 8, 7, 9)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(7, tree.size())
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(5))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(9))
    }

    @Test
    fun `test buildPreorder check orders 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 4, 3, 8, 7, 9)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(6, tree.size())
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(9))
    }

    @Test
    fun `test buildPreorder check orders 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 4, 5, 8, 7, 9)

        tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)

        assertEquals(6, tree.size())
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(5))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(9))
    }

    @Test
    fun `test buildPreorder incorrect tree`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 5, 7, 1)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)
        }
    }

    @Test
    fun `test buildPreorder incorrect tree 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 7, 1)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)
        }
    }

    @Test
    fun `test buildPreorder incorrect tree 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 6, 1)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)
        }
    }

    @Test
    fun `test buildPreorder incorrect tree 4`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 7, 6)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.PRE_ORDER)
        }
    }

}

internal class BinarySearchTreeInOrderBuildTest {
    @Test
    fun `test buildInOrder preserves traversal`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf(1, 2, 3, 4, 5, 6, 7)

        tree.buildTree(items, TreeTraversalOrder.IN_ORDER)

        assertEquals(items, tree.traverse(TreeTraversalOrder.IN_ORDER).asSequence().toList())
    }

    @Test
    fun `test buildInOrder empty`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>()

        tree.buildTree(items, TreeTraversalOrder.IN_ORDER)

        assertEquals(0, tree.size())
    }

    @Test
    fun `test buildInOrder 1 element`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1)

        tree.buildTree(items, TreeTraversalOrder.IN_ORDER)

        assertEquals(1, tree.size())
        assertTrue(tree.contains(1))
    }

    @Test
    fun `test buildInOrder 2 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 2)

        tree.buildTree(items, TreeTraversalOrder.IN_ORDER)

        assertEquals(2, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(2))
    }

    @Test
    fun `test buildInOrder 3 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 0, 2)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.IN_ORDER)
        }
    }

    @Test
    fun `test buildInOrder check orders`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 2, 3, 4, 5, 6, 7)

        tree.buildTree(items, TreeTraversalOrder.IN_ORDER)

        assertEquals(7, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(2))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(5))
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(7))
    }

    @Test
    fun `test buildInOrder check orders 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 2, 4, 5)

        tree.buildTree(items, TreeTraversalOrder.IN_ORDER)

        assertEquals(4, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(2))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(5))
    }

    @Test
    fun `test buildInOrder check orders 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(0, 3, 5, 7, 8)

        tree.buildTree(items, TreeTraversalOrder.IN_ORDER)

        assertEquals(5, tree.size())
        assertTrue(tree.contains(0))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(5))
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(8))
    }

    @Test
    fun `test buildInOrder incorrect tree`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 2, 4, 3, 2)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.IN_ORDER)
        }
    }

    @Test
    fun `test buildInOrder incorrect tree 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(4, 3, 2, 1)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.IN_ORDER)
        }
    }

    @Test
    fun `test buildInOrder incorrect tree 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 6, 1)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.IN_ORDER)
        }
    }

    @Test
    fun `test buildInOrder incorrect tree 4`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 7, 6)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.IN_ORDER)
        }
    }

}

internal class BinarySearchTreePostOrderBuildTest {
    @Test
    fun `test buildPostOrder preserves traversal`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf(3, 5, 4, 7, 9, 8, 6)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(items, tree.traverse(TreeTraversalOrder.POST_ORDER).asSequence().toList())
    }

    @Test
    fun `test buildPostOrder empty`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>()

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(0, tree.size())
    }

    @Test
    fun `test buildPostOrder 1 element`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(1, tree.size())
        assertTrue(tree.contains(1))
    }

    @Test
    fun `test buildPostOrder 2 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(3, 2)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(2, tree.size())
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(2))
    }

    @Test
    fun `test buildPostOrder 3 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1, 3, 2)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(3, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(2))
    }

    @Test
    fun `test buildPostOrder check orders`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(3, 5, 4, 7, 9, 8, 6)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(7, tree.size())
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(5))
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(8))
    }

    @Test
    fun `test buildPostOrder check orders 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(7, 9, 8)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(3, tree.size())
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(9))
    }

    @Test
    fun `test buildPostOrder check orders 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(7, 9, 8, 6)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(4, tree.size())
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(9))
        assertTrue(tree.contains(6))
    }

    @Test
    fun `test buildPostOrder check orders 4`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(3, 4, 6, 8)

        tree.buildTree(items, TreeTraversalOrder.POST_ORDER)

        assertEquals(4, tree.size())
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(8))
    }

    @Test
    fun `test buildPostOrder incorrect tree`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(10, 5, 9, 8, 6)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.POST_ORDER)
        }
    }

    @Test
    fun `test buildPostOrder incorrect tree 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 4, 5, 7, 6)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.POST_ORDER)
        }
    }

    @Test
    fun `test buildPostOrder incorrect tree 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 6)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.POST_ORDER)
        }
    }

}

internal class BinarySearchTreeLevelOrderBuildTest {
    @Test
    fun `test buildLevelOrder empty`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>()

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(0, tree.size())
    }

    @Test
    fun `test buildLevelOrder 1 element`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(1)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(1, tree.size())
        assertTrue(tree.contains(1))
    }

    @Test
    fun `test buildLevelOrder 2 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(3, 2)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(2, tree.size())
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(2))
    }

    @Test
    fun `test buildLevelOrder 3 elements`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(3, 1, 4)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(3, tree.size())
        assertTrue(tree.contains(1))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(4))
    }

    @Test
    fun `test buildLevelOrder check orders`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 4, 8, 3, 5, 7, 9)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(7, tree.size())
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(5))
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(8))
    }

    @Test
    fun `test buildLevelOrder with interleaved subtree levels`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf(20, 10, 30, 5, 15, 25, 35, 3, 7, 12, 17)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(items.size, tree.size())
        assertEquals(items, tree.traverse(TreeTraversalOrder.LEVEL_ORDER).asSequence().toList())
    }

    @Test
    fun `test buildLevelOrder check orders 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 4, 3, 5)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(4, tree.size())
        assertTrue(tree.contains(4))
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(3))
        assertTrue(tree.contains(5))
    }

    @Test
    fun `test buildLevelOrder check orders 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 8, 7, 9)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(4, tree.size())
        assertTrue(tree.contains(7))
        assertTrue(tree.contains(8))
        assertTrue(tree.contains(9))
        assertTrue(tree.contains(6))
    }

    @Test
    fun `test buildLevelOrder check orders 4`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 8, 9)

        tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)

        assertEquals(3, tree.size())
        assertTrue(tree.contains(9))
        assertTrue(tree.contains(6))
        assertTrue(tree.contains(8))
    }

    @Test
    fun `test buildLevelOrder incorrect tree`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 8, 4)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)
        }
    }

    @Test
    fun `test buildLevelOrder incorrect tree 5`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 4, 4)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)
        }
    }

    @Test
    fun `test buildLevelOrder incorrect tree 2`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 6, 8)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)
        }
    }

    @Test
    fun `test buildLevelOrder incorrect tree 3`() {
        val tree = BinarySearchTree<Int>()
        val items = listOf<Int>(6, 3, 6)

        assertThrows(IllegalArgumentException::class.java) {
            tree.buildTree(items, TreeTraversalOrder.LEVEL_ORDER)
        }
    }

}
