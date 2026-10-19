package tree

enum class TreeTraversalOrder {
    PRE_ORDER,
    IN_ORDER,
    POST_ORDER,
    LEVEL_ORDER
}

/**
 * Iterative pre-order traversal (node -> `left` -> `right`) that takes any [root] and
 * returns the visited nodes in order.
 *
 * The right child is pushed before the left child, so the left child is popped and visited
 * first. Each node is pushed and popped at most once, giving O(n) time and O(h) extra space
 * for a tree of height h.
 */
fun <T : Comparable<T>> traversePreOrder(root: Node<T>?): List<Node<T>> {
    if (root == null) return emptyList()

    val visited = mutableListOf<Node<T>>()
    val stack = ArrayDeque<Node<T>>() // Stack
    stack.addLast(root)

    while (stack.isNotEmpty()) {
        val node = stack.removeLast()
        visited.add(node)

        // push right first so the left child is popped and visited first.
        if (node.right != null) stack.addLast(node.right!!)
        if (node.left != null) stack.addLast(node.left!!)
    }

    return visited
}

/**
 * Iterative in-order traversal (`left` -> node -> `right`) that takes any [root] and
 * returns the visited nodes in order.
 *
 * The stack records the path back to pending ancestors. Each node is pushed and popped
 * at most once, giving O(n) time and O(h) extra space for a tree of height h.
 */
fun <T : Comparable<T>> traverseInOrder(root: Node<T>?): List<Node<T>> {
    val visited = mutableListOf<Node<T>>()
    val stack = ArrayDeque<Node<T>>()

    var current = root
    while (current != null || stack.isNotEmpty()) {
        // descend as far left as possible
        while (current != null) {
            // remembering the path back up
            stack.addLast(current)
            // go left
            current = current.left
        }

        // `current` is null because we followed left links until reaching a node
        // with no left child. The stack now contains the path back to ancestors.
        //
        // The top node is the next node in in-order order. It is either:
        // - a leaf, or
        // - a node whose left subtree has been fully visited; its right subtree
        //   is the next subtree to traverse.
        val node = stack.removeLast()
        visited.add(node)
        current = node.right
    }

    return visited
}

/**
 * Iterative post-order traversal (`left` -> `right` -> node) that takes any [root] and
 * returns the visited nodes in order.
 *
 * The stack records the path back to pending ancestors. After returning from a node's left
 * subtree, [lastVisited] determines whether to traverse its right subtree or visit the node.
 * Each node is pushed and popped at most once, giving O(n) time and O(h) extra space for a
 * tree of height h.
 */
fun <T : Comparable<T>> traversePostOrder(root: Node<T>?): List<Node<T>> {
    val visited = mutableListOf<Node<T>>()
    val stack = ArrayDeque<Node<T>>()
    var current = root
    var lastVisited: Node<T>? = null

    while (current != null || stack.isNotEmpty()) {
        if (current != null) {
            // descend as far left as possible.
            stack.addLast(current)
            current = current.left
        } else {
            // it is either:
            // - a leaf, or
            // - a node whose left subtree has been fully visited; its right subtree
            //   is the next subtree to traverse.
            val node = stack.last()

            // if the right child exists and is not the one we just visited, traverse it
            if (node.right != null && lastVisited !== node.right) {
                current = node.right
            } else {
                // both subtrees have been traversed, so this node can be visited.
                visited.add(stack.removeLast())
                lastVisited = node
            }
        }
    }

    return visited
}

/**
 * Iterative level-order traversal that takes any [root] and returns the visited nodes in
 * breadth-first order, from left to right within each level.
 *
 * The queue holds nodes waiting to be visited. Each node is added and removed once, giving
 * O(n) time and O(w) extra space, where w is the tree's maximum width.
 */
fun <T : Comparable<T>> traverseLevelOrder(root: Node<T>?): List<Node<T>> {
    if (root == null) return emptyList()

    val visited = mutableListOf<Node<T>>()
    val queue = ArrayDeque<Node<T>>()
    queue.addLast(root)

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        visited.add(node)

        // Add left before right so nodes at each level are visited from left to right.
        if (node.left != null) queue.addLast(node.left!!)
        if (node.right != null) queue.addLast(node.right!!)
    }

    return visited
}

/**
 * Iterative level-order traversal that returns every visited node together with its depth.
 * The root has depth 1; every child is one level deeper than its parent.
 */
fun <T : Comparable<T>> traverseLevelOrderWithDepth(root: Node<T>?): List<NodeAtDepth<T>> {
    if (root == null) return emptyList()

    val visited = mutableListOf<NodeAtDepth<T>>()
    val queue = ArrayDeque<NodeAtDepth<T>>()
    queue.addLast(NodeAtDepth(root, depth = 1))

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        visited.add(current)

        if (current.node.left != null) {
            queue.addLast(NodeAtDepth(current.node.left!!, current.depth + 1))
        }
        if (current.node.right != null) {
            queue.addLast(NodeAtDepth(current.node.right!!, current.depth + 1))
        }
    }

    return visited
}

/**
 * Iterative level-order traversal that tracks the current level with a queue-size snapshot.
 * It returns nodes in breadth-first order and currently prints each node's level while visiting it.
 */
fun <T : Comparable<T>> traverseLevelOrderByLevel(root: Node<T>?): List<Node<T>> {
    if (root == null) return emptyList()

    val visited = mutableListOf<Node<T>>()
    val queue = ArrayDeque<Node<T>>()
    queue.addLast(root)
    var level = 1

    while (queue.isNotEmpty()) {
        val nodesAtCurrentLevel = queue.size

        for (index in 0 until nodesAtCurrentLevel) {
            val node = queue.removeFirst()
            visited.add(node)
            println("level = $level, value = ${node.data}")

            if (node.left != null) queue.addLast(node.left!!)
            if (node.right != null) queue.addLast(node.right!!)
        }

        level += 1
    }

    return visited
}

/**
 * Recursive level-order traversal that takes any [root] and returns the visited nodes in
 * breadth-first order, from left to right within each level.
 *
 * Each call visits one level and builds a list of nodes for the next level. This uses O(n)
 * time and O(n) extra space in the worst case, so [traverseLevelOrder] is preferred when a
 * queue is allowed.
 */
fun <T : Comparable<T>> traverseLevelOrderRecursive(root: Node<T>?): List<Node<T>> {
    val visited = mutableListOf<Node<T>>()

    fun traverse(currentLevelNodes: List<Node<T>>) {
        if (currentLevelNodes.isEmpty()) return

        val nextLevelNodes = mutableListOf<Node<T>>()

        for (node in currentLevelNodes) {
            visited.add(node)
            if (node.left != null) nextLevelNodes.add(node.left!!)
            if (node.right != null) nextLevelNodes.add(node.right!!)
        }

        traverse(nextLevelNodes)
    }

    if (root != null) traverse(listOf(root))
    return visited
}
