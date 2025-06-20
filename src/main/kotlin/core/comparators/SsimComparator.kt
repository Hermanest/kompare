package core.comparators

import core.preprocessors.GrayscaleImagePreprocessor
import core.preprocessors.ProxyImagePreprocessor
import core.preprocessors.SizeImagePreprocessor
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

object SsimComparator : IImageComparator {
    override val preprocessor = ProxyImagePreprocessor(
        listOf(
            SizeImagePreprocessor(128.0),
            GrayscaleImagePreprocessor()
        )
    )

    override fun compare(mat1: Mat, mat2: Mat): Double {
        return ssim(mat1, mat2)
    }

    fun ssim(mat1: Mat, mat2: Mat): Double {
        // Convert to 32F
        val img1 = Mat()
        val img2 = Mat()
        mat1.convertTo(img1, CvType.CV_32F)
        mat2.convertTo(img2, CvType.CV_32F)

        // Means
        val mu1 = Mat()
        val mu2 = Mat()
        Imgproc.GaussianBlur(img1, mu1, Size(11.0, 11.0), 1.5)
        Imgproc.GaussianBlur(img2, mu2, Size(11.0, 11.0), 1.5)

        // Squares of means
        val mu1Sq = Mat()
        val mu2Sq = Mat()
        Core.multiply(mu1, mu1, mu1Sq)
        Core.multiply(mu2, mu2, mu2Sq)

        // mu1 * mu2
        val mu1Mu2 = Mat()
        Core.multiply(mu1, mu2, mu1Mu2)

        // Squares
        val img1Sq = Mat()
        val img2Sq = Mat()
        Core.multiply(img1, img1, img1Sq)
        Core.multiply(img2, img2, img2Sq)

        // Cross
        val img1Img2 = Mat()
        Core.multiply(img1, img2, img1Img2)

        // Variance = E[x^2] - (E[x])^2
        val sigma1Sq = Mat()
        val sigma2Sq = Mat()
        val sigma12 = Mat()

        Imgproc.GaussianBlur(img1Sq, sigma1Sq, Size(11.0, 11.0), 1.5)
        Core.subtract(sigma1Sq, mu1Sq, sigma1Sq)

        Imgproc.GaussianBlur(img2Sq, sigma2Sq, Size(11.0, 11.0), 1.5)
        Core.subtract(sigma2Sq, mu2Sq, sigma2Sq)

        Imgproc.GaussianBlur(img1Img2, sigma12, Size(11.0, 11.0), 1.5)
        Core.subtract(sigma12, mu1Mu2, sigma12)

        // Constants for stability (from the original SSIM paper)
        val C1 = 6.5025
        val C2 = 58.5225

        val t1 = Mat()
        val t2 = Mat()
        val t3 = Mat()

        // (2*mu1*mu2 + C1)
        Core.multiply(mu1Mu2, Scalar(2.0), t1)
        Core.add(t1, Scalar(C1), t1)

        // (2*sigma12 + C2)
        Core.multiply(sigma12, Scalar(2.0), t2)
        Core.add(t2, Scalar(C2), t2)

        // t1 * t2
        Core.multiply(t1, t2, t3)

        // (mu1^2 + mu2^2 + C1)
        Core.add(mu1Sq, mu2Sq, t1)
        Core.add(t1, Scalar(C1), t1)

        // (sigma1^2 + sigma2^2 + C2)
        Core.add(sigma1Sq, sigma2Sq, t2)
        Core.add(t2, Scalar(C2), t2)

        // t1 * t2
        Core.multiply(t1, t2, t1)

        // Final SSIM map
        val ssimMap = Mat()
        Core.divide(t3, t1, ssimMap)

        val mean = Core.mean(ssimMap)

        // Cleanup
        listOf(
            img1, img2, mu1, mu2, mu1Sq, mu2Sq, mu1Mu2,
            img1Sq, img2Sq, img1Img2, sigma1Sq, sigma2Sq, sigma12, t1, t2, t3, ssimMap
        ).forEach { it.release() }

        return mean.`val`[0] // SSIM value ∈ [0, 1], higher is more similar
    }
}