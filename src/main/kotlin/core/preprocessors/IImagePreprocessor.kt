package core.preprocessors

import org.opencv.core.Mat

interface IImagePreprocessor {
    fun preprocess(mat: Mat)
}