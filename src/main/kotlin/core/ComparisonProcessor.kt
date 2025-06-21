package core

import core.comparators.IImageComparator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR
import utils.aspect
import kotlin.collections.forEach

private typealias GroupedImages = HashMap<Float, BatchedImages>
private typealias BatchedImages = ArrayList<Pair<String, Mat>>

class ComparisonProcessor(private val comparator: IImageComparator) {
    companion object {
        private const val MAX_TASKS_COUNT = 500
    }

    suspend fun compare(
        paths: Collection<String>,
        onStageChange: (stage: String) -> Unit,
        onProgressChange: (done: Int, total: Int) -> Unit
    ): MutableList<ComparisonGroup> {
        val groupedImages = GroupedImages(paths.size)

        coroutineScope {
            val totalPaths = paths.size
            var currentPath = 0

            onStageChange("Preprocessing")

            for (path in paths) {
                launch {
                    val img = load(path)

                    if (img != null) {
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
        val comparisons = ArrayList<Comparison>()

        val totalImages = groupedImages.map { it.value.size }.sum()
        var currentImage = 0

        for ((_, images) in groupedImages) {
            coroutineScope {
                launch {
                    compareBatch(images, comparisons, semaphore) {
                        currentImage++
                        onProgressChange(currentImage, totalImages)
                    }
                }
            }
        }

        onStageChange("Grouping")
        onProgressChange(1, 1)

        return coroutineScope {
            async {
                groupComparisons(comparisons)
            }.await()
        }
    }

    /// Each aspect group creates a separate batch
    private suspend fun compareBatch(
        images: BatchedImages,
        results: MutableCollection<Comparison>,
        semaphore: Semaphore,
        onProgressIncrease: () -> Unit
    ) = coroutineScope {
        for (i in images.indices) {
            semaphore.acquire()

            launch {
                val (basePath, baseImage) = images[i]

                for (j in i + 1 until images.size) {
                    val (comparePath, compareImage) = images[j]
                    val similarity = comparator.compare(baseImage, compareImage)

                    if (similarity >= 0.5) {
                        results.add(Comparison(basePath, comparePath, similarity))
                    }
                }

                onProgressIncrease()
            }.invokeOnCompletion {
                semaphore.release()
            }
        }
    }

    fun groupComparisons(comparisons: List<Comparison>): MutableList<ComparisonGroup> {
        val visited = mutableSetOf<String>()
        val adjacency = mutableMapOf<String, MutableSet<String>>()

        // Build adjacency list
        for (comp in comparisons) {
            adjacency.computeIfAbsent(comp.path1) { mutableSetOf() }.add(comp.path2)
            adjacency.computeIfAbsent(comp.path2) { mutableSetOf() }.add(comp.path1)
        }

        // Build lookup map for fast comparison retrieval
        val comparisonLookup = comparisons.associateBy {
            it.path1 to it.path2
        } + comparisons.associateBy {
            it.path2 to it.path1
        }

        val groups = mutableListOf<ComparisonGroup>()

        for (start in adjacency.keys) {
            if (start in visited) continue

            val groupPaths = mutableSetOf<String>()
            val queue = ArrayDeque<String>()
            queue.add(start)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (current in visited) continue

                visited.add(current)
                groupPaths.add(current)

                adjacency[current]?.forEach { neighbor ->
                    if (neighbor !in visited) {
                        queue.add(neighbor)
                    }
                }
            }

            // Collect comparisons only between members of the group
            val groupComparisons = mutableListOf<Comparison>()
            val pathList = groupPaths.toList()

            for (i in pathList.indices) {
                for (j in i + 1 until pathList.size) {
                    val comp = comparisonLookup[pathList[i] to pathList[j]]
                    if (comp != null) {
                        groupComparisons.add(comp)
                    }
                }
            }

            if (groupComparisons.isNotEmpty()) {
                groups.add(ComparisonGroup(groupComparisons))
            }
        }

        return groups
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