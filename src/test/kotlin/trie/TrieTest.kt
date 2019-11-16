package trie

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TrieTest {

    @Test
    fun `insert and search`() {
        val keys = arrayOf("the", "a", "there", "answer", "any", "by", "their")
        val trie = Trie()

        for (key in keys) {
            assertFalse(trie.search(key))
            trie.insert(key)
            assertTrue(trie.search(key))
        }
        assertTrue(trie.search("the"))
        assertTrue(trie.search("there"))

        assertFalse(trie.search("th"))
        assertFalse(trie.search("an"))
    }

    @Test
    fun `getAll`() {
        val keys = arrayOf("the", "a", "there", "answer", "any", "by", "their")
        val trie = Trie()
        for (key in keys)
            trie.insert(key)

        val alphabets = trie.getAll()

        assertEquals(keys.size, alphabets.size)
        assertEquals("a", alphabets[0])
        assertEquals("answer", alphabets[1])
        assertEquals("any", alphabets[2])
        assertEquals("by", alphabets[3])
        assertEquals("the", alphabets[4])
        assertEquals("their", alphabets[5])
        assertEquals("there", alphabets[6])
    }

    @Test
    fun `remove non-exist key`() {
        val keys = arrayOf("the", "a", "there", "answer", "any", "by", "their")
        val trie = Trie()
        for (key in keys)
            trie.insert(key)

        trie.remove("akl")

        for (key in keys)
            assertTrue(trie.search(key))
    }

    @Test
    fun `remove unique key`() {
        val keys = arrayOf("the", "a", "there", "answer", "any", "by", "their")
        val trie = Trie()
        for (key in keys)
            trie.insert(key)

        trie.remove("by")

        assertEquals(6, trie.getAll().size)
        for (key in keys)
            if (key != "by")
                assertTrue(trie.search(key))
            else
                assertFalse(trie.search(key))
    }

    @Test
    fun `remove longer key`() {
        val keys = arrayOf("the", "a", "there", "answer", "any", "by", "their")
        val trie = Trie()
        for (key in keys)
            trie.insert(key)

        trie.remove("answer")

        assertEquals(6, trie.getAll().size)
        for (key in keys)
            if (key != "answer")
                assertTrue(trie.search(key))
            else
                assertFalse(trie.search(key))
    }

    @Test
    fun `remove shorter key`() {
        val keys = arrayOf("the", "a", "there", "answer", "any", "by", "their")
        val trie = Trie()
        for (key in keys)
            trie.insert(key)

        trie.remove("a")

        assertEquals(6, trie.getAll().size)
        for (key in keys)
            if (key != "a")
                assertTrue(trie.search(key))
            else
                assertFalse(trie.search(key))
    }

    @Test
    fun `remove single key`() {
        val keys = arrayOf("the")
        val trie = Trie()
        for (key in keys)
            trie.insert(key)

        trie.remove("the")

        assertEquals(0, trie.getAll().size)
        assertFalse(trie.search("the"))
    }

}