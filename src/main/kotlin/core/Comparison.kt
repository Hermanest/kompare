package core

import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.math.round

class Comparison(
    val path: String,
    val similarity: Double
) {
    val name by lazy { Path(path).name }
    val percentage: Double = round(similarity * 10000) / 100

    override fun hashCode(): Int {
        return path.hashCode()
    }
}