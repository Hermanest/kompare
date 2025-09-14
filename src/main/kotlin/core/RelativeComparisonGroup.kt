package core

class RelativeComparisonGroup(
    mainPath: String,
    comparisons: List<RelativeComparison>
) {
    private val _comparisons = comparisons
    private val _filteredComparisons = ArrayList<RelativeComparison>(comparisons.size)

    val main = RelativeComparison(mainPath, 1.0)

    val combinedComparisons: List<RelativeComparison>
        get() = _filteredComparisons.emptyOr { it }

    val otherComparisons: List<RelativeComparison>
        get() = _filteredComparisons.emptyOr { it.subList(1, it.size) }

    fun filterBy(threshold: Float = 0f, phrase: String = "") {
        _filteredComparisons.clear()
        _filteredComparisons.add(main)

        _comparisons.forEach {
            if (it.percentage / 100 < threshold) {
                return@forEach
            }
            
            if (!it.path.contains(phrase)) {
                return@forEach
            }

            _filteredComparisons.add(it)
        }
    }

    private inline fun <T> List<T>.emptyOr(delegate: (List<T>) -> List<T>): List<T> {
        return if (this.size > 1) {
            delegate(this)
        } else {
            emptyList()
        }
    }

    override fun hashCode(): Int {
        return main.path.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return main.path == (other as? RelativeComparisonGroup)?.main?.path
    }
}
