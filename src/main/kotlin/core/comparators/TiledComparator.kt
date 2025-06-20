package core.comparators

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import java.util.*
import kotlin.math.min

object TiledComparator : IImageComparator {
    //region Caching

    private val cachedMaterials = Stack<Mat>()

    private fun getTempMaterial(): Mat {
        return if (cachedMaterials.isNotEmpty()) {
            cachedMaterials.pop()
        } else {
            Mat()
        }
    }

    private fun releaseTempMaterial(mat: Mat) {
        cachedMaterials.push(mat)
    }

    override val preprocessor = null

    //endregion

    override fun compare(mat1: Mat, mat2: Mat): Double {
        val (tileSize, gapSize1, gapSize2) = determineTileSize(mat1, mat2)

        val tiles1 = splitIntoTiles(mat1, tileSize, gapSize1)
        val tiles2 = splitIntoTiles(mat2, tileSize, gapSize2)

        return matchTileMatrices(tiles1, tiles2)
    }

    private fun determineTileSize(mat1: Mat, mat2: Mat): Triple<Int, Int, Int> {
        val refSize = minOf(mat1.rows(), mat1.cols(), mat2.rows(), mat2.cols())
        val tileSize = refSize / 10 // Tile count

        val gapSize1 = mat1.rows() % tileSize
        val gapSize2 = mat2.rows() % tileSize

        return Triple(tileSize, gapSize1, gapSize2)
    }

    private fun splitIntoTiles(image: Mat, tileSize: Int, gapSize: Int): List<Mat> {
        val tiles = mutableListOf<Mat>()
        val adjustedRows = image.rows() - gapSize
        val adjustedCols = image.cols() - gapSize

        for (y in 0 until adjustedRows step tileSize) {
            for (x in 0 until adjustedCols step tileSize) {
                val rect = Rect(x, y, minOf(tileSize, image.cols() - x), minOf(tileSize, image.rows() - y))
                val mat = Mat(image, rect)

                tiles.add(mat)
            }
        }

        return tiles
    }

    private fun matchTileMatrices(tiles1: List<Mat>, tiles2: List<Mat>): Double {
        var totalScore = 0.0
        var count = 0

        for (index in 0 until min(tiles1.size, tiles2.size) - 1) {
            val tile1 = tiles1[index]
            val tile2 = tiles2[index]

            totalScore += compareTiles(tile1, tile2)
            count++
        }

        return totalScore / count
    }

    private fun compareTiles(mat1: Mat, mat2: Mat): Double {
        val diff = getTempMaterial()

        try {
            Core.subtract(mat1, mat2, diff)
            Core.multiply(diff, diff, diff)

            // Sum the squared differences for each channel
            val diffSum = Core.sumElems(diff).`val`.sum()

            // Compute the number of pixels and channels
            val totalElements = diff.rows() * diff.cols() * diff.channels()

            // Compute mean squared error (MSE)
            val mse = diffSum / totalElements

            // Compute similarity as an inverse function of MSE
            return 1.0 / (1.0 + mse)
        } catch (ex: Exception) {
            return 0.0
        } finally {
            releaseTempMaterial(diff)
        }
    }
}