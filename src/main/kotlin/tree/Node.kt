package tree

class Node<T : Comparable<T>>(
    var data: T,
    var left: Node<T>? = null,
    var right: Node<T>? = null,
)

data class NodeAtDepth<T : Comparable<T>>(
    val node: Node<T>,
    val depth: Int
)
