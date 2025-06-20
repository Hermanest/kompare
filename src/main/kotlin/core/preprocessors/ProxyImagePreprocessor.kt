package core.preprocessors

import org.opencv.core.Mat

class ProxyImagePreprocessor(private val preprocessors: Iterable<IImagePreprocessor>) : IImagePreprocessor {
    override fun preprocess(mat: Mat) {
        preprocessors.forEach { it.preprocess(mat) }
    }
}