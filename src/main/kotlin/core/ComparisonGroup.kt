package core

import kotlin.io.path.Path
import kotlin.io.path.name

class ComparisonGroup(
    val mainPath: String,
    val other: List<Comparison>
) {
    val mainName by lazy { Path(mainPath).name }
    val size = 1 + other.size
    
    fun getPathAt(index: Int): String {
        return if (index == 0) mainPath else other[index - 1].path
    }
}