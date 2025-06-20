package core.comparators

import core.preprocessors.IImagePreprocessor
import org.opencv.core.Mat

interface IImageComparator {
    val preprocessor: IImagePreprocessor?

    fun compare(mat1: Mat, mat2: Mat): Double
}