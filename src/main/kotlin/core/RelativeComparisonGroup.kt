package core

class RelativeComparisonGroup(
    mainPath: String,
    otherComparisons: List<RelativeComparison>,
    val parentGroup: ComparisonGroup
) {
    val comparisons = listOf(RelativeComparison(mainPath, 1.0)) + otherComparisons
    val size: Int get() = comparisons.size
    val main = comparisons.first()
    
    fun withThreshold(threshold: Double): RelativeComparisonGroup {
        return RelativeComparisonGroup(
            main.path,
            comparisons.filter { it.similarity >= threshold },
            parentGroup
        )
    }

    override fun hashCode(): Int {
        return main.path.hashCode()
    }
    
    override fun equals(other: Any?): Boolean {
        return main.path == (other as? RelativeComparisonGroup)?.main?.path
    }
}
