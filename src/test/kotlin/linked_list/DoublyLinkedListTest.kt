package linked_list

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class DoublyLinkedListTest{

    private val LOOPS = 10000
    private val TEST_SZ = 40
    private val NUM_NULLS = TEST_SZ / 5
    private val MAX_RAND_NUM = 250

    private lateinit var linkedList: DoublyLinkedList<Int>
    private lateinit var stringLinkedList: DoublyLinkedList<String>


    @BeforeEach
    fun beforeEach() {
        linkedList = DoublyLinkedList<Int>()
        stringLinkedList = DoublyLinkedList<String>()
    }

    @Test
    fun `test empty linkedList`() {
        assertTrue(linkedList.isEmpty())
    }

    @Test
    fun `test removeFirst of empty linkedList`() {
        assertThrows<Exception> {
            linkedList.removeFirst()
        }
    }

    @Test
    fun `test removeLast of empty linkedList`() {
        assertThrows<Exception> {
            linkedList.removeLast()
        }
    }

    @Test
    fun `test peekFirst of empty linkedList`() {
        assertThrows<Exception> {
            linkedList.peekFirst()
        }
    }

    @Test
    fun `test peekLast of empty linkedList`() {
        assertThrows<Exception> {
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

    @Test
    fun `test randomized removing`() {
        val otherSinglyLinkedList = SinglyLinkedList<Int>()
        for (loops in 0 until LOOPS) {
            linkedList.clear()
            otherSinglyLinkedList.clear()

            val randNums = genRandList(TEST_SZ)
            for (value in randNums) {
                otherSinglyLinkedList.addLast(value)
                linkedList.addLast(value)
            }

            Collections.shuffle(randNums)

            for (randomValue in randNums) {
                assertEquals(otherSinglyLinkedList.remove(randomValue), linkedList.remove(randomValue))
                assertEquals(otherSinglyLinkedList.size, linkedList.size)

                var iter1 = otherSinglyLinkedList.iterator()
                var iter2 = linkedList.iterator()
                while (iter1.hasNext())
                    assertEquals(iter1.next(), iter2.next())

                iter1 = otherSinglyLinkedList.iterator()
                iter2 = linkedList.iterator()
                while (iter1.hasNext())
                    assertEquals(iter1.next(), iter2.next())
            }

            linkedList.clear()
            otherSinglyLinkedList.clear()

            for (value in randNums) {
                otherSinglyLinkedList.addLast(value)
                linkedList.addLast(value)
            }

            // Try removing elements whether or not they exist
            for (index in randNums.indices) {
                val randomValue = (MAX_RAND_NUM * Math.random()).toInt()
                assertEquals(otherSinglyLinkedList.remove(randomValue), linkedList.remove(randomValue))
                assertEquals(otherSinglyLinkedList.size, linkedList.size)

                val iter1 = otherSinglyLinkedList.iterator()
                val iter2 = linkedList.iterator()
                while (iter1.hasNext())
                    assertEquals(iter1.next(), iter2.next())
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

                val iter1 = originalList.iterator()
                val iter2 = linkedList.iterator()
                while (iter1.hasNext())
                    assertEquals(iter1.next(), iter2.next())
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