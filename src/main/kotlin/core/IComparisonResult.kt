package core

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

sealed interface IComparisonResult

class AnalyzeResult(results: MutableList<ComparisonGroup>) : IComparisonResult {
    private val _onGroupRemoved = MutableSharedFlow<ComparisonGroup>()
    private val _results = results

    val onGroupRemoved: SharedFlow<ComparisonGroup> = _onGroupRemoved
    val results: List<ComparisonGroup> = _results

    @OptIn(DelicateCoroutinesApi::class)
    fun removeGroup(group: ComparisonGroup) {
        _results.remove(group)
        
        GlobalScope.launch {
            _onGroupRemoved.emit(group)
        }
    }
}

data class FindMatchResult(val results: MutableList<ComparisonGroup>) : IComparisonResult