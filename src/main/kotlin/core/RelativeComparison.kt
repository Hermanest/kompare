package core

import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.math.round

class RelativeComparison(
    val path: String,
    val similarity: Double
) {
    val name by lazy { Path(path).name }
    val percentage: Double = round(similarity * 10000) / 100

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RelativeComparison

        if (similarity != other.similarity) return false
        if (percentage != other.percentage) return false
        if (path != other.path) return false
        if (name != other.name) return false

        return true
    }
}