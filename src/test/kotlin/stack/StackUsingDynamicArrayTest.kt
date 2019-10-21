package stack

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import stack.StackUsingDynamicArray

internal class StackUsingDynamicArrayTest {

    private lateinit var stack: StackUsingDynamicArray<Int>

    @BeforeEach
    fun beforeEach() {
        stack = StackUsingDynamicArray<Int>()
    }

    @Test
    fun `test empty Stack`() {
        assertTrue(stack.isEmpty())
        assertEquals(stack.size, 0)
    }

    @Test
    fun `test pop on empty`() {
        org.junit.jupiter.api.assertThrows<Exception> {
            stack.pop()
        }
    }

    @Test
    fun `test peek on empty`() {
        org.junit.jupiter.api.assertThrows<Exception> {
            stack.peek()
        }
    }

    @Test
    fun `test push`() {
        stack.push(2)
        assertEquals(stack.size, 1)
    }

    @Test
    fun `test peek`() {
        stack.push(2)
        assertTrue(stack.peek() == 2)
        assertEquals(stack.size, 1)
    }

    @Test
    fun `test pop`() {
        stack.push(2)
        assertTrue(stack.pop() == 2)
        assertEquals(stack.size, 0)
    }

    @Test
    fun `test exhaustively`() {
        assertTrue(stack.isEmpty())
        stack.push(1)
        assertTrue(!stack.isEmpty())
        stack.push(2)
        assertEquals(stack.size, 2)
        assertTrue(stack.peek() == 2)
        assertEquals(stack.size, 2)
        assertTrue(stack.pop() == 2)
        assertEquals(stack.size, 1)
        assertTrue(stack.peek() == 1)
        assertEquals(stack.size, 1)
        assertTrue(stack.pop() == 1)
        assertEquals(stack.size, 0)
        assertTrue(stack.isEmpty())
    }
}

