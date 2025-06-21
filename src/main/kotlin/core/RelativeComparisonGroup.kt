package core

class RelativeComparisonGroup(
    mainPath: String,
    val otherComparisons: List<RelativeComparison>,
    val parentGroup: ComparisonGroup
) {
    val main = RelativeComparison(mainPath, 1.0)
    val combinedComparisons = listOf(main) + otherComparisons
    
    fun withThreshold(threshold: Double): RelativeComparisonGroup {
        return RelativeComparisonGroup(
            main.path,
            otherComparisons.filter { it.similarity >= threshold },
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
