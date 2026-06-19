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

class UiComparisonsList(
    val groupingThreshold: Float,
    private val rawGroups: List<ComparisonGroup>
) {
    private var sortOrder by mutableStateOf(SortOrder.NONE)
    private val groupsCache = mutableMapOf<Int, UiComparisonGroup>()

    val groups: List<UiComparisonGroup> = object : AbstractList<UiComparisonGroup>() {
        private val sortedIndices: List<Int> by derivedStateOf {
            val originalIndices = rawGroups.indices.toList()
            when (sortOrder) {
                SortOrder.ASCENDING -> originalIndices.sorted()
                SortOrder.DESCENDING -> originalIndices.sortedDescending()
                SortOrder.NONE -> originalIndices
            }
        }

        override val size: Int get() = rawGroups.size

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
    }

    val totalSize = groups.size

    fun sort(order: SortOrder) {
        sortOrder = order
    }

    fun filter(predicate: (UiComparisonGroup, UiComparison) -> Boolean) {

    }
}