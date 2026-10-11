package queue

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class QueueUsingStaticArrayTest {
    private lateinit var queue: QueueUsingStaticArray<Int>

    @BeforeEach
    fun beforeEach() {
        queue = QueueUsingStaticArray<Int>()
    }

    @Test
    fun `test empty Queue`() {
        assertTrue(queue.isEmpty())
        assertEquals(queue.size(), 0)
    }

    @Test
    fun `test dequeue on empty`() {
        org.junit.jupiter.api.assertThrows<Exception> {
            queue.dequeue()
        }
    }

    @Test
    fun `test peek on empty`() {
        org.junit.jupiter.api.assertThrows<Exception> {
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

    @Test
    fun `test overflow on full queue`() {
        val smallQueue = QueueUsingStaticArray<Int>(2)
        smallQueue.enqueue(1)
        smallQueue.enqueue(2)
        assertTrue(smallQueue.isFull())

        org.junit.jupiter.api.assertThrows<IllegalStateException> {
            smallQueue.enqueue(3)
        }
    }

    @Test
    fun `test circular wrap around`() {
        val q = QueueUsingStaticArray<Int>(3)
        q.enqueue(1)
        q.enqueue(2)
        assertEquals(1, q.dequeue())
        q.enqueue(3)
        q.enqueue(4) // wraps around circular array

        assertEquals(2, q.dequeue())
        assertEquals(3, q.dequeue())
        assertEquals(4, q.dequeue())
        assertTrue(q.isEmpty())
    }

    @Test
    fun `test invalid capacity throws`() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            QueueUsingStaticArray<Int>(0)
        }
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            QueueUsingStaticArray<Int>(-5)
        }
    }
}