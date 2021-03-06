package heap

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

internal class DaryMinHeapTest {

    private val loops = 1000
    private val maxSize = 100

    @Test
    fun `test empty`() {
        val heap = DaryMinHeap<Int>(4, 0)
        assertEquals(heap.size(), 0)
        assertTrue(heap.isEmpty())
        assertEquals(heap.poll(), null)
        assertEquals(heap.peek(), null)
    }

    @Test
    fun `test heap property`() {
        val heap = DaryMinHeap<Int>(3, 30)
        val intArray = arrayOf(3, 2, 5, 6, 7, 9, 4, 8, 1)

        // Try manually creating heap
        for (num in intArray)
            heap.add(num)
        for (index in 1..9)
            assertTrue(index == heap.poll())
    }

    @Test
    fun `test heapify`() {
        for (index in 1 until loops) {
            val array = genRandArray(index)
            val heap = DaryMinHeap<Int>(d = 2, initialCapacity = 50, unsortedItems = array)

            val priorityQueue = PriorityQueue<Int>(index)
            for (x in array)
                priorityQueue.add(x)

            while (!priorityQueue.isEmpty()) {
                assertEquals(heap.poll(), priorityQueue.poll())
            }
        }
    }

    @Test
    fun `test priority queue size param`() {
        for (index in 1 until loops) {
            val randomArray = genRandArray(index)

            val heap = DaryMinHeap<Int>(index, randomArray.size)
            val priorityQueue = PriorityQueue<Int>(index)

            for (item in randomArray) {
                priorityQueue.add(item)
                heap.add(item)
            }
            while (!priorityQueue.isEmpty())
                assertEquals(heap.poll(), priorityQueue.poll())
        }
    }

    @Test
    fun `test priority random operations`() {
        for (loop in 0 until loops) {
            var randomInt1 = Math.random()
            var randomInt2 = Math.random()
            if (randomInt2 < randomInt1) {
                val tmp = randomInt1
                randomInt1 = randomInt2
                randomInt2 = randomInt1
            }

            val randomArray = genRandArray(loops)
            val d = 2 + (Math.random() * 6).toInt()
            val heap = DaryMinHeap<Int>(d, loops)
            val priorityQueue = PriorityQueue<Int>(loops)

            for (loopIndex in 0 until loops) {
                val randomArray2 = randomArray[loopIndex]
                val randomInt3 = Math.random()
                if (randomInt3 in 0.0..randomInt1) {
                    heap.add(randomArray2)
                    priorityQueue.add(randomArray2)
                } else if (randomInt1 < randomInt3 && randomInt3 <= randomInt2) {
                    if (!priorityQueue.isEmpty()) assertEquals(heap.poll(), priorityQueue.poll())
                } else {
                    heap.clear()
                    priorityQueue.clear()
                }
            }

            if (priorityQueue.peek() == null)
                Assertions.assertNull(heap.peek())
            else
                assertEquals(priorityQueue.peek(), heap.peek()!!)
        }
    }

    @Test
    fun `test clear`() {
        val stringArray = arrayOf("aa", "bb", "cc", "dd", "ee")
        val heap = DaryMinHeap<String>(2, stringArray.size)
        for (str in stringArray)
            heap.add(str)
        heap.clear()
        assertEquals(heap.size(), 0)
        assertTrue(heap.isEmpty())
    }

    @Test
    fun `test containment`() {
        val stringArray = arrayOf("aa", "bb", "cc", "dd", "ee")
        val heap = DaryMinHeap<String>(d=2, unsortedItems = stringArray)
        heap.remove("aa")
        Assertions.assertFalse(heap.contains("aa"))
        heap.remove("bb")
        Assertions.assertFalse(heap.contains("bb"))
        heap.remove("cc")
        Assertions.assertFalse(heap.contains("cc"))
        heap.remove("dd")
        Assertions.assertFalse(heap.contains("dd"))
        heap.clear()
        Assertions.assertFalse(heap.contains("ee"))
    }

    @Test
    fun `test removing duplicates`() {
        val intArray = arrayOf(2, 7, 2, 11, 7, 13, 2)
        val heap = DaryMinHeap<Int>(3, intArray.size + 1)

        for (element in intArray)
            heap.add(element)
        assertTrue(heap.peek() == 2)
        heap.add(3)

        assertTrue(heap.poll() == 2)
        assertTrue(heap.poll() == 2)
        assertTrue(heap.poll() == 2)
        assertTrue(heap.poll() == 3)
        assertTrue(heap.poll() == 7)
        assertTrue(heap.poll() == 7)
        assertTrue(heap.poll() == 11)
        assertTrue(heap.poll() == 13)
    }

    @Test
    fun `test containment randomized`() {
        for (index in 0 until loops) {
            val randNums = genRandList(100)
            val priorityQueue = PriorityQueue<Int>()
            val heap = DaryMinHeap<Int>()
            for (randomNumIndex in randNums.indices) {
                heap.add(randNums[randomNumIndex])
                priorityQueue.add(randNums[randomNumIndex])
            }

            for (randomNumIndex in randNums.indices) {
                val randVal = randNums[randomNumIndex]
                assertEquals(heap.contains(randVal), priorityQueue.contains(randVal))
                heap.remove(randVal)
                priorityQueue.remove(randVal)
                assertEquals(heap.contains(randVal), priorityQueue.contains(randVal))
            }
        }
    }

    private fun sequentialRemoving(intArray: Array<Int>, removeOrder: Array<Int>) {
        assertEquals(intArray.size, removeOrder.size)
        val heap = DaryMinHeap(unsortedItems = intArray)
        val priorityQueue = PriorityQueue<Int>()
        for (value in intArray)
            priorityQueue.offer(value)

        for (index in removeOrder.indices) {
            val elem = removeOrder[index]

            assertTrue(heap.peek() == priorityQueue.peek())
            assertEquals(heap.remove(elem), priorityQueue.remove(elem))
            assertTrue(heap.size() == priorityQueue.size)
        }

        assertTrue(heap.isEmpty())
    }

    @Test
    fun `test removing`() {
        var intArray = arrayOf(1, 2, 3, 4, 5, 6, 7)
        var removeOrder = arrayOf(1, 3, 6, 4, 5, 7, 2)
        sequentialRemoving(intArray, removeOrder)

        intArray = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        removeOrder = arrayOf(7, 4, 6, 10, 2, 5, 11, 3, 1, 8, 9)
        sequentialRemoving(intArray, removeOrder)

        intArray = arrayOf(8, 1, 3, 3, 5, 3)
        removeOrder = arrayOf(3, 3, 5, 8, 1, 3)
        sequentialRemoving(intArray, removeOrder)

        intArray = arrayOf(7, 7, 3, 1, 1, 2)
        removeOrder = arrayOf(2, 7, 1, 3, 7, 1)
        sequentialRemoving(intArray, removeOrder)

        intArray = arrayOf(32, 66, 93, 42, 41, 91, 54, 64, 9, 35)
        removeOrder = arrayOf(64, 93, 54, 41, 35, 9, 66, 42, 32, 91)
        sequentialRemoving(intArray, removeOrder)
    }

    @Test
    fun `test removing duplicates 2`() {
        val intArray = arrayOf(2, 7, 2, 11, 7, 13, 2)
        val heap = DaryMinHeap(unsortedItems = intArray)

        assertTrue(heap.peek() == 2)
        heap.add(3)

        assertTrue(heap.poll() == 2)
        assertTrue(heap.poll() == 2)
        assertTrue(heap.poll() == 2)
        assertTrue(heap.poll() == 3)
        assertTrue(heap.poll() == 7)
        assertTrue(heap.poll() == 7)
        assertTrue(heap.poll() == 11)
        assertTrue(heap.poll() == 13)
    }

    @Test
    fun `test randomized polling`() {
        for (loopIndex in 0 until loops) {
            val randNums = genRandList(loopIndex)
            val priorityQueue = PriorityQueue<Int>()
            val heap = DaryMinHeap<Int>()

            // Add all the elements to both priority queues
            for (value in randNums) {
                priorityQueue.offer(value)
                heap.add(value)
            }

            while (!priorityQueue.isEmpty()) {
                assertEquals(priorityQueue.size, heap.size())
                assertEquals(priorityQueue.peek(), heap.peek())
                assertEquals(priorityQueue.contains(priorityQueue.peek()), heap.contains(heap.peek()!!))

                val v1 = priorityQueue.poll()
                val v2 = heap.poll()

                assertEquals(v1, v2)
                if (priorityQueue.peek() == null)
                    Assertions.assertNull(heap.peek())
                else
                    assertEquals(priorityQueue.peek(), heap.peek()!!)
                assertEquals(priorityQueue.size, heap.size())
            }
        }
    }

    @Test
    fun `test randomized removing`() {
        for (loopIndex in 0 until loops) {
            val randNums = genRandList(loopIndex)
            val priorityQueue = PriorityQueue<Int>()
            val heap = DaryMinHeap<Int>()

            // Add all the elements to both priority queues
            for (value in randNums) {
                priorityQueue.offer(value)
                heap.add(value)
            }

            Collections.shuffle(randNums)
            var index = 0

            while (!priorityQueue.isEmpty()) {
                val removeNum = randNums[index++]

                assertEquals(priorityQueue.size, heap.size())
                assertEquals(priorityQueue.peek(), heap.peek())
                priorityQueue.remove(removeNum)
                heap.remove(removeNum)
                if (priorityQueue.peek() == null)
                    Assertions.assertNull(heap.peek())
                else
                    assertEquals(priorityQueue.peek(), heap.peek()!!)
                assertEquals(priorityQueue.size, heap.size())
            }
        }
    }

    @Test
    fun `test heap reusability`() {
        val uniqueList = genUniqueRandList(loops)

        val priorityQueue = PriorityQueue<Int>()
        val heap = DaryMinHeap<Int>()

        for (size in uniqueList) {
            heap.clear()
            priorityQueue.clear()

            val nums = genRandList(size)
            for (num in nums) {
                heap.add(num)
                priorityQueue.add(num)
            }

            Collections.shuffle(nums)

            for (index in 0 until size / 2) {

                // Sometimes add a new number into the BinaryCompleteHeapUsingDynamicArray
                if (0.25 < Math.random()) {
                    val randNum = (Math.random() * 10000).toInt()
                    priorityQueue.add(randNum)
                    heap.add(randNum)
                }

                val removeNum = nums[index]

                assertEquals(priorityQueue.size, heap.size())
                assertEquals(priorityQueue.peek(), heap.peek())

                priorityQueue.remove(removeNum)
                heap.remove(removeNum)

                if (priorityQueue.peek() == null)
                    Assertions.assertNull(heap.peek())
                else
                    assertEquals(priorityQueue.peek(), heap.peek()!!)
                assertEquals(priorityQueue.size, heap.size())
            }
        }
    }

    private fun genRandArray(size: Int): Array<Int> {
        val intArray = arrayOfNulls<Int>(size)
        for (index in 0 until size)
            intArray[index] = (Math.random() * maxSize).toInt()
        return intArray as Array<Int>
    }

    // Generate a list of random numbers
    private fun genRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (index in 0 until size)
            list.add((Math.random() * maxSize).toInt())
        return list
    }

    // Generate a list of unique random numbers
    private fun genUniqueRandList(size: Int): List<Int> {
        val list = ArrayList<Int>(size)
        for (index in 0 until size)
            list.add(index)
        Collections.shuffle(list)
        return list
    }

}