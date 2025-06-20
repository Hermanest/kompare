package core.preprocessors

import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.roundToInt

class SizeImagePreprocessor(private val referenceSize: Double) : IImagePreprocessor {
    override fun preprocess(mat: Mat) {
        resize(mat, referenceSize)
    }

    private fun resize(mat: Mat, refSize: Double) {
        val aspectRaw = mat.width().toDouble() / mat.height()
        val aspect = (aspectRaw * 100).roundToInt() / 100.0

        val newWidth: Int
        val newHeight: Int

        if (aspect >= 1.0) {
            newWidth = refSize.roundToInt()
            newHeight = (refSize / aspect).roundToInt()
        } else {
            newHeight = refSize.roundToInt()
            newWidth = (refSize * aspect).roundToInt()
        }

        val newSize = Size(newWidth.toDouble(), newHeight.toDouble())
        Imgproc.resize(mat, mat, newSize)
    }
}
