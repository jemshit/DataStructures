package binary_search_tree

import stack.StackUsingDynamicArray
import kotlin.math.max

enum class TreeTraversalOrder {
    PRE_ORDER,
    IN_ORDER,
    POST_ORDER,
    LEVEL_ORDER
}

class BinarySearchTree<T : Comparable<T>> {
    private var root: Node<T>? = null
    private var nodeCount: Int = 0

    class Node<T : Comparable<T>> {
        var data: T
        var leftChild: Node<T>? = null
        var rightChild: Node<T>? = null

        constructor(data: T, leftChild: Node<T>? = null, rightChild: Node<T>? = null) {
            this.data = data
            this.leftChild = leftChild
            this.rightChild = rightChild
        }
    }

    // standard
    fun size(): Int = nodeCount

    fun isEmpty(): Boolean = nodeCount == 0

    private fun find(node: Node<T>?, item: T): Node<T>? {
        if (node == null) return null

        val cmpResult = node.data.compareTo(item)
        if (cmpResult == 0)
            return node
        else if (cmpResult < 0)
            return find(node.rightChild, item)
        else
            return find(node.leftChild, item)
    }

    fun contains(item: T): Boolean {
        val node = find(root, item)
        return node != null
    }

    fun height(): Int {
        if (root == null)
            return 0
        return height(root!!)
    }

    private fun height(node: Node<T>): Int {
        val rightHeight = if (node.rightChild == null) 0 else height(node.rightChild!!)
        val leftHeight = if (node.leftChild == null) 0 else height(node.leftChild!!)

        if (node == root)
            return max(leftHeight, rightHeight)
        else
            return 1 + max(leftHeight, rightHeight)
    }

    // insert
    fun insert(data: T): Boolean {
        if (contains(data))
            return false

        nodeCount += 1
        root = insert(data, root)
        return true
    }

    private fun insert(data: T, node: Node<T>?): Node<T>? {
        if (node == null)
            return Node(data)

        val cmpResult = data.compareTo(node.data)
        if (cmpResult == 0)
            return node
        else if (cmpResult < 0) {
            node.leftChild = insert(data, node.leftChild)
            return node
        } else {
            node.rightChild = insert(data, node.rightChild)
            return node
        }
    }

    // remove
    fun remove(data: T): Boolean {
        if (!contains(data))
            return false

        nodeCount -= 1
        root = remove(data, root!!)
        return true
    }

    private fun remove(data: T, node: Node<T>): Node<T>? {
        val cmpResult = data.compareTo(node.data)
        if (cmpResult < 0) {
            node.leftChild = remove(data, node.leftChild!!)
            return node
        } else if (cmpResult > 0) {
            node.rightChild = remove(data, node.rightChild!!)
            return node
        } else {
            if (node.leftChild == null) {
                // node.right is not changed, no balance needed
                return node.rightChild
            } else if (node.rightChild == null) {
                // node.left is not changed, no balance needed
                return node.leftChild
            } else {
                val rightMin = findMin(node.rightChild!!)
                node.data = rightMin.data
                node.rightChild = remove(rightMin.data, node.rightChild!!)
                return node
            }
        }
    }

    private fun findMin(node: Node<T>): Node<T> {
        if (node.leftChild == null)
            return node
        return findMin(node.leftChild!!)
    }

    // traverse
    fun traverse(order: TreeTraversalOrder): Iterator<T> {
        return when (order) {
            TreeTraversalOrder.PRE_ORDER -> preOrderTraversal()
            TreeTraversalOrder.IN_ORDER -> inOrderTraversal()
            TreeTraversalOrder.POST_ORDER -> postOrderTraversal()
            TreeTraversalOrder.LEVEL_ORDER -> levelOrderTraversal()
        }
    }

    private fun preOrderTraversal(): Iterator<T> {
        return object : Iterator<T> {
            val stack = StackUsingDynamicArray<Node<T>>()
            private val initialSize = size()

            init {
                if (root != null)
                    stack.push(root!!)
            }

            override fun hasNext(): Boolean {
                if (initialSize != size())
                    throw ConcurrentModificationException()
                return !stack.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw IllegalAccessException()

                val result = stack.pop()
                if (result.rightChild != null) stack.push(result.rightChild!!)
                if (result.leftChild != null) stack.push(result.leftChild!!)

                return result.data
            }
        }
    }

    private fun postOrderTraversal(): Iterator<T> {
        return object : Iterator<T> {
            val stack = StackUsingDynamicArray<Pair<Node<T>, Boolean>>()
            private val initialSize = size()

            init {
                if (root != null)
                    stack.push(Pair(root!!, false))
            }

            override fun hasNext(): Boolean {
                if (initialSize != size())
                    throw ConcurrentModificationException()
                return !stack.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw IllegalAccessException()

                var (top, childPushed) = stack.pop()
                if (childPushed) return top.data

                while (top.leftChild != null || top.rightChild != null) {
                    stack.push(Pair(top, true))
                    if (top.rightChild != null) stack.push(Pair(top.rightChild!!, false))
                    if (top.leftChild != null) stack.push(Pair(top.leftChild!!, false))

                    val (newTop, newChildPushed) = stack.pop()
                    top = newTop
                    childPushed = newChildPushed
                    if (childPushed) return top.data
                }

                return top.data
            }
        }
    }

    private fun inOrderTraversal(): Iterator<T> {
        return object : Iterator<T> {
            val stack = StackUsingDynamicArray<Pair<Node<T>, Boolean>>()
            private val initialSize = size()

            init {
                if (root != null)
                    stack.push(Pair(root!!, false))
            }

            override fun hasNext(): Boolean {
                if (initialSize != size())
                    throw ConcurrentModificationException()
                return !stack.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw IllegalAccessException()

                var (top, childPushed) = stack.pop()
                if (childPushed) return top.data

                while (top.leftChild != null || top.rightChild != null) {
                    if (top.rightChild != null) stack.push(Pair(top.rightChild!!, false))
                    stack.push(Pair(top, true))
                    if (top.leftChild != null) stack.push(Pair(top.leftChild!!, false))

                    val (newTop, newChildPushed) = stack.pop()
                    top = newTop
                    if (newChildPushed) return top.data
                }

                return top.data
            }
        }
    }

    private fun levelOrderTraversal(): Iterator<T> {
        return object : Iterator<T> {
            private val initialSize = size()
            val nodeQueue = queue.QueueUsingStaticArray<Node<T>>(initialSize)

            init {
                if (root != null)
                    nodeQueue.enqueue(root!!)
            }

            override fun hasNext(): Boolean {
                if (initialSize != size())
                    throw ConcurrentModificationException()
                return !nodeQueue.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw IllegalAccessException()

                val node = nodeQueue.dequeue()
                if (node.leftChild != null) nodeQueue.enqueue(node.leftChild!!)
                if (node.rightChild != null) nodeQueue.enqueue(node.rightChild!!)

                return node.data
            }
        }
    }

    // build tree
    fun buildTree(
        nodes: List<T>,
        traversalOrder: TreeTraversalOrder
    ): BinarySearchTree<T> {
        when (traversalOrder) {
            TreeTraversalOrder.PRE_ORDER -> {
                root = buildTreePreOrder(nodes)
                return this
            }
            TreeTraversalOrder.IN_ORDER -> {
                root = buildTreeInOrder(nodes)
                return this
            }
            TreeTraversalOrder.POST_ORDER -> {
                root = buildTreePostOrder(nodes)
                return this
            }
            TreeTraversalOrder.LEVEL_ORDER -> {
                root = buildTreeLevelOrder(nodes)
                return this
            }
        }
    }

    private fun buildTreePreOrder(nodes: List<T>): Node<T>? {
        if (nodes.isEmpty())
            return null

        val root = Node(nodes.get(0), null, null)
        val leftItems = mutableListOf<T>()
        val rightItems = mutableListOf<T>()

        var filledLeft = false
        for (index in 1 until nodes.size) {
            if (nodes.get(index) == root.data)
                throw IllegalArgumentException()

            if (nodes.get(index) < root.data) {
                if (filledLeft)
                    throw IllegalArgumentException()
                leftItems.add(nodes.get(index))
            }
            if (nodes.get(index) > root.data) {
                filledLeft = true
                rightItems.add(nodes.get(index))
            }
        }

        root.leftChild = if (leftItems.isEmpty()) null else buildTreePreOrder(leftItems)
        root.rightChild = if (rightItems.isEmpty()) null else buildTreePreOrder(rightItems)

        nodeCount += 1
        return root
    }

    // Multiple ways to build tree, this will try to build balanced tree
    private fun buildTreeInOrder(nodes: List<T>): Node<T>? {
        if (nodes.isEmpty())
            return null

        val balancedRootIndex = nodes.size / 2
        val root = Node(nodes.get(balancedRootIndex), null, null)

        val leftItems = mutableListOf<T>()
        val rightItems = mutableListOf<T>()

        var filledLeft = false
        for (index in 0 until nodes.size) {
            if (index == balancedRootIndex) {
                filledLeft = true
            } else if (index < balancedRootIndex) {
                if (filledLeft || nodes.get(index) >= root.data)
                    throw IllegalArgumentException()
                leftItems.add(nodes.get(index))
            } else if (index > balancedRootIndex) {
                if (nodes.get(index) <= root.data)
                    throw IllegalArgumentException()
                rightItems.add(nodes.get(index))
            }
        }

        root.leftChild = buildTreeInOrder(leftItems)
        root.rightChild = buildTreeInOrder(rightItems)

        nodeCount += 1
        return root
    }

    private fun buildTreePostOrder(nodes: List<T>): Node<T>? {
        if (nodes.isEmpty())
            return null

        val root = Node(nodes.get(nodes.size - 1), null, null)
        val leftItems = mutableListOf<T>()
        val rightItems = mutableListOf<T>()

        var rightFilled = false
        for (reverseIndex in (nodes.size - 2) downTo 0) {
            if (nodes.get(reverseIndex) > root.data) {
                if (rightFilled)
                    throw IllegalArgumentException()
                rightItems.add(nodes.get(reverseIndex))
            } else if (nodes.get(reverseIndex) < root.data) {
                rightFilled = true
                leftItems.add(nodes.get(reverseIndex))
            } else {
                throw IllegalArgumentException()
            }
        }

        root.leftChild = buildTreePostOrder(leftItems)
        root.rightChild = buildTreePostOrder(rightItems)

        nodeCount += 1
        return root
    }

    private fun buildTreeLevelOrder(nodes: List<T>): Node<T>? {
        if (nodes.isEmpty())
            return null

        val root = Node(nodes.get(0), null, null)
        val leftItems = mutableListOf<T>()
        val rightItems = mutableListOf<T>()

        for (index in 1 until nodes.size) {
            if (nodes.get(index) == root.data) {
                throw IllegalArgumentException()
            }
            if (nodes.get(index) < root.data) {
                if (leftItems.isEmpty() && rightItems.isNotEmpty())
                    throw IllegalArgumentException()
                leftItems.add(nodes.get(index))
            }
            if (nodes.get(index) > root.data) {
                rightItems.add(nodes.get(index))
            }
        }

        root.leftChild = if (leftItems.isEmpty()) null else buildTreePreOrder(leftItems)
        root.rightChild = if (rightItems.isEmpty()) null else buildTreePreOrder(rightItems)

        nodeCount += 1
        return root
    }

}