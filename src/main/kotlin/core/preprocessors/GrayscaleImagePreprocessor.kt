package core.preprocessors

import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class GrayscaleImagePreprocessor : IImagePreprocessor {
    override fun preprocess(mat: Mat) {
        if (mat.channels() == 3) {
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
        }
    }
}