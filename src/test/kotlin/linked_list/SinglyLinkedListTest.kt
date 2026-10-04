package linked_list

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class SinglyLinkedListTest {

    private val LOOPS = 10000
    private val TEST_SZ = 40
    private val NUM_NULLS = TEST_SZ / 5
    private val MAX_RAND_NUM = 250

    private lateinit var linkedList: SinglyLinkedList<Int>
    private lateinit var stringLinkedList: SinglyLinkedList<String>


    @BeforeEach
    fun beforeEach() {
        linkedList = SinglyLinkedList<Int>()
        stringLinkedList = SinglyLinkedList<String>()
    }

    @Test
    fun `test empty linkedList`() {
        assertTrue(linkedList.isEmpty())
    }

    @Test
    fun `test removeFirst of empty linkedList`() {
        assertThrows<NoSuchElementException> {
            linkedList.removeFirst()
        }
    }

    @Test
    fun `test removeLast of empty linkedList`() {
        assertThrows<NoSuchElementException> {
            linkedList.removeLast()
        }
    }

    @Test
    fun `test peekFirst of empty linkedList`() {
        assertThrows<NoSuchElementException> {
            linkedList.peekFirst()
        }
    }

    @Test
    fun `test peekLast of empty linkedList`() {
        assertThrows<NoSuchElementException> {
            linkedList.peekLast()
        }
    }

    @Test
    fun `test addFirst`() {
        linkedList.addFirst(3)
        assertEquals(linkedList.size, 1)
        linkedList.addFirst(5)
        assertEquals(linkedList.size, 2)
    }

    @Test
    fun `test addLast`() {
        linkedList.addLast(3)
        assertEquals(linkedList.size, 1)
        linkedList.addLast(5)
        assertEquals(linkedList.size, 2)
    }

    @Test
    fun `test removeFirst`() {
        linkedList.addFirst(3)
        assertTrue(linkedList.removeFirst() == 3)
        assertTrue(linkedList.isEmpty())
    }

    @Test
    fun `test removeLast`() {
        linkedList.addLast(4)
        assertTrue(linkedList.removeLast() == 4)
        assertTrue(linkedList.isEmpty())
    }

    @Test
    fun `test peekFirst`() {
        linkedList.addFirst(4)
        assertTrue(linkedList.peekFirst() == 4)
        assertEquals(linkedList.size, 1)
    }

    @Test
    fun `test peekLast`() {
        linkedList.addLast(4)
        assertTrue(linkedList.peekLast() == 4)
        assertEquals(linkedList.size, 1)
    }

    @Test
    fun `test peeking`() {
        // 5
        linkedList.addFirst(5)
        assertTrue(linkedList.peekFirst() == 5)
        assertTrue(linkedList.peekLast() == 5)

        // 6 - 5
        linkedList.addFirst(6)
        assertTrue(linkedList.peekFirst() == 6)
        assertTrue(linkedList.peekLast() == 5)

        // 7 - 6 - 5
        linkedList.addFirst(7)
        assertTrue(linkedList.peekFirst() == 7)
        assertTrue(linkedList.peekLast() == 5)

        // 7 - 6 - 5 - 8
        linkedList.addLast(8)
        assertTrue(linkedList.peekFirst() == 7)
        assertTrue(linkedList.peekLast() == 8)

        // 7 - 6 - 5
        linkedList.removeLast()
        assertTrue(linkedList.peekFirst() == 7)
        assertTrue(linkedList.peekLast() == 5)

        // 7 - 6
        linkedList.removeLast()
        assertTrue(linkedList.peekFirst() == 7)
        assertTrue(linkedList.peekLast() == 6)

        // 6
        linkedList.removeFirst()
        assertTrue(linkedList.peekFirst() == 6)
        assertTrue(linkedList.peekLast() == 6)
    }

    @Test
    fun `test removing`() {
        stringLinkedList.addLast("a")
        stringLinkedList.addLast("b")
        stringLinkedList.addLast("c")
        stringLinkedList.addLast("d")
        stringLinkedList.addLast("e")
        stringLinkedList.addLast("f")
        stringLinkedList.remove("b")
        stringLinkedList.remove("a")
        stringLinkedList.remove("d")
        stringLinkedList.remove("e")
        stringLinkedList.remove("c")
        stringLinkedList.remove("f")
        assertEquals(0, stringLinkedList.size)
    }

    @Test
    fun `test removeAt`() {
        linkedList.addLast(1)
        linkedList.addLast(2)
        linkedList.addLast(3)
        linkedList.addLast(4)
        linkedList.removeAt(0)
        linkedList.removeAt(2)
        assertTrue(linkedList.peekFirst() == 2)
        assertTrue(linkedList.peekLast() == 3)
        linkedList.removeAt(1)
        linkedList.removeAt(0)
        assertEquals(linkedList.size, 0)
    }

    @Test
    fun `test clear`() {
        linkedList.addLast(22)
        linkedList.addLast(33)
        linkedList.addLast(44)
        assertEquals(linkedList.size, 3)
        linkedList.clear()
        assertEquals(linkedList.size, 0)
        linkedList.addLast(22)
        linkedList.addLast(33)
        linkedList.addLast(44)
        assertEquals(linkedList.size, 3)
        linkedList.clear()
        assertEquals(linkedList.size, 0)
    }

    @Test
    fun `test indexOf`() {
        linkedList.addLast(22)
        linkedList.addLast(33)
        linkedList.addLast(44)
        assertEquals(linkedList.size, 3)
        assertEquals(0, linkedList.indexOf(22))
        assertEquals(1, linkedList.indexOf(33))
        assertEquals(2, linkedList.indexOf(44))
        assertEquals(-1, linkedList.indexOf(54))
        linkedList.removeLast()
        assertEquals(linkedList.size, 2)
        assertEquals(0, linkedList.indexOf(22))
        assertEquals(1, linkedList.indexOf(33))
        assertEquals(-1, linkedList.indexOf(44))
    }

    @Test
    fun `test add-remove middle`() {
        linkedList.add(0, 22)
        linkedList.add(1, 33)
        linkedList.addLast(55)
        linkedList.add(2, 44)
        assertEquals(linkedList.size, 4)
        assertEquals(0, linkedList.indexOf(22))
        assertEquals(1, linkedList.indexOf(33))
        assertEquals(2, linkedList.indexOf(44))
        assertEquals(3, linkedList.indexOf(55))

        linkedList.remove(55)
        linkedList.removeAt(2)
        assertEquals(linkedList.size, 2)
        assertEquals(0, linkedList.indexOf(22))
        assertEquals(1, linkedList.indexOf(33))
    }

    // ---------- regression tests: tail / empty-list invariant ----------

    @Test
    fun `test peekLast throws after removeFirst empties the list`() {
        linkedList.addFirst(7)
        linkedList.removeFirst()

        assertTrue(linkedList.isEmpty())
        assertThrows<NoSuchElementException> { linkedList.peekLast() }
        assertThrows<NoSuchElementException> { linkedList.peekFirst() }
    }

    @Test
    fun `test peekLast throws after removeLast empties the list`() {
        linkedList.addLast(1)
        linkedList.addLast(2)
        linkedList.removeLast()
        linkedList.removeLast()

        assertThrows<NoSuchElementException> { linkedList.peekLast() }
        assertThrows<NoSuchElementException> { linkedList.peekFirst() }
    }

    @Test
    fun `test peekLast throws after removeAt empties the list`() {
        linkedList.addLast(1)
        linkedList.removeAt(0)

        assertThrows<NoSuchElementException> { linkedList.peekLast() }
    }

    @Test
    fun `test peekLast throws after clear`() {
        linkedList.addLast(1)
        linkedList.addLast(2)
        linkedList.clear()

        assertThrows<NoSuchElementException> { linkedList.peekLast() }
        assertThrows<NoSuchElementException> { linkedList.peekFirst() }
    }

    @Test
    fun `test list is reusable after being emptied`() {
        linkedList.addLast(1)
        linkedList.removeFirst()

        linkedList.addLast(2)
        linkedList.addLast(3)
        assertEquals(listOf(2, 3), linkedList.toList())
        assertEquals(3, linkedList.peekLast())

        linkedList.removeLast()
        assertEquals(2, linkedList.peekLast())
        assertEquals(2, linkedList.peekFirst())

        linkedList.clear()
        linkedList.addFirst(9)
        assertEquals(9, linkedList.peekLast())
        assertEquals(listOf(9), linkedList.toList())
    }

    @Test
    fun `test removing tail updates tail`() {
        linkedList.addLast(1)
        linkedList.addLast(2)
        linkedList.addLast(3)

        assertTrue(linkedList.remove(3))
        assertEquals(2, linkedList.peekLast())

        linkedList.addLast(4)
        assertEquals(listOf(1, 2, 4), linkedList.toList())
    }

    // ---------- regression tests: remove(item) ----------

    @Test
    fun `test remove removes first occurrence even if tail equals item`() {
        listOf(2, 1, 3, 1).forEach { linkedList.addLast(it) }

        assertTrue(linkedList.remove(1))

        assertEquals(listOf(2, 3, 1), linkedList.toList())
        assertEquals(1, linkedList.peekLast())
        assertEquals(3, linkedList.size)
    }

    @Test
    fun `test remove removes only one of many duplicates`() {
        listOf(5, 5, 5).forEach { linkedList.addLast(it) }

        assertTrue(linkedList.remove(5))

        assertEquals(listOf(5, 5), linkedList.toList())
    }

    @Test
    fun `test remove returns false for missing item and for empty list`() {
        assertFalse(linkedList.remove(5))

        linkedList.addLast(1)
        linkedList.addLast(2)
        assertFalse(linkedList.remove(5))
        assertEquals(listOf(1, 2), linkedList.toList())
    }

    // ---------- get / set / contains / toString ----------

    @Test
    fun `test get and set`() {
        listOf(10, 20, 30).forEach { linkedList.addLast(it) }

        assertEquals(10, linkedList[0])
        assertEquals(20, linkedList[1])
        assertEquals(30, linkedList[2])

        assertEquals(20, linkedList.set(1, 25))
        assertEquals(25, linkedList[1])

        linkedList[2] = 35
        assertEquals(35, linkedList.peekLast())
        assertEquals(3, linkedList.size)
    }

    @Test
    fun `test contains`() {
        listOf(10, 20, 30).forEach { linkedList.addLast(it) }

        assertTrue(20 in linkedList)
        assertFalse(40 in linkedList)
        assertFalse(1 in SinglyLinkedList<Int>())
    }

    @Test
    fun `test toString`() {
        assertEquals("[]", linkedList.toString())

        listOf(1, 2, 3).forEach { linkedList.addLast(it) }
        assertEquals("[1, 2, 3]", linkedList.toString())
    }

    // ---------- index validation ----------

    @Test
    fun `test invalid indexes throw IndexOutOfBoundsException with a message`() {
        linkedList.addLast(1)

        val exceptions = listOf(
            assertThrows<IndexOutOfBoundsException> { linkedList.add(-1, 0) },
            assertThrows<IndexOutOfBoundsException> { linkedList.add(2, 0) },
            assertThrows<IndexOutOfBoundsException> { linkedList.removeAt(-1) },
            assertThrows<IndexOutOfBoundsException> { linkedList.removeAt(1) },
            assertThrows<IndexOutOfBoundsException> { linkedList[-1] },
            assertThrows<IndexOutOfBoundsException> { linkedList[1] },
            assertThrows<IndexOutOfBoundsException> { linkedList[1] = 5 },
        )

        exceptions.forEach { assertTrue(it.message!!.contains("Size: 1"), it.message) }
        assertEquals(listOf(1), linkedList.toList())
    }

    @Test
    fun `test add at size appends and add at 0 prepends`() {
        linkedList.add(0, 2)    // empty list: position 0 == size
        linkedList.add(1, 3)    // append
        linkedList.add(0, 1)    // prepend
        linkedList.add(2, 99)   // middle

        assertEquals(listOf(1, 2, 99, 3), linkedList.toList())
        assertEquals(3, linkedList.peekLast())
    }

    // ---------- iterator ----------

    @Test
    fun `test iterator throws NoSuchElementException when exhausted`() {
        assertThrows<NoSuchElementException> { linkedList.iterator().next() }

        linkedList.addLast(1)
        val iterator = linkedList.iterator()
        assertEquals(1, iterator.next())
        assertFalse(iterator.hasNext())
        assertThrows<NoSuchElementException> { iterator.next() }
    }

    @Test
    fun `test iterator fails fast on structural modification`() {
        linkedList.addLast(1)
        linkedList.addLast(2)

        val iterator = linkedList.iterator()
        assertEquals(1, iterator.next())

        linkedList.addLast(3) // structural change invalidates the iterator

        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `test set does not invalidate iterator`() {
        linkedList.addLast(1)
        linkedList.addLast(2)

        val iterator = linkedList.iterator()
        assertEquals(1, iterator.next())

        linkedList[1] = 99 // data swap, not a structural change

        // still valid (no exception), but it sees the new data because it points at the same node
        assertEquals(99, iterator.next())
        assertFalse(iterator.hasNext())
    }

    // ---------- randomized ----------

    @Test
    fun `test randomized removing`() {
        val originalList = LinkedList<Int>()
        for (loops in 0 until LOOPS) {
            linkedList.clear()
            originalList.clear()

            val randNums = genRandList(TEST_SZ)
            for (value in randNums) {
                originalList.addLast(value)
                linkedList.addLast(value)
            }

            Collections.shuffle(randNums)

            for (randomValue in randNums) {
                assertEquals(originalList.remove(randomValue), linkedList.remove(randomValue))
                assertEquals(originalList.size, linkedList.size)
                assertEquals(originalList.toList(), linkedList.toList())
                assertEquals(originalList.peekLastOrNull(), linkedList.peekLastOrNull())
            }

            linkedList.clear()
            originalList.clear()

            for (value in randNums) {
                originalList.addLast(value)
                linkedList.addLast(value)
            }

            // Try removing elements whether or not they exist
            for (index in randNums.indices) {
                val randomValue = (MAX_RAND_NUM * Math.random()).toInt()
                assertEquals(originalList.remove(randomValue), linkedList.remove(randomValue))
                assertEquals(originalList.size, linkedList.size)
                assertEquals(originalList.toList(), linkedList.toList())
                assertEquals(originalList.peekLastOrNull(), linkedList.peekLastOrNull())
            }
        }
    }

    @Test
    fun `test randomized removeAt`() {
        val originalList = LinkedList<Int>()

        for (loops in 0 until LOOPS) {
            linkedList.clear()
            originalList.clear()

            val randNums = genRandList(TEST_SZ)

            for (value in randNums) {
                originalList.add(value)
                linkedList.addLast(value)
            }

            for (index in randNums.indices) {
                val randomIndex = (linkedList.size * Math.random()).toInt()

                val num1 = originalList.removeAt(randomIndex)
                val num2 = linkedList.removeAt(randomIndex)
                assertEquals(num1, num2)
                assertEquals(originalList.size, linkedList.size)
                assertEquals(originalList.toList(), linkedList.toList())
                assertEquals(originalList.peekLastOrNull(), linkedList.peekLastOrNull())
            }
        }
    }

    @Test
    fun `test randomized indexOf`() {
        val originalList = LinkedList<Int>()

        for (loops in 0 until LOOPS) {
            originalList.clear()
            linkedList.clear()

            val randNums = genUniqueRandList(TEST_SZ)

            for (value in randNums) {
                originalList.add(value)
                linkedList.addLast(value)
            }

            Collections.shuffle(randNums)

            for (index in randNums.indices) {

                val elem = randNums[index]
                val index1 = originalList.indexOf(elem)
                val index2 = linkedList.indexOf(elem)

                assertEquals(index1, index2)
                assertEquals(originalList.size, linkedList.size)

                val iter1 = originalList.iterator()
                val iter2 = linkedList.iterator()
                while (iter1.hasNext()) assertEquals(iter1.next(), iter2.next())
            }
        }
    }

    @Test
    fun `test randomized add at index`() {
        val originalList = LinkedList<Int>()

        for (loops in 0 until LOOPS) {
            linkedList.clear()
            originalList.clear()

            for (value in genRandList(TEST_SZ)) {
                val index = ((linkedList.size + 1) * Math.random()).toInt()
                originalList.add(index, value)
                linkedList.add(index, value)

                assertEquals(originalList.toList(), linkedList.toList())
                assertEquals(originalList.peekLastOrNull(), linkedList.peekLastOrNull())
            }
        }
    }

    private fun LinkedList<Int>.peekLastOrNull(): Int? = peekLast()
    private fun SinglyLinkedList<Int>.peekLastOrNull(): Int? = if (isEmpty()) null else peekLast()

    // Generate a list of random numbers
    private fun genRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (i in 0 until size)
            list.add((Math.random() * MAX_RAND_NUM).toInt())
        for (i in 0 until NUM_NULLS)
            list.add(-1)
        list.shuffle()
        return list
    }

    // Generate a list of unique random numbers
    private fun genUniqueRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (i in 0 until size)
            list.add(i)
        for (i in 0 until NUM_NULLS)
            list.add(-1)
        list.shuffle()
        return list
    }

}

internal class SinglyLinkedListReverseTest {

    private fun listOf(vararg values: Int): SinglyLinkedList<Int> {
        val list = SinglyLinkedList<Int>()
        values.forEach { list.addLast(it) }
        return list
    }

    @Test
    fun `test reverse 1`() {
        val linkedList = listOf(4, 5, 1, 2)

        linkedList.reverse()

        assertEquals(kotlin.collections.listOf(2, 1, 5, 4), linkedList.toList())
    }

    @Test
    fun `test reverse 2`() {
        val linkedList = listOf(85, 15, 4, 20)

        linkedList.reverse()

        assertEquals(kotlin.collections.listOf(20, 4, 15, 85), linkedList.toList())
    }

    @Test
    fun `test reverse empty list`() {
        val linkedList = SinglyLinkedList<Int>()

        linkedList.reverse()

        assertTrue(linkedList.isEmpty())
        assertThrows<NoSuchElementException> { linkedList.peekLast() }
    }

    @Test
    fun `test reverse single element`() {
        val linkedList = listOf(1)

        linkedList.reverse()

        assertEquals(kotlin.collections.listOf(1), linkedList.toList())
        assertEquals(1, linkedList.peekFirst())
        assertEquals(1, linkedList.peekLast())
    }

    @Test
    fun `test reverse two elements`() {
        val linkedList = listOf(1, 2)

        linkedList.reverse()

        assertEquals(kotlin.collections.listOf(2, 1), linkedList.toList())
        assertEquals(2, linkedList.size)
        assertEquals(2, linkedList.peekFirst())
        assertEquals(1, linkedList.peekLast())
    }

    @Test
    fun `test reverse keeps head tail and size consistent`() {
        val linkedList = listOf(1, 2, 3)

        linkedList.reverse()
        assertEquals(3, linkedList.peekFirst())
        assertEquals(1, linkedList.peekLast())
        assertEquals(3, linkedList.size)

        // tail must still be the real last node
        linkedList.addLast(0)
        linkedList.addFirst(9)
        assertEquals(kotlin.collections.listOf(9, 3, 2, 1, 0), linkedList.toList())

        assertEquals(0, linkedList.removeLast())
        assertEquals(1, linkedList.peekLast())
    }

    @Test
    fun `test reverse twice restores the original order`() {
        for (size in 0..6) {
            val values = IntArray(size) { it }
            val linkedList = listOf(*values)

            linkedList.reverse()
            linkedList.reverse()

            assertEquals(values.toList(), linkedList.toList(), "size=$size")
        }
    }
}
