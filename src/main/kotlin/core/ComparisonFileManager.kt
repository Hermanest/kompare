package core

import utils.deleteFile

object ComparisonFileManager : IComparisonFileManager {
    override fun deleteGroup(group: ComparisonGroup) {
        for (item in group.paths) {
            deleteFile(item)    
        }
        group.markGroupDeleted()
    }
    
    override fun delete(group: ComparisonGroup, path: String) {
        group.removePath(path)
        deleteFile(path)
    }
}