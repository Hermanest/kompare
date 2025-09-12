package ui.adapters

import core.IComparatorFactory
import core.IComparisonProcessor

class ProcessorProvider(private val factory: IComparatorFactory) : IProcessorProvider {
    override val processor: IComparisonProcessor
        get() = _processor ?: throw IllegalStateException("Processor is not initialized")
    
    private var _processor: IComparisonProcessor? = null
    
    fun setProcessor(processor: IComparisonProcessor) {
        _processor = processor
    }
    
    fun clearProcessor() {
        _processor = null
    }
}