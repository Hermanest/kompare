package core

import org.bytedeco.opencv.opencv_core.*
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.math.round


class Comparison(
    val similarity: Double,
    val path1: String,
    val path2: String
) {
    companion object {
        fun calcHash(path1: String, path2: String): Int {
            lateinit var first: String
            lateinit var second: String

            if (path1 > path2) {
                first = path1
                second = path2
            } else {
                first = path2
                second = path1
            }

            return first.hashCode() + second.hashCode()
        }

    }

    val name1 by lazy { Path(path1).name }
    val name2 by lazy { Path(path2).name }

    fun percentage(): Double {
        return round(similarity * 10000) / 100
    }
    
    override fun equals(other: Any?): Boolean {
        if (other !is Comparison) {
            return false
        }
        return other.hashCode() == hashCode()
    }

    override fun hashCode(): Int {
        return calcHash(path1, path2)
    }
}