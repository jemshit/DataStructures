package tree

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
