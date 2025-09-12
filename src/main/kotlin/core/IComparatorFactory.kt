package core

interface IComparatorFactory {
    fun createProcessor(data: IComparisonInitData): IComparisonProcessor
}