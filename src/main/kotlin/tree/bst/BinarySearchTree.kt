package tree.bst

import queue.QueueUsingStaticArray
import stack.StackUsingDynamicArray
import tree.Node
import tree.TreeTraversalOrder
import kotlin.math.max

class BinarySearchTree<T : Comparable<T>> {
    var root: Node<T>? = null
        private set
    private var nodeCount: Int = 0
    private var modCount: Int = 0

    /**
     * Replaces the whole tree with an already-built node tree.
     * Used by [buildTree], which lives outside this class and so cannot touch [root] /
     * [nodeCount] directly.
     */
    internal fun replaceTree(newRoot: Node<T>?, itemCount: Int) {
        root = newRoot
        nodeCount = itemCount
        modCount += 1
    }

    fun size(): Int = nodeCount

    fun isEmpty(): Boolean = nodeCount == 0

    private fun find(node: Node<T>?, item: T): Node<T>? {
        if (node == null) return null

        val cmpResult = node.data.compareTo(item)
        if (cmpResult == 0)
            return node
        else if (cmpResult < 0)
            return find(node.right, item)
        else
            return find(node.left, item)
    }

    fun contains(item: T): Boolean {
        val node = find(root, item)
        return node != null
    }

    fun height(): Int {
        if (root != null) return height(root)
        return 0
    }

    /** Returns -1 for an absent subtree, so a leaf has height 0. */
    private fun height(node: Node<T>?): Int {
        if (node == null) return -1
        return 1 + max(height(node.left), height(node.right))
    }

    fun insert(data: T): Boolean {
        if (contains(data))
            return false

        root = insert(data, root)
        nodeCount += 1
        modCount += 1
        return true
    }

    private fun insert(data: T, node: Node<T>?): Node<T>? {
        if (node == null)
            return Node(data)

        val cmpResult = data.compareTo(node.data)
        if (cmpResult == 0)
            return node
        else if (cmpResult < 0) {
            node.left = insert(data, node.left)
            return node
        } else {
            node.right = insert(data, node.right)
            return node
        }
    }

    /* Iterative alternative: visits only one root-to-leaf path.

    fun insert(data: T): Boolean {
        if (root == null) {
            root = Node(data)
            nodeCount += 1
            modCount += 1
            return true
        }

        var current = root!!
        while (true) {
            val comparison = data.compareTo(current.data)
            if (comparison == 0) return false

            if (comparison < 0) {
                if (current.left == null) {
                    current.left = Node(data)
                    nodeCount += 1
                    modCount += 1
                    return true
                }
                current = current.left!!
            } else {
                if (current.right == null) {
                    current.right = Node(data)
                    nodeCount += 1
                    modCount += 1
                    return true
                }
                current = current.right!!
            }
        }
    }*/

    fun remove(data: T): Boolean {
        if (!contains(data))
            return false

        root = remove(data, root!!)
        nodeCount -= 1
        modCount += 1
        return true
    }

    private fun remove(data: T, node: Node<T>): Node<T>? {
        val cmpResult = data.compareTo(node.data)
        if (cmpResult < 0) {
            node.left = remove(data, node.left!!)
            return node
        } else if (cmpResult > 0) {
            node.right = remove(data, node.right!!)
            return node
        } else {
            if (node.left == null) {
                // No left child: promote the right subtree.
                return node.right
            } else if (node.right == null) {
                // No right child: promote the left subtree.
                return node.left
            } else {
                // Replace this value with its in-order successor, then remove the successor.
                val rightMin = findMin(node.right!!)
                node.data = rightMin.data
                node.right = remove(rightMin.data, node.right!!)
                return node
            }
        }
    }

    private fun findMin(node: Node<T>): Node<T> {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    fun traverse(order: TreeTraversalOrder): Iterator<T> {
        return when (order) {
            TreeTraversalOrder.PRE_ORDER -> traversePreOrder()
            TreeTraversalOrder.IN_ORDER -> traverseInOrder()
            TreeTraversalOrder.POST_ORDER -> traversePostOrder()
            TreeTraversalOrder.LEVEL_ORDER -> traverseLevelOrder()
        }
    }

    private fun traversePreOrder(): Iterator<T> {
        return object : Iterator<T> {
            val stack = StackUsingDynamicArray<Node<T>>()
            private val expectedModCount = modCount

            init {
                if (root != null)
                    stack.push(root!!)
            }

            override fun hasNext(): Boolean {
                checkForModification()
                return !stack.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw NoSuchElementException()

                val result = stack.pop()
                if (result.right != null) stack.push(result.right!!)
                if (result.left != null) stack.push(result.left!!)

                return result.data
            }

            private fun checkForModification() {
                if (expectedModCount != modCount)
                    throw ConcurrentModificationException()
            }
        }
    }

    private fun traverseInOrder(): Iterator<T> {
        return object : Iterator<T> {
            val stack = StackUsingDynamicArray<Node<T>>()
            private val expectedModCount = modCount
            private var current = root

            override fun hasNext(): Boolean {
                checkForModification()
                return current != null || !stack.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw NoSuchElementException()

                while (current != null) {
                    stack.push(current!!)
                    current = current!!.left
                }

                val node = stack.pop()
                current = node.right
                return node.data
            }

            private fun checkForModification() {
                if (expectedModCount != modCount)
                    throw ConcurrentModificationException()
            }
        }
    }

    private fun traversePostOrder(): Iterator<T> {
        return object : Iterator<T> {
            val stack = StackUsingDynamicArray<Node<T>>()
            private val expectedModCount = modCount
            private var current = root
            private var lastVisited: Node<T>? = null

            override fun hasNext(): Boolean {
                checkForModification()
                return current != null || !stack.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw NoSuchElementException()

                while (current != null || !stack.isEmpty()) {
                    if (current != null) {
                        stack.push(current!!)
                        current = current!!.left
                    } else {
                        val node = stack.peek()
                        if (node.right != null && node.right !== lastVisited) {
                            current = node.right
                        } else {
                            val visited = stack.pop()
                            lastVisited = visited
                            return visited.data
                        }
                    }
                }

                throw NoSuchElementException()
            }

            private fun checkForModification() {
                if (expectedModCount != modCount)
                    throw ConcurrentModificationException()
            }
        }
    }

    private fun traverseLevelOrder(): Iterator<T> {
        return object : Iterator<T> {
            private val expectedModCount = modCount
            val queue = QueueUsingStaticArray<Node<T>>(maxOf(nodeCount, 1))

            init {
                if (root != null)
                    queue.enqueue(root!!)
            }

            override fun hasNext(): Boolean {
                checkForModification()
                return !queue.isEmpty()
            }

            override fun next(): T {
                if (!hasNext())
                    throw NoSuchElementException()

                val node = queue.dequeue()
                if (node.left != null) queue.enqueue(node.left!!)
                if (node.right != null) queue.enqueue(node.right!!)

                return node.data
            }

            private fun checkForModification() {
                if (expectedModCount != modCount)
                    throw ConcurrentModificationException()
            }
        }
    }

}
