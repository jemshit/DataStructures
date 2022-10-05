package queue

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.exp

internal class MaxSlidingWindowTest {

    private fun maxSlidingWindow(input: Array<Int>, k: Int)
            : Array<Int> {
        val window = MonotonicQueue()
        val result: MutableList<Int> = mutableListOf()

        for (index in 0 until input.size) {
            if (index < k - 1) {
                // fill the window first
                window.push(input[index])
            } else {
                // the window begins to slide forward, window size is (k-1)
                window.push(input[index])
                result.add(window.max())
                window.pop(input[index - k + 1])    //  is the last element of the window
            }
        }
        return result.toTypedArray()
    }

    @Test
    fun `test`() {
        val input = arrayOf(1, 3, -1, -3, 5, 3, 6, 7)
        val expected = arrayOf(3, 3, 5, 5, 6, 7)
        Assertions.assertArrayEquals(expected, maxSlidingWindow(input, k = 3))

        val input2 = arrayOf(1, 3, -1, -3, -4, 3, 6, 7)
        val expected2 = arrayOf(3, 3, -1, 3, 6, 7)
        Assertions.assertArrayEquals(expected2, maxSlidingWindow(input2, k = 3))
    }
}