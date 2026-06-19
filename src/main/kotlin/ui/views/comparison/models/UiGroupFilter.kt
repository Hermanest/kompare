package ui.views.comparison.models

data class UiGroupFilter(
    val filterOffThreshold: Float,
    val filterPhrase: String?,
) {
    fun filter(group: UiComparisonGroup, comp: UiComparison): Boolean {
        return comp.similarity >= filterOffThreshold &&
                filterPhrase?.let { comp.path.contains(it, true) } ?: true
    }
}