package utils

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.opencv.core.Mat
import kotlin.math.roundToInt

val Mat.aspect: Float
    get() {
        val size = this.size()
        val aspect = size.width / size.height
        return (aspect * 100).roundToInt() / 100f
    }

fun <T> T.pairedHash(other: T): Int {
    var first = this.hashCode()
    var second = other.hashCode()

    if (first > second) {
        val cached = second
        second = first
        first = cached
    }

    return first + second
}

fun <T> List<T>.stableKey(): Int {
    return fold(0) { acc, item -> acc + item.hashCode() }
}

operator fun Color.times(factor: Float): Color {
    return copy(
        alpha,
        red * factor, 
        green * factor,
        blue * factor
    )
}

fun <T> T.clamp(min: T, max: T): T where T : Number, T : Comparable<T> {
    return when {
        this > max -> max
        this < min -> min
        else -> this
    }
}

fun Float.lerp(from: Float, to: Float, inverted: Boolean = false): Float {
    return (to - from) * (if (inverted) 1 - this else this) + from
}