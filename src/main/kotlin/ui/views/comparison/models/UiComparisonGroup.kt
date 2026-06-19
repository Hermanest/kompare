package ui.views.comparison.models

import androidx.compose.runtime.mutableStateListOf
import core.ComparisonGroup

class UiComparisonGroup(
    val id: Int,
    private val rawGroup: ComparisonGroup,
) {
    val comparisons get() = mutableComparisons

    private val mutableComparisons = mutableStateListOf<UiComparison>()
    private val deleted = mutableSetOf<String>()
    private val filtered = mutableSetOf<String>()

    init {
        setAnchor(rawGroup.comparisons[0].path1)
    }

    fun setAnchor(comparison: String) {
        val anchor = UiComparison(comparison, 1.0, false, false)

        val comparisons = rawGroup.comparisons
            .asSequence()
            .filter { it.path1 == comparison || it.path2 == comparison }
            .map {
                val path = if (it.path1 == comparison) it.path2 else it.path1
                UiComparison(path, it.similarity, false, false)
            }

        mutableComparisons.clear()
        mutableComparisons.add(anchor)
        mutableComparisons.addAll(comparisons)
    }

    fun delete(index: Int) {
        val comparison = comparisons[index]

        deleted.add(comparison.path)
        comparisons[index] = comparison.copy(isDeleted = true)
    }

    fun restore(index: Int) {
        val comparison = comparisons[index]

        deleted.remove(comparison.path)
        comparisons[index] = comparison.copy(isDeleted = false)
    }

    fun deleteSweep() {
        comparisons.forEachIndexed { index, comp ->
            deleted.add(comp.path)
            comparisons[index] = comp.copy(isDeleted = true)
        }
    }

    fun __filter(predicate: (UiComparison) -> Boolean) {
        comparisons.forEach { comparison ->
            if (!predicate(comparison)) {
                filtered.add(comparison.path)
                //comparison.__setFiltered(true)
            } else {
                filtered.remove(comparison.path)
                //comparison.__setFiltered(false)
            }
        }
    }
}