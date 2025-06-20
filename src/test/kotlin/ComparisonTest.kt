import core.ComparisonGroup
import core.ComparisonProcessor
import core.comparators.IImageComparator
import core.comparators.SsimComparator
import kotlinx.coroutines.runBlocking
import nu.pattern.OpenCV
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgcodecs.Imgcodecs.IMREAD_COLOR
import java.io.File
import kotlin.test.Test

class ConsoleProgressTracker {
    private var currentStage: String = ""
    private var lastPrintedLength = 0

    val onStageChange: (String) -> Unit = { stage ->
        currentStage = stage
        printStage()
    }

    val onProgressChange: (Int, Int) -> Unit = { done, total ->
        printProgress(done, total)
    }

    private fun printStage() {
        println("\n📌 Stage: $currentStage")
        System.out.flush()
    }

    private fun printProgress(done: Int, total: Int) {
        val percent = if (total == 0) 100 else (done * 100) / total
        val bar = buildProgressBar(percent)
        val line = "   ⏳ Progress: $bar $done / $total ($percent%)"
        print("\r" + line.padEnd(lastPrintedLength, ' '))
        lastPrintedLength = line.length
        if (done == total) println()
        System.out.flush()
    }

    private fun buildProgressBar(percent: Int, width: Int = 30): String {
        val filled = (percent * width) / 100
        return "[" + "=".repeat(filled) + " ".repeat(width - filled) + "]"
    }
}


class ComparisonTest {
    @Test
    fun basic() {
        val comparator: IImageComparator = SsimComparator
        val processor = ComparisonProcessor(comparator)

        OpenCV.loadShared()
        
        val time = System.currentTimeMillis()
        val paths = getAllPathsInDirectory("YOUR DIRECTORY").filter { path ->
            path.endsWith(".jpg") or path.endsWith(".png") or path.endsWith(".jpeg")
        }
        
        lateinit var result: List<ComparisonGroup>

        runBlocking {
            val tracker = ConsoleProgressTracker()
            
            result = processor.compare(paths, tracker.onStageChange, tracker.onProgressChange)
        }
        
        println("Took: ${System.currentTimeMillis() - time}ms")
    }

    fun getAllPathsInDirectory(dirPath: String): List<String> {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        return dir.listFiles()
            ?.filter { it.isFile }
            ?.map { it.absolutePath }
            ?: emptyList()
    }
}