package core.processors

import core.*
import core.comparators.IImageComparator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR
import utils.aspect
import java.io.File

private typealias GroupedImages = HashMap<Float, BatchedImages>
private typealias BatchedImages = ArrayList<Pair<String, Mat>>

class AnalyzeComparisonProcessor(
    private val data: AnalyzeInitData,
    private val comparator: IImageComparator
) : IComparisonProcessor {
    companion object {
        private const val MAX_TASKS_COUNT = 500
    }

    override val total: StateFlow<Int> get() = _totalProgress
    override val handled: StateFlow<Int> get() = _handledProgress
    override val stage: StateFlow<String> get() = _stage
    override val result: IComparisonResult? get() = _result

    private val _totalProgress = MutableStateFlow(0)
    private val _handledProgress = MutableStateFlow(0)
    private val _stage = MutableStateFlow("")
    private var _result: IComparisonResult? = null

    private var _job: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun start() {
        _job = GlobalScope.launch {
            val groups = compare()
            _result = AnalyzeResult(groups)
        }
    }

    override suspend fun join() {
        _job?.join()
    }

    private suspend fun compare(): MutableList<ComparisonGroup> {
        val paths = getImagePaths(data.directoryPath)
        val groupedImages = GroupedImages(paths.size)

        coroutineScope {
            val mutex = Mutex()

            _stage.value = "Preprocessing"
            _totalProgress.value = paths.size

            for (path in paths) {
                launch {
                    val img = loadImage(path)

                    // Awaiting mutation usually takes some time so we update the ui first
                    _handledProgress.value++

                    if (img != null) {
                        mutex.withLock {
                            groupedImages
                                .getOrPut(img.aspect) { ArrayList() }
                                .add(path to img)
                        }
                    }
                }
            }
        }

        _stage.value = "Comparing"
        _totalProgress.value = groupedImages.map { it.value.size }.sum()
        _handledProgress.value = 0

        val semaphore = Semaphore(MAX_TASKS_COUNT)
        val comparisons = ArrayList<Comparison>()

        for ((_, images) in groupedImages) {
            coroutineScope {
                launch {
                    compareBatch(images, comparisons, semaphore) {
                        _handledProgress.value++
                    }
                }
            }
        }

        _stage.value = "Grouping"
        _totalProgress.value = 0
        _handledProgress.value = 0

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

    private fun groupComparisons(comparisons: List<Comparison>): MutableList<ComparisonGroup> {
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

    private fun loadImage(path: String): Mat? {
        return try {
            val image = Imgcodecs.imread(path, IMREAD_COLOR)
            comparator.preprocessor?.preprocess(image)

            image
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getImagePaths(path: String): List<String> {
        return File(data.directoryPath)
            .listFiles()
            ?.filter {
                it.isFile && when (it.extension) {
                    "png", "jpg", "jpeg" -> true
                    else -> false
                }
            }
            ?.map { it.path } ?: emptyList()
    }
}