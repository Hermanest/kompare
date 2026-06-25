package ui.views.comparison.models

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import core.ComparisonGroup

enum class SortOrder {
    ASCENDING,
    DESCENDING,
    NONE
}

enum class SortBy {
    SIMILARITY,
    ALPHABET,
}

class UiComparisonsList(
    val groupingThreshold: Float,
    private val rawGroups: List<ComparisonGroup>
) : AbstractList<UiComparisonGroup>() {
    var sortOrder by mutableStateOf(SortOrder.NONE)
    var sortBy by mutableStateOf(SortBy.SIMILARITY)

    private val groupsCache = mutableMapOf<Int, UiComparisonGroup>()

    private val sortedIndices: List<Int> by derivedStateOf {
        val originalIndices = rawGroups.indices.toList()
        when (sortOrder) {
            SortOrder.ASCENDING -> originalIndices.sorted()
            SortOrder.DESCENDING -> originalIndices.sortedDescending()
            SortOrder.NONE -> originalIndices
        }
    }

    override val size: Int get() = rawGroups.size
    val totalSize = rawGroups.size

    override fun get(index: Int): UiComparisonGroup {
        val originalGroupIndex = sortedIndices[index]

        return synchronized(groupsCache) {
            groupsCache.getOrPut(originalGroupIndex) {
                UiComparisonGroup(
                    id = originalGroupIndex,
                    rawGroup = rawGroups[originalGroupIndex]
                )
            }
        }
    }

    fun filter(predicate: (UiComparisonGroup, UiComparison) -> Boolean) {

    }
}