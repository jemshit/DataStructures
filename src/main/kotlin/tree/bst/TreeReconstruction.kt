package tree.bst

import tree.Node
import tree.TreeTraversalOrder

/**
 * Builds a tree from a traversal sequence of its nodes, replacing this tree's contents.
 *
 * - `PRE_ORDER` / `POST_ORDER` are only unambiguous for BSTs, because the in-order sequence
 *   of a BST is its keys in ascending order and is therefore implicitly known.
 * - `IN_ORDER` picks the middle element as the root, so it rebuilds a balanced tree.
 * - `LEVEL_ORDER` uniquely reconstructs a BST by reading the sequence level by level.
 *
 * Throws [IllegalArgumentException] when the sequence cannot describe a valid BST.
 * When building fails, the existing tree is left unchanged.
 */
fun <T : Comparable<T>> BinarySearchTree<T>.buildTree(
    values: List<T>,
    traversalOrder: TreeTraversalOrder
): BinarySearchTree<T> {
    val builtRoot = when (traversalOrder) {
        TreeTraversalOrder.PRE_ORDER -> buildTreePreOrder(values)
        TreeTraversalOrder.IN_ORDER -> buildTreeInOrder(values)
        TreeTraversalOrder.POST_ORDER -> buildTreePostOrder(values)
        TreeTraversalOrder.LEVEL_ORDER -> buildTreeLevelOrder(values)
    }

    replaceTree(builtRoot, values.size)
    return this
}

private fun <T : Comparable<T>> buildTreePreOrder(values: List<T>): Node<T>? {
    var nextIndex = 0

    fun build(lowerBound: T?, upperBound: T?): Node<T>? {
        if (nextIndex == values.size) return null

        val value = values[nextIndex]

        // this value belongs to a later ancestor subtree, so leave it unconsumed.
        if (!value.isBetween(lowerBound, upperBound)) return null

        val node = Node(value)
        // consume this Pre-order value as the current subtree root.
        nextIndex += 1

        //  Pre-order: root, then left subtree, then right subtree.
        node.left = build(lowerBound, value)
        node.right = build(value, upperBound)

        return node
    }

    val root = build(lowerBound = null, upperBound = null)
    require(nextIndex == values.size) { "Invalid pre-order traversal" }
    return root
}

/** An in-order traversal cannot preserve shape, so this deliberately constructs a balanced BST. */
private fun <T : Comparable<T>> buildTreeInOrder(values: List<T>): Node<T>? {
    for (index in 1 until values.size) {
        // A BST's in-order traversal must be strictly increasing; this also rejects duplicates.
        require(values[index - 1] < values[index]) { "Invalid in-order traversal" }
    }

    fun build(startIndex: Int, endIndexExclusive: Int): Node<T>? {
        if (startIndex >= endIndexExclusive) return null

        // Choosing the middle value makes this reconstructed BST balanced.
        val rootIndex = (startIndex + endIndexExclusive) / 2
        val node = Node(values[rootIndex])

        // In-order visits the complete left range, then the root, then the right range.
        node.left = build(startIndex, rootIndex)
        node.right = build(rootIndex + 1, endIndexExclusive)

        return node
    }

    return build(startIndex = 0, endIndexExclusive = values.size)
}

private fun <T : Comparable<T>> buildTreePostOrder(values: List<T>): Node<T>? {
    var nextIndex = values.lastIndex // Shared by recursive calls; each accepted value becomes one node.

    fun build(lowerBound: T?, upperBound: T?): Node<T>? {
        if (nextIndex < 0) return null

        val value = values[nextIndex]
        // This value belongs to a subtree an ancestor will build, so leave it unconsumed.
        if (!value.isBetween(lowerBound, upperBound)) return null

        val node = Node(value)
        nextIndex -= 1 // Consume this post-order value as the current subtree root.

        // Reading post-order backward gives root, then the complete right subtree, then the left.
        node.right = build(value, upperBound)
        node.left = build(lowerBound, value)

        return node
    }

    val root = build(lowerBound = null, upperBound = null)
    require(nextIndex == -1) { "Invalid post-order traversal" }
    return root
}

private fun <T : Comparable<T>> buildTreeLevelOrder(values: List<T>): Node<T>? {
    if (values.isEmpty()) return null

    val root = Node(values[0]) // The first level-order value is the root.
    val parents = ArrayDeque<PendingParent<T>>()
    parents.addLast(PendingParent(root, lowerBound = null, upperBound = null))

    // The queue holds parents whose child positions may still accept later values.
    for (index in 1 until values.size) {
        val value = values[index]
        var inserted = false

        // Try the earliest unfinished parent first, preserving level-order placement.
        while (parents.isNotEmpty() && !inserted) {
            val parent = parents.first()

            if (parent.node.left == null && value.isBetween(parent.lowerBound, parent.node.data)) {
                val child = Node(value)
                parent.node.left = child
                // Its subtree must stay between the parent's lower bound and its value.
                parents.addLast(PendingParent(child, parent.lowerBound, parent.node.data))
                inserted = true
            } else if (parent.node.right == null && value.isBetween(parent.node.data, parent.upperBound)) {
                val child = Node(value)
                parent.node.right = child
                // Its subtree must stay between its value and the parent's upper bound.
                parents.addLast(PendingParent(child, parent.node.data, parent.upperBound))
                // A parent is complete once its right child is attached.
                parents.removeFirst()
                inserted = true
            } else {
                // This value cannot be either child, so this parent is complete.
                parents.removeFirst()
            }
        }

        // No unfinished parent can accept this value, so the traversal is invalid.
        if (!inserted) throw IllegalArgumentException()
    }

    return root
}

/** A parent still waiting for one or both children in a level-order traversal. */
private data class PendingParent<T : Comparable<T>>(
    val node: Node<T>,
    val lowerBound: T?,
    val upperBound: T?
)

private fun <T : Comparable<T>> T.isBetween(lowerBound: T?, upperBound: T?): Boolean =
    (lowerBound == null || this > lowerBound) && (upperBound == null || this < upperBound)
