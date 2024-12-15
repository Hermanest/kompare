import core.Comparison
import kotlin.test.Test
import kotlin.test.assertEquals

class ComparisonHashTest {
    @Test
    fun basic() {
        val comparison1 = Comparison(0.0, "hello", "world", null)
        val comparison2 = Comparison(1.0, "world", "hello", null)
        assertEquals(comparison1, comparison2)
    }
    
    @Test
    fun hashSet() {
        val comparison1 = Comparison(0.0, "hello", "world", null)
        val comparison2 = Comparison(1.0, "world", "hello", null)
        val set = hashSetOf<Comparison>()
        
        set.add(comparison1)
        assertEquals(set.contains(comparison2), true)
        
        set.add(comparison2)
        assertEquals(set.size, 1)
    }
}