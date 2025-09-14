package core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed interface IComparisonResult

class AnalyzeResult(results: MutableList<ComparisonGroup>) : IComparisonResult {
    private val _onGroupRemoved = MutableSharedFlow<ComparisonGroup>()
    private val _results = results

    val onGroupRemoved: SharedFlow<ComparisonGroup> = _onGroupRemoved
    val results: List<ComparisonGroup> = _results

    fun removeGroup(group: ComparisonGroup) {
        _results.remove(group)
        _onGroupRemoved.tryEmit(group)
    }
}

data class FindMatchResult(val results: MutableList<ComparisonGroup>) : IComparisonResult