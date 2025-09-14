package core

interface IComparisonFileManager {
    fun deleteGroup(group: ComparisonGroup)
    fun delete(group: ComparisonGroup, path: String)
}