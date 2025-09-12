package core

import core.comparators.SsimComparator
import core.processors.AnalyzeComparisonProcessor

class ComparatorFactory : IComparatorFactory {
    override fun createProcessor(data: IComparisonInitData): IComparisonProcessor {
        return when (data) {
            is AnalyzeInitData -> AnalyzeComparisonProcessor(data, SsimComparator)
            is FindMatchInitData -> throw Exception()
        }
    }
}