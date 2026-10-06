package array

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.ArrayDeque
import kotlin.random.Random

internal class CircularDynamicArrayTest {

    @Test
    fun `default array becomes full when its initial capacity is reached`() {
        val array = CircularDynamicArray<Int>()

        assertTrue(array.isEmpty())
        assertEquals(0, array.size)
        assertFalse(array.isFull())

        array.addLast(42)

        assertFalse(array.isEmpty())
        assertFalse(array.isFull())
        assertEquals(1, array.size)

        array.addLast(43)

        assertTrue(array.isFull())
        assertEquals(2, array.size)
    }

    @Test
    fun `custom-capacity array is not full until all slots are used`() {
        val array = CircularDynamicArray<Int>(3)

        assertFalse(array.isFull())
        array.addLast(1)
        assertFalse(array.isFull())
        array.addLast(2)
        assertFalse(array.isFull())
        array.addLast(3)
        assertTrue(array.isFull())
    }

    @Test
    fun `initial capacity must be at least the default capacity`() {
        assertThrows<IllegalArgumentException> { CircularDynamicArray<Int>(1) }
        assertThrows<IllegalArgumentException> { CircularDynamicArray<Int>(0) }
        assertThrows<IllegalArgumentException> { CircularDynamicArray<Int>(-1) }
    }

    @Test
    fun `empty array rejects reads and removals from both ends`() {
        val array = CircularDynamicArray<Int>()

        assertThrows<NoSuchElementException> { array.getFirst() }
        assertThrows<NoSuchElementException> { array.getLast() }
        assertThrows<NoSuchElementException> { array.removeFirst() }
        assertThrows<NoSuchElementException> { array.removeLast() }
    }

    @Test
    fun `single element is both first and last`() {
        val array = CircularDynamicArray<String>()

        array.addFirst("only")

        assertEquals("only", array.getFirst())
        assertEquals("only", array.getLast())
        assertEquals(1, array.size)
    }

    @Test
    fun `addLast and removeFirst preserve FIFO order`() {
        val array = CircularDynamicArray<Int>(2)
        (1..100).forEach(array::addLast)

        assertEquals(100, array.size)
        (1..100).forEach { expected ->
            assertEquals(expected, array.removeFirst())
        }
        assertTrue(array.isEmpty())
    }

    @Test
    fun `addFirst and removeLast preserve FIFO order from the opposite direction`() {
        val array = CircularDynamicArray<Int>(2)
        (1..100).forEach(array::addFirst)

        assertEquals(100, array.size)
        (1..100).forEach { expected ->
            assertEquals(expected, array.removeLast())
        }
        assertTrue(array.isEmpty())
    }

    @Test
    fun `addLast and removeLast preserve LIFO order`() {
        val array = CircularDynamicArray<Int>(4)
        (1..100).forEach(array::addLast)

        (100 downTo 1).forEach { expected ->
            assertEquals(expected, array.removeLast())
        }
    }

    @Test
    fun `addFirst and removeFirst preserve LIFO order`() {
        val array = CircularDynamicArray<Int>(4)
        (1..100).forEach(array::addFirst)

        (100 downTo 1).forEach { expected ->
            assertEquals(expected, array.removeFirst())
        }
    }

    @Test
    fun `mixed additions establish the expected first and last elements`() {
        val array = CircularDynamicArray<Int>(2)

        array.addLast(20)
        array.addFirst(10)
        array.addLast(30)
        array.addFirst(0)

        assertEquals(0, array.getFirst())
        assertEquals(30, array.getLast())
        assertEquals(4, array.size)
        assertEquals(listOf(0, 10, 20, 30), drainFromFront(array))
    }

    @Test
    fun `peek operations do not remove elements`() {
        val array = CircularDynamicArray<Int>()
        array.addLast(10)
        array.addLast(20)

        repeat(3) {
            assertEquals(10, array.getFirst())
            assertEquals(20, array.getLast())
            assertEquals(2, array.size)
        }
    }

    @Test
    fun `size follows every kind of mutation`() {
        val array = CircularDynamicArray<Int>(4)

        array.addFirst(2)
        assertEquals(1, array.size)
        array.addLast(3)
        assertEquals(2, array.size)
        array.addFirst(1)
        assertEquals(3, array.size)
        array.removeLast()
        assertEquals(2, array.size)
        array.removeFirst()
        assertEquals(1, array.size)
        array.removeLast()
        assertEquals(0, array.size)
    }

    @Test
    fun `indices wrap around without changing logical order`() {
        val array = CircularDynamicArray<Int>(5)
        (1..5).forEach(array::addLast)

        assertEquals(1, array.removeFirst())
        assertEquals(2, array.removeFirst())
        array.addLast(6)
        array.addLast(7)

        assertTrue(array.isFull())
        assertEquals(listOf(3, 4, 5, 6, 7), drainFromFront(array))
    }

    @Test
    fun `growth preserves order when logical contents wrap around`() {
        val array = CircularDynamicArray<Int>(4)
        (1..4).forEach(array::addLast)
        assertEquals(1, array.removeFirst())
        assertEquals(2, array.removeFirst())
        array.addLast(5)
        array.addLast(6)

        // This addition forces growth while the logical interval crosses index zero.
        array.addLast(7)

        assertEquals(listOf(3, 4, 5, 6, 7), drainFromFront(array))
    }

    @Test
    fun `growth caused by addFirst preserves order`() {
        val array = CircularDynamicArray<Int>(2)
        array.addLast(2)
        array.addLast(3)

        array.addFirst(1)

        assertEquals(listOf(1, 2, 3), drainFromFront(array))
    }

    @Test
    fun `removing to shrink threshold preserves remaining wrapped elements`() {
        val array = CircularDynamicArray<Int>(8)
        (1..8).forEach(array::addLast)
        repeat(4) { array.removeFirst() }
        (9..12).forEach(array::addLast)

        repeat(6) { array.removeFirst() }

        assertEquals(2, array.size)
        assertEquals(11, array.getFirst())
        assertEquals(12, array.getLast())
        assertEquals(listOf(11, 12), drainFromFront(array))
    }

    @Test
    fun `array remains reusable after becoming empty`() {
        val array = CircularDynamicArray<String>(2)
        array.addLast("old-first")
        array.addLast("old-last")
        assertEquals("old-first", array.removeFirst())
        assertEquals("old-last", array.removeLast())

        array.addFirst("new-middle")
        array.addFirst("new-first")
        array.addLast("new-last")

        assertEquals(listOf("new-first", "new-middle", "new-last"), drainFromFront(array))
    }

    @Test
    fun `stored values retain generic type and object identity`() {
        data class Item(val id: Int)

        val first = Item(1)
        val last = Item(2)
        val array = CircularDynamicArray<Item>()
        array.addLast(first)
        array.addLast(last)

        assertSame(first, array.getFirst())
        assertSame(last, array.getLast())
        assertSame(last, array.removeLast())
        assertSame(first, array.removeFirst())
    }

    @Test
    fun `deterministic mixed-operation stress test matches ArrayDeque`() {
        val random = Random(8675309)
        val expected = ArrayDeque<Int>()
        val actual = CircularDynamicArray<Int>(3)

        repeat(10_000) {
            when (random.nextInt(6)) {
                0 -> {
                    val value = random.nextInt()
                    expected.addFirst(value)
                    actual.addFirst(value)
                }
                1 -> {
                    val value = random.nextInt()
                    expected.addLast(value)
                    actual.addLast(value)
                }
                2 -> if (expected.isNotEmpty()) {
                    assertEquals(expected.removeFirst(), actual.removeFirst())
                }
                3 -> if (expected.isNotEmpty()) {
                    assertEquals(expected.removeLast(), actual.removeLast())
                }
                4 -> if (expected.isNotEmpty()) {
                    assertEquals(expected.first, actual.getFirst())
                }
                5 -> if (expected.isNotEmpty()) {
                    assertEquals(expected.last, actual.getLast())
                }
            }

            assertEquals(expected.size, actual.size)
            assertEquals(expected.isEmpty(), actual.isEmpty())
            if (expected.isNotEmpty()) {
                assertEquals(expected.first, actual.getFirst())
                assertEquals(expected.last, actual.getLast())
            }
        }

        assertEquals(expected.toList(), drainFromFront(actual))
    }

    private fun <T : Any> drainFromFront(array: CircularDynamicArray<T>): List<T> = buildList {
        while (!array.isEmpty()) {
            add(array.removeFirst())
        }
    }
}
