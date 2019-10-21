import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DynamicArrayTest {

    private lateinit var dynamicIntArray: DynamicArray<Int>

    @BeforeEach
    fun beforeEach() {
        dynamicIntArray = DynamicArray()
    }

    @Test
    fun `test empty list`() {
        assertTrue(dynamicIntArray.isEmpty())
    }

    @Test
    fun `test removing from empty list`() {
        assertThrows<IndexOutOfBoundsException> {
            dynamicIntArray.removeAt(0)
        }
    }

    @Test
    fun `test removing from outOfBoundException`() {
        dynamicIntArray.add(1)
        dynamicIntArray.add(2)
        dynamicIntArray.add(3)
        assertThrows<IndexOutOfBoundsException> {
            dynamicIntArray.removeAt(3)
        }
    }

    @Test
    fun `test removing from outOfBoundException2`() {
        for (i in 0..999)
            dynamicIntArray.add(789)
        assertThrows<IndexOutOfBoundsException> {
            dynamicIntArray.removeAt(1000)
        }
    }

    @Test
    fun `test removing from outOfBoundException3`() {
        for (i in 0..999)
            dynamicIntArray.add(789)
        assertThrows<IndexOutOfBoundsException> {
            dynamicIntArray.removeAt(-1)
        }
    }

    @Test
    fun `test removing from outOfBoundException4`() {
        for (i in 0..15)
            dynamicIntArray.add(1)
        assertThrows<IndexOutOfBoundsException> {
            dynamicIntArray.removeAt(-66)
        }
    }

    @Test
    fun `test removing`() {
        val dynamicStringArray = DynamicArray<String>()
        val staticStringArray = arrayOf<String>("a", "b", "c", "d", "e", "g", "h")
        for (stringItem in staticStringArray)
            dynamicStringArray.add(stringItem)

        var success = dynamicStringArray.remove("c")
        assertTrue(success)

        success = dynamicStringArray.remove("c")
        assertFalse(success)

        success = dynamicStringArray.remove("h")
        assertTrue(success)


        success = dynamicStringArray.remove("a")
        assertTrue(success)

        success = dynamicStringArray.remove("a")
        assertFalse(success)

        success = dynamicStringArray.remove("h")
        assertFalse(success)
    }

    @Test
    fun `test removing 2`() {
        val dynamicStringArray = DynamicArray<String>()
        val staticStringArray = arrayOf("a", "b", "c", "d")
        for (stringItem in staticStringArray)
            dynamicStringArray.add(stringItem)

        assertTrue(dynamicStringArray.remove("a"))
        assertTrue(dynamicStringArray.remove("b"))
        assertTrue(dynamicStringArray.remove("c"))
        assertTrue(dynamicStringArray.remove("d"))

        assertFalse(dynamicStringArray.remove("a"))
        assertFalse(dynamicStringArray.remove("b"))
        assertFalse(dynamicStringArray.remove("c"))
        assertFalse(dynamicStringArray.remove("d"))
    }

    @Test
    fun `test indexOf string`() {
        val dynamicStringArray = DynamicArray<String>()
        dynamicStringArray.add("a")
        dynamicStringArray.add("b")

        assertEquals(0, dynamicStringArray.indexOf("a"))
        assertEquals(1, dynamicStringArray.indexOf("b"))
        assertEquals(-1, dynamicStringArray.indexOf("c"))
    }

    @Test
    fun `test indexOf int`() {
        val dynamicStringArray = DynamicArray<Int>()
        dynamicStringArray.add(1)
        dynamicStringArray.add(2)

        assertEquals(0, dynamicStringArray.indexOf(1))
        assertEquals(1, dynamicStringArray.indexOf(2))
        assertEquals(-1, dynamicStringArray.indexOf(3))
    }

    @Test
    fun `test add-get elements`() {
        val list = DynamicArray<Int>()
        val elements = intArrayOf(1, 2, 3, 4, 5, 6, 7)

        for (index in elements.indices)
            list.add(elements[index])

        for (index in elements.indices)
            assertEquals(list.get(index), elements[index])
    }

    @Test
    fun `test add-remove`() {
        val dynamicArray = DynamicArray<Long>(0)

        for (i in 0..54) dynamicArray.add(44L)
        for (i in 0..54) dynamicArray.remove(44L)
        assertTrue(dynamicArray.isEmpty())

        for (i in 0..54) dynamicArray.add(44L)
        for (i in 0..54) dynamicArray.removeAt(0)
        assertTrue(dynamicArray.isEmpty())

        for (i in 0..154) dynamicArray.add(44L)
        for (i in 0..154) dynamicArray.remove(44L)
        assertTrue(dynamicArray.isEmpty())

        for (i in 0..154) dynamicArray.add(44L)
        for (i in 0..154) dynamicArray.removeAt(0)
        assertTrue(dynamicArray.isEmpty())
    }

    @Test
    fun `test set-remove`() {
        val dynamicArray = DynamicArray<Long>(0)

        for (i in 0..54) dynamicArray.add(44L)
        for (i in 0..54) dynamicArray.set(i, 33L)
        for (i in 0..54) dynamicArray.remove(33L)
        assertTrue(dynamicArray.isEmpty())

        for (i in 0..54) dynamicArray.add(44L)
        for (i in 0..54) dynamicArray.set(i, 33L)
        for (i in 0..54) dynamicArray.removeAt(0)
        assertTrue(dynamicArray.isEmpty())

        for (i in 0..154) dynamicArray.add(44L)
        for (i in 0..154) dynamicArray.set(i, 33L)
        for (i in 0..154) dynamicArray.remove(33L)
        assertTrue(dynamicArray.isEmpty())

        for (i in 0..154) dynamicArray.add(44L)
        for (i in 0..154) dynamicArray.removeAt(0)
        assertTrue(dynamicArray.isEmpty())
    }

    @Test
    fun `test size`() {
        val dynamicArray = DynamicArray<Int>()

        val elements = arrayOf<Int>(-76, 45, 66, 3, 54, 33)
        var index = 0
        var size = 1
        while (index < elements.size) {
            dynamicArray.add(elements[index])
            assertEquals(dynamicArray.size, size)
            index++
            size++
        }
    }

    @Test
    fun `test clear`() {
        val dynamicArray = DynamicArray<Int>()
        dynamicArray.add(1)
        dynamicArray.add(2)
        assertEquals(2, dynamicArray.size)

        dynamicArray.clear()
        assertTrue(dynamicArray.isEmpty())
    }

    @Test
    fun `test insert`() {
        val dynamicArray = DynamicArray<Int>()
        dynamicArray.insert(0, 0)
        dynamicArray.insert(1, 1)
        assertEquals(2, dynamicArray.size)
        assertEquals(0, dynamicArray.get(0))
        assertEquals(1, dynamicArray.get(1))

        dynamicArray.insert(1, 2)
        assertEquals(2, dynamicArray.size)
        assertEquals(2, dynamicArray.get(1))
    }
}