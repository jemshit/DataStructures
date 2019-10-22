package queue

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class QueueUsingDoublyLinkedListTest {

    private lateinit var queue: QueueUsingDoublyLinkedList<Int>

    @BeforeEach
    fun beforeEach() {
        queue = QueueUsingDoublyLinkedList<Int>()
    }

    @Test
    fun `test empty Queue`() {
        assertTrue(queue.isEmpty())
        assertEquals(queue.size(), 0)
    }

    @Test
    fun `test dequeue on empty`() {
        assertThrows<Exception> {
            queue.dequeue()
        }
    }

    @Test
    fun `test peek on empty`() {
        assertThrows<Exception> {
            queue.peek()
        }
    }

    @Test
    fun `test enqueue`() {
        queue.enqueue(2)
        assertEquals(queue.size(), 1)
    }

    @Test
    fun `test peek`() {
        queue.enqueue(2)
        assertTrue(queue.peek() == 2)
        assertEquals(queue.size(), 1)
    }

    @Test
    fun `test dequeue`() {
        queue.enqueue(2)
        assertTrue(queue.dequeue() == 2)
        assertEquals(queue.size(), 0)
    }

    @Test
    fun `test exhaustively`() {
        assertTrue(queue.isEmpty())
        queue.enqueue(1)
        assertTrue(!queue.isEmpty())
        queue.enqueue(2)
        assertEquals(queue.size(), 2)
        assertTrue(queue.peek() == 1)
        assertEquals(queue.size(), 2)
        assertTrue(queue.dequeue() == 1)
        assertEquals(queue.size(), 1)
        assertTrue(queue.peek() == 2)
        assertEquals(queue.size(), 1)
        assertTrue(queue.dequeue() == 2)
        assertEquals(queue.size(), 0)
        assertTrue(queue.isEmpty())
    }

}