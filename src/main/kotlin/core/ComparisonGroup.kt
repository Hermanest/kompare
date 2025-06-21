package core

class ComparisonGroup(val comparisons: List<Comparison>) {
    private val similarityMap = comparisons
        .associateBy { Comparison.calcHash(it.path1, it.path2) }

    val paths = comparisons
        .flatMap { listOf(it.path1, it.path2) }
        .toSet()
        .sorted()

    var mainPath = paths.first()
        private set

    val size: Int get() = paths.size

    fun setMainPath(index: Int) {
        require(index in paths.indices) {
            "Index $index out of bounds for paths of size ${paths.size}"
        }
        mainPath = paths[index]
    }

    fun getPathAt(index: Int): String = paths[index]

    fun getComparisons(anchorIndex: Int): RelativeComparisonGroup {
        return getComparisonsFor(paths[anchorIndex])
    }

    fun getComparisons(): RelativeComparisonGroup {
        return getComparisonsFor(mainPath)
    }

    private fun getComparisonsFor(anchor: String): RelativeComparisonGroup {
        val filtered = paths
            .asSequence()
            .filter { it != anchor }
            .mapNotNull { other ->
                val hash = Comparison.calcHash(anchor, other)
                similarityMap[hash]
            }
            .map {
                RelativeComparison(
                    path = if (it.path1 == anchor) it.path2 else it.path1, 
                    similarity = it.similarity
                )
            }
            .toList()

        return RelativeComparisonGroup(anchor, filtered, this)
    }
}
