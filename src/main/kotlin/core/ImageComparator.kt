package core

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.bytedeco.javacpp.FloatPointer
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.opencv.global.opencv_core.CV_8U
import org.bytedeco.opencv.global.opencv_core.CV_8UC3
import org.bytedeco.opencv.global.opencv_core.NORM_MINMAX
import org.bytedeco.opencv.global.opencv_core.absdiff
import org.bytedeco.opencv.global.opencv_core.normalize
import org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR
import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV
import org.bytedeco.opencv.global.opencv_imgproc.HISTCMP_CORREL
import org.bytedeco.opencv.global.opencv_imgproc.calcHist
import org.bytedeco.opencv.global.opencv_imgproc.compareHist
import org.bytedeco.opencv.global.opencv_imgproc.cvtColor
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.MatVector
import org.bytedeco.opencv.opencv_core.Size
import utils.pairedHash
import kotlin.collections.groupBy

private typealias Images = List<Pair<String, Mat>>

object ImageComparator {
    private const val MAX_TASKS_COUNT = 100
    private const val REF_IMAGE_SIZE = 512

    suspend fun compare(paths: List<String>, updateCallback: (Int, Int, String) -> Unit): MutableList<Comparison> =
        coroutineScope {
            lateinit var imageGroups: Collection<Images>
            launch {
                val images = loadImages(paths) { current, total ->
                    updateCallback(current, total, "Converting")
                }
                imageGroups = images.groupBy {
                    val size = it.second.size()
                    val aspect = size.width() / size.height().toFloat()
                    aspect
                }.values.filter { it.size > 1 }
            }.join()

            val comparisons = mutableListOf<Comparison>()
            var currentIdx = 0
            var size = imageGroups.sumOf { it.size }

            for (group in imageGroups) {
                compareBatchInternal(group, comparisons) { batchCurrent, batchTotal ->
                    currentIdx++
                    updateCallback(currentIdx, size, "Comparing")
                }
            }
            updateCallback(1, 1, "Loading previews")

            return@coroutineScope comparisons
        }


    private suspend fun compareBatchInternal(
        images: Images,
        comparisons: MutableList<Comparison>,
        updateCallback: (Int, Int) -> Unit
    ) {
        val hashes = hashSetOf<Int>()
        var currentIdx = 0
        var tasksCount = 0

        for ((startPath, startImg) in images) {
            coroutineScope {
                for ((path, img) in images) {
                    if (img == startImg) {
                        continue
                    }

                    var hash = startImg.pairedHash(img)
                    if (hashes.contains(hash)) {
                        println("$path is already checked")
                        continue
                    }

                    while (tasksCount >= MAX_TASKS_COUNT) {
                        yield()
                    }

                    tasksCount++
                    hashes.add(hash)

                    launch {
                        var similarity = calculateSimilarity(startImg, img)
                        val result = Comparison(
                            similarity,
                            startPath,
                            path
                        )

                        comparisons.add(result)
                        tasksCount--
                    }
                }
                currentIdx++
                updateCallback(currentIdx, images.size)
            }
        }
    }

    private fun loadImages(paths: List<String>, callback: (Int, Int) -> Unit): Images {
        val list = ArrayList<Pair<String, Mat>>(paths.size)
        var idx = 0

        for (path in paths) {
            val img = imread(path, IMREAD_COLOR)
            val size = getTargetSize(img)
            resize(img, size)

            if (!img.empty()) {
                list.add(path to img)
            }
            idx++
            callback(idx, paths.size)
        }
        return list
    }

    private fun getTargetSize(mat: Mat): Size {
        val aspect = mat.cols() / mat.rows().toFloat()
        val width = REF_IMAGE_SIZE / aspect
        val height = REF_IMAGE_SIZE * aspect

        return Size(width.toInt(), height.toInt())
    }

    private fun resize(mat: Mat, size: Size) {
        opencv_imgproc.resize(mat, mat, size)
    }

    private fun calculateSimilarity(img1: Mat, img2: Mat): Double {
        val hsv1 = Mat()
        val hsv2 = Mat()
        
        if (img1.channels() > 1) {
            cvtColor(img1, hsv1, COLOR_BGR2HSV)
        } else {
            hsv1.put(img1)
        }

        if (img2.channels() > 1) {
            cvtColor(img2, hsv2, COLOR_BGR2HSV)
        } else {
            hsv2.put(img2)
        }
        
        val images1 = MatVector(hsv1)
        val images2 = MatVector(hsv2)
        
        val histSize = IntPointer(256) 
        val ranges = FloatPointer(0.0f, 256.0f) 
        val channels = IntPointer(0) 
        
        val hist1 = Mat()
        val hist2 = Mat()
        calcHist(images1, channels, Mat(), hist1, histSize, ranges, false)
        calcHist(images2, channels, Mat(), hist2, histSize, ranges, false)
        
        normalize(hist1, hist1)
        normalize(hist2, hist2)
        
        return compareHist(hist1, hist2, HISTCMP_CORREL)
    }
}
