package core

sealed interface IComparisonResult

data class AnalyzeResult(val results: MutableList<ComparisonGroup>) : IComparisonResult
data class FindMatchResult(val results: MutableList<ComparisonGroup>) : IComparisonResult