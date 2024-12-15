import core.Comparison
import kotlin.test.Test
import kotlin.test.assertEquals

class ComparisonHashTest {
    @Test
    fun basic() {
        val comparison1 = Comparison(0.0, "hello", "world")
        val comparison2 = Comparison(1.0, "world", "hello")
        assertEquals(comparison1, comparison2)
    }
    
    @Test
    fun hashSet() {
        val comparison1 = Comparison(0.0, "hello", "world")
        val comparison2 = Comparison(1.0, "world", "hello")
        val set = hashSetOf<Comparison>()
        
        set.add(comparison1)
        assertEquals(set.contains(comparison2), true)
        
        set.add(comparison2)
        assertEquals(set.size, 1)
    }
}