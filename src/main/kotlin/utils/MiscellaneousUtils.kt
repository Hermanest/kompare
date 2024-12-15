package utils

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

fun <T> T.clamp(min: T, max: T): T where T : Number, T : Comparable<T> {
    return when {
        this > max -> max
        this < min -> min
        else -> this
    }
}
