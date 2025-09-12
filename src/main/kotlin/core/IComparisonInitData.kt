package core

sealed interface IComparisonInitData

class AnalyzeInitData(
    val directoryPath: String, 
    val filterOffThreshold: Float
) : IComparisonInitData

class FindMatchInitData(val mainPath: String, val directoryPath: String) : IComparisonInitData