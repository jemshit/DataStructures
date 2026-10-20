package tree.avl

import kotlin.math.max

class AVLNode<T : Comparable<T>>(
    var data: T,
    var left: AVLNode<T>? = null,
    var right: AVLNode<T>? = null,
) {
    var balanceFactor: Int = 0
    var height: Int = 0

    init {
        update()
    }

    fun update() {
        val rightHeight = if (right == null) -1 else right!!.height
        val leftHeight = if (left == null) -1 else left!!.height

        height = 1 + max(leftHeight, rightHeight)
        balanceFactor = rightHeight - leftHeight
    }
}

class AVLTree<T : Comparable<T>> {
    var root: AVLNode<T>? = null
        private set
    private var nodeCount: Int = 0

    // Basic queries
    fun size(): Int = nodeCount

    fun isEmpty(): Boolean = nodeCount == 0

    /** Returns the tree height in edges; both an empty tree and a leaf report 0. */
    fun height(): Int = root?.height ?: 0

    private fun find(node: AVLNode<T>?, data: T): AVLNode<T>? {
        if (node == null) return null

        val cmpResult = data.compareTo(node.data)
        if (cmpResult == 0)
            return node
        else if (cmpResult < 0)
            return find(node.left, data)
        else
            return find(node.right, data)
    }

    fun contains(data: T): Boolean {
        val node = find(root, data)
        return node != null
    }

    // rotations: O(1)
    private fun rightRotate(node: AVLNode<T>): AVLNode<T> {
        val left = node.left!!
        node.left = left.right
        left.right = node

        // Update cached height and balance factor. The lower node must be updated first.
        node.update() // node became child of 'left', update it first
        left.update()

        return left
    }

    private fun leftRotate(node: AVLNode<T>): AVLNode<T> {
        val right = node.right!!
        node.right = right.left
        right.left = node

        // Update cached height and balance factor. The lower node must be updated first.
        node.update() // node became child of 'right', update it first
        right.update()

        return right
    }

    // unbalanced cases: O(1)
    private fun leftLeftCase(node: AVLNode<T>): AVLNode<T> {
        return rightRotate(node)
    }

    private fun leftRightCase(node: AVLNode<T>): AVLNode<T> {
        node.left = leftRotate(node.left!!)
        return rightRotate(node)
    }

    private fun rightRightCase(node: AVLNode<T>): AVLNode<T> {
        return leftRotate(node)
    }

    private fun rightLeftCase(node: AVLNode<T>): AVLNode<T> {
        node.right = rightRotate(node.right!!)
        return leftRotate(node)
    }

    // balance: O(1)
    private fun balance(node: AVLNode<T>): AVLNode<T> {
        if (node.balanceFactor == -2) {
            // left subtree is tall
            val left = node.left!!
            if (left.balanceFactor <= 0) // if left.bf is 0, then we still do leftLeftCase
                return leftLeftCase(node)
            else
                return leftRightCase(node)
        } else if (node.balanceFactor == 2) {
            // right subtree is tall
            val right = node.right!!
            if (right.balanceFactor >= 0) // if right.bf is 0, then we still do rightRightCase
                return rightRightCase(node)
            else
                return rightLeftCase(node)
        }

        // -1, 0, and 1 are balanced.
        return node
    }

    // insert: O(logN)
    fun insert(data: T): Boolean {
        if (contains(data))
            return false

        nodeCount += 1
        root = insert(data, root)
        return true
    }

    private fun insert(data: T, node: AVLNode<T>?): AVLNode<T> {
        if (node == null)
            return AVLNode(data)

        val cmpResult = data.compareTo(node.data)
        if (cmpResult == 0)
            return node
        else if (cmpResult < 0)
            node.left = insert(data, node.left)
        else
            node.right = insert(data, node.right)

        // As recursive calls return, execution visits each ancestor from bottom to top.
        // That lets us update and rebalance the path to the root without parent pointers.
        node.update()
        return balance(node)
    }

    // remove: O(logN)
    fun remove(data: T): Boolean {
        if (!contains(data))
            return false

        nodeCount -= 1
        root = remove(data, root!!)
        return true
    }

    private fun remove(data: T, node: AVLNode<T>): AVLNode<T>? {
        val cmpResult = data.compareTo(node.data)
        if (cmpResult < 0)
            node.left = remove(data, node.left!!)
        else if (cmpResult > 0)
            node.right = remove(data, node.right!!)
        else {
            if (node.left == null) {
                // Replace this subtree directly. Ancestors update and rebalance while recursion unwinds.
                return node.right
            } else if (node.right == null) {
                // Replace this subtree directly. Ancestors update and rebalance while recursion unwinds.
                return node.left
            } else {
                val rightMin = findMin(node.right!!)
                node.data = rightMin.data
                node.right = remove(rightMin.data, node.right!!)
            }
        }

        // As recursive calls return, execution visits each ancestor from bottom to top.
        // That lets us update and rebalance the path to the root without parent pointers.
        node.update()
        return balance(node)
    }

    private fun findMin(node: AVLNode<T>): AVLNode<T> {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }
}
