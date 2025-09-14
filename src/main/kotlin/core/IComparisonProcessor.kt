package core

import kotlinx.coroutines.flow.StateFlow

interface IComparisonProcessor {
    val total: StateFlow<Int>
    val handled: StateFlow<Int>
    val stage: StateFlow<String>
    
    val result: IComparisonResult?
    val initData: IComparisonInitData
    
    fun start()
    suspend fun join()
}