package queue

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DequeUsingCircularDynamicArrayTest {

    private lateinit var deque: DequeUsingCircularDynamicArray<Int>

    @BeforeEach
    fun beforeEach() {
        deque = DequeUsingCircularDynamicArray<Int>()
    }

    @Test
    fun `test empty deque`() {
        assertTrue(deque.isEmpty())
        assertEquals(deque.size(), 0)
    }

    @Test
    fun `test removeFirst of empty deque`() {
        assertThrows<NoSuchElementException> { deque.removeFirst() }
    }

    @Test
    fun `test removeLast of empty deque`() {
        assertThrows<NoSuchElementException> { deque.removeLast() }
    }

    @Test
    fun `test peekFirst of empty deque`() {
        assertThrows<NoSuchElementException> { deque.peekFirst() }
    }

    @Test
    fun `test peekLast of empty deque`() {
        assertThrows<NoSuchElementException> { deque.peekLast() }
    }

    @Test
    fun `test addFirst addLast sequence`() {
        // deque: 1 -> 2 -> 3 -> 4 (front -> rear)
        deque.addFirst(1)
        deque.addFirst(2)
        deque.addLast(3)
        deque.addLast(4)

        assertEquals(deque.size(), 4)
        assertEquals(deque.peekFirst(), 2)
        assertEquals(deque.peekLast(), 4)
        assertFalse(deque.isEmpty())
    }

    @Test
    fun `test removeFirst removeLast sequence`() {
        // deque: 1 -> 2 -> 3 -> 4 (front -> rear)
        deque.addFirst(1)
        deque.addFirst(2)
        deque.addLast(3)
        deque.addLast(4)

        assertEquals(2, deque.removeFirst())
        assertEquals(4, deque.removeLast())
        assertEquals(1, deque.peekFirst())
        assertEquals(3, deque.peekLast())
        assertEquals(deque.size(), 2)
    }

    @Test
    fun `test interleaved add and remove at both ends`() {
        // deque: 1 -> 2 -> 3 (front -> rear)
        deque.addLast(1)
        deque.addLast(2)
        deque.addLast(3)

        assertEquals(3, deque.removeLast())   // deque: 1 -> 2
        assertEquals(1, deque.removeFirst()) // deque: 2

        deque.addFirst(5)                    // deque: 5 -> 2
        deque.addLast(7)                    // deque: 5 -> 2 -> 7

        assertEquals(5, deque.peekFirst())
        assertEquals(7, deque.peekLast())
        assertEquals(deque.size(), 3)
    }

    @Test
    fun `test drain to empty and reuse`() {
        deque.addFirst(9)
        assertEquals(9, deque.removeLast())
        assertTrue(deque.isEmpty())

        // reusable after being emptied
        deque.addLast(7)
        assertEquals(7, deque.peekFirst())
        assertEquals(7, deque.peekLast())
        assertEquals(7, deque.removeFirst())
        assertTrue(deque.isEmpty())
    }

    @Test
    fun `test growth beyond initial capacity`() {
        // the circular array must resize (grow) transparently
        for (index in 0 until 100) {
            deque.addLast(index)
        }
        assertEquals(100, deque.size())

        for (index in 0 until 100) {
            assertEquals(index, deque.removeFirst())
        }
        assertTrue(deque.isEmpty())
    }
}