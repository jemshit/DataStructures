package avl_tree

import kotlin.math.max

class AVLTree<T : Comparable<T>> {
    var root: AVLNode<T>? = null
        private set
    private var nodeCount: Int = 0

    class AVLNode<T : Comparable<T>> {
        var data: T
        var leftChild: AVLNode<T>?
        var rightChild: AVLNode<T>?

        var balanceFactor: Int = 0
        var height: Int = 0

        constructor(data: T, leftChild: AVLNode<T>? = null, rightChild: AVLNode<T>? = null) {
            this.data = data
            this.leftChild = leftChild
            this.rightChild = rightChild

            this.update()
        }

        fun update() {
            val rightHeight = if (rightChild == null) -1 else rightChild!!.height
            val leftHeight = if (leftChild == null) -1 else leftChild!!.height

            this.height = 1 + max(leftHeight, rightHeight)
            this.balanceFactor = rightHeight - leftHeight
        }
    }

    // standards
    fun size(): Int = nodeCount

    fun isEmpty(): Boolean = nodeCount == 0

    fun height(): Int = root?.height ?: 0

    private fun find(data: T, node: AVLNode<T>?): AVLNode<T>? {
        if (node == null) return null

        val cmpResult = data.compareTo(node.data)
        if (cmpResult == 0)
            return node
        else if (cmpResult < 0)
            return find(data, node.leftChild)
        else
            return find(data, node.rightChild)
    }

    fun contains(data: T): Boolean {
        val node = find(data, root)
        return node != null
    }

    // rotations: O(1)
    private fun rightRotate(node: AVLNode<T>): AVLNode<T> {
        val leftChild = node.leftChild!!
        node.leftChild = leftChild.rightChild
        leftChild.rightChild = node

        // update height
        node.update() // node became child of 'leftChild', update it first
        leftChild.update()

        return leftChild
    }

    private fun leftRotate(node: AVLNode<T>): AVLNode<T> {
        val rightChild = node.rightChild!!
        node.rightChild = rightChild.leftChild
        rightChild.leftChild = node

        // update height
        node.update() // node became child of 'rightChild', update it first
        rightChild.update()

        return rightChild
    }

    // unbalanced cases: O(1)
    private fun leftLeftCase(node: AVLNode<T>): AVLNode<T> {
        return rightRotate(node)
    }

    private fun leftRightCase(node: AVLNode<T>): AVLNode<T> {
        node.leftChild = leftRotate(node.leftChild!!)
        return rightRotate(node)
    }

    private fun rightRightCase(node: AVLNode<T>): AVLNode<T> {
        return leftRotate(node)
    }

    private fun rightLeftCase(node: AVLNode<T>): AVLNode<T> {
        node.rightChild = rightRotate(node.rightChild!!)
        return leftRotate(node)
    }

    // balance: O(1)
    private fun balance(node: AVLNode<T>): AVLNode<T> {
        if (node.balanceFactor == -2) {
            // left subtree is tall
            if (node.leftChild!!.balanceFactor <= 0) // if leftChild.bf is 0, then we still do leftLeftCase
                return leftLeftCase(node)
            else
                return leftRightCase(node)
        } else if (node.balanceFactor == +2) {
            // right subtree is tall
            if (node.rightChild!!.balanceFactor >= 0) // if rightChild.bf is 0, then we still do rightRightCase
                return rightRightCase(node)
            else
                return rightLeftCase(node)
        }

        // -1, 0, +1 are balanced
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
            node.leftChild = insert(data, node.leftChild)
        else
            node.rightChild = insert(data, node.rightChild)

        // balance
        // In Recursive BST insert, we get pointers to all ancestors one by one in bottom-up manner.
        // So we don't need parent pointer to travel up and update/balance
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
            node.leftChild = remove(data, node.leftChild!!)
        else if (cmpResult > 0)
            node.rightChild = remove(data, node.rightChild!!)
        else {
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
            }
        }

        // balance
        // accesses all parent nodes in bottom up manner
        node.update()
        return balance(node)
    }

    private fun findMin(node: AVLNode<T>): AVLNode<T> {
        if (node.leftChild != null)
            return findMin(node.leftChild!!)
        return node
    }
}