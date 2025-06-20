package core

import core.comparators.IImageComparator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR
import utils.aspect

private typealias GroupedImages = HashMap<Float, ArrayList<Pair<String, Mat>>>

class ComparisonProcessor(private val comparator: IImageComparator) {
    companion object {
        private const val MAX_TASKS_COUNT = 500
    }

    suspend fun compare(
        paths: Collection<String>,
        onStageChange: (stage: String) -> Unit,
        onProgressChange: (done: Int, total: Int) -> Unit
    ): ArrayList<ComparisonGroup> {
        val images = ArrayList<Mat>(paths.size)
        val groupedImages = GroupedImages(paths.size)

        coroutineScope {
            val totalPaths = paths.size
            var currentPath = 0

            onStageChange("Preprocessing")

            for (path in paths) {
                launch {
                    val img = load(path)

                    if (img != null) {
                        images.add(img)
                        putAspectMat(path, img, groupedImages)
                    }

                    currentPath++
                    onProgressChange(currentPath, totalPaths)
                }
            }
        }

        onStageChange("Comparing")
        onProgressChange(0, 0)

        val semaphore = Semaphore(MAX_TASKS_COUNT)
        val results = ArrayList<ComparisonGroup>()

        val totalImages = groupedImages.map { it.value.size }.sum()
        var currentImage = 0

        for ((_, list) in groupedImages) {
            coroutineScope {
                launch {
                    compareBatch(list, results, semaphore) {
                        currentImage++
                        onProgressChange(currentImage, totalImages)
                    }
                }
            }
        }

        return results
    }

    /// Each aspect group creates a separate batch
    private suspend fun compareBatch(
        images: Collection<Pair<String, Mat>>,
        results: MutableCollection<ComparisonGroup>,
        semaphore: Semaphore,
        onProgressIncrease: () -> Unit
    ) = coroutineScope {
        val imageList = images.toList()

        for (i in imageList.indices) {
            semaphore.acquire()

            launch {
                val (basePath, baseImage) = imageList[i]

                val groupMatches = mutableListOf<Comparison>()

                for (j in i + 1 until imageList.size) {
                    val (comparePath, compareImage) = imageList[j]

                    val similarity = comparator.compare(baseImage, compareImage)
                    val comparison = Comparison(comparePath, similarity)

                    groupMatches.add(comparison)
                }

                if (groupMatches.isNotEmpty()) {
                    results.add(ComparisonGroup(basePath, groupMatches))
                }

                onProgressIncrease()
            }.invokeOnCompletion {
                semaphore.release()
            }
        }
    }

    private fun putAspectMat(path: String, mat: Mat, grouped: GroupedImages) {
        val list = grouped.getOrPut(mat.aspect) { ArrayList() }

        list.add(path to mat)
    }

    private fun load(path: String): Mat? {
        return try {
            val image = Imgcodecs.imread(path, IMREAD_COLOR)
            comparator.preprocessor?.preprocess(image)

            image
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}