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
    private var nodeCount: Int = 0
    private var root: Node<T>? = null

    class Node<T : Comparable<T>> {
        var data: T
        var leftChild: Node<T>? = null
        var rightChild: Node<T>? = null

        constructor(data: T, leftChild: Node<T>?, rightChild: Node<T>?) {
            this.data = data
            this.leftChild = leftChild
            this.rightChild = rightChild
        }
    }

    fun size(): Int = nodeCount

    fun isEmpty(): Boolean = nodeCount == 0

    fun add(item: T): Boolean {
        if (root == null) {
            root = Node(item, null, null)
            nodeCount += 1
            return true
        }

        return add(root!!, item) != null
    }

    private fun add(node: Node<T>, item: T): Node<T>? {
        val compareResult = node.data.compareTo(item)
        if (compareResult == 0) {
            // Do Not allow duplicate nodes
            return null
        } else if (compareResult < 0) {
            if (node.rightChild == null) {
                // Add as right child
                node.rightChild = Node(item, null, null)
                nodeCount += 1
                return node.rightChild
            }
            return add(node.rightChild!!, item)
        } else {
            if (node.leftChild == null) {
                // Add as left child
                node.leftChild = Node(item, null, null)
                nodeCount += 1
                return node.leftChild
            }
            return add(node.leftChild!!, item)
        }
    }

    private var foundToDelete = false

    fun remove(item: T): Boolean {
        if (root == null)
            return false
        else if (root!!.data.compareTo(item) == 0 && nodeCount == 1) {
            root = null
            nodeCount -= 1
            return true
        }

        root = remove(root!!, item)
        return foundToDelete
    }

    private fun remove(
        node: Node<T>?,
        item: T
    ): Node<T>? {
        if (node == null)
            return null

        val comparison = node.data.compareTo(item)
        if (comparison < 0) {
            node.rightChild = remove(node.rightChild, item)
            return node

        } else if (comparison > 0) {
            node.leftChild = remove(node.leftChild, item)
            return node

        } else {
            foundToDelete = true
            if (node.leftChild == null) {
                nodeCount -= 1
                return node.rightChild

            } else if (node.rightChild == null) {
                nodeCount -= 1
                return node.leftChild

            } else {
                val substitution = findMin(node.rightChild!!)
                node.data = substitution.data
                node.rightChild = remove(node.rightChild!!, substitution.data)
                return node
            }
        }
    }


    private fun findMin(node: Node<T>): Node<T> {
        if (node.leftChild == null)
            return node
        return findMin(node.leftChild!!)
    }

    fun contains(item: T): Boolean {
        val nodeFound = find(root, item)
        return nodeFound != null
    }

    private fun find(
        node: Node<T>?,
        item: T
    ): Node<T>? {
        if (node == null)
            return null

        val compareResult = node.data.compareTo(item)
        if (compareResult == 0)
            return node
        else if (compareResult < 0)
            return find(node.rightChild, item)
        else
            return find(node.leftChild, item)
    }

    fun height(): Int {
        if (root == null)
            return 0
        return height(root!!)
    }

    private fun height(node: Node<T>): Int {
        if (node.leftChild == null && node.rightChild == null)
            return 1
        else if (node.leftChild == null) {
            return 1 + height(node.rightChild!!)
        } else if (node.rightChild == null) {
            return 1 + height(node.leftChild!!)
        } else {
            return 1 + max(height(node.rightChild!!), height(node.leftChild!!))
        }
    }

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
                if (root != null) {
                    if (root!!.rightChild != null)
                        stack.push(Pair(root!!.rightChild!!, false))

                    stack.push(Pair(root!!, true))

                    if (root!!.leftChild != null)
                        stack.push(Pair(root!!.leftChild!!, false))
                }
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
                return this
            }
            TreeTraversalOrder.POST_ORDER -> {
                return this
            }
            TreeTraversalOrder.LEVEL_ORDER -> {
                return this
            }
        }
    }


    private fun buildTreePreOrder(nodes: List<T>): Node<T>? {
        if (nodes.isEmpty())
            return null
        nodes.count()

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

}