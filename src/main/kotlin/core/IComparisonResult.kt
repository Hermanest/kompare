package core

sealed interface IComparisonResult

class AnalyzeResult(results: MutableList<ComparisonGroup>) : IComparisonResult {
    private val _results = results

    val results: List<ComparisonGroup> = _results
}

data class FindMatchResult(val results: MutableList<ComparisonGroup>) : IComparisonResult