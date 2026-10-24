# Kotlin Data Structures

A focused collection of data structures implemented from scratch in Kotlin. Built for learning, interview preparation, and understanding the trade-offs behind the abstractions used every day.

Each implementation favors readable code, clear APIs, and tests that demonstrate expected behavior.

## Linear data structures

- [Dynamic arrays](src/main/kotlin/array) — resizable arrays and circular buffers.
- [Linked lists](src/main/kotlin/linked_list) — singly and doubly linked lists.
- [Stacks](src/main/kotlin/stack) — array-, linked-list-, and queue-backed stacks.
- [Queues and deques](src/main/kotlin/queue) — array-, linked-list-, and stack-backed queues, deques, and monotonic queues.

## Hierarchical data structures

- [Trees](src/main/kotlin/tree) — tree nodes and traversal algorithms.
- [Binary search trees](src/main/kotlin/tree/bst) — ordered trees and reconstruction.
- [AVL trees](src/main/kotlin/tree/avl) — self-balancing binary search trees.
- [Heaps](src/main/kotlin/heap) — binary and d-ary min heaps.
- [Disjoint sets](src/main/kotlin/disjoint_set) — union-find with union by rank and size.
- [Binary indexed trees](src/main/kotlin/binary_index_tree) — efficient prefix-sum queries and updates.

## Hashing

- [Hash tables](src/main/kotlin/hash_table) — separate chaining and linear-probing strategies.
- [Hash sets](src/main/kotlin/hash_set) — a map-backed set implementation.

## String data structures

- [Tries](src/main/kotlin/trie) — prefix trees for string lookup.
- [Suffix arrays](src/main/kotlin/suffix_array) — slow, medium, and optimized constructions, plus [string-search problems](src/main/kotlin/suffix_array/problems).

## Run the tests

```bash
./gradlew test
```

Inspired by [William Fiset](https://www.youtube.com/watch?v=RBSGKlAvoiM) and [Donglai Fu](https://github.com/labuladong/fucking-algorithm).
