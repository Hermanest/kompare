package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import core.Comparison
import core.ImageComparator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.views.comparison.ComparisonView
import ui.views.LoadingView
import ui.views.StartView
import utils.deleteFile
import java.io.File

enum class AppView {
    Start, Loading, Comparison
}

@Composable
fun App() {
    var currentView by remember { mutableStateOf(AppView.Start) }
    var comparisons by remember { mutableStateOf(mutableListOf<Comparison>()) }
    var selectedComparison by remember { mutableStateOf<Comparison?>(null) }

    var totalFiles by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }

    when (currentView) {
        AppView.Start -> StartView(
            onImport = {
                currentView = AppView.Loading

                val files = File(it).listFiles()?.filter {
                    it.isFile && when (it.extension) {
                        "png", "jpg", "jpeg" -> true
                        else -> false
                    }
                } ?: emptyList()

                totalFiles = 0
                progress = 0
                message = "Idling"

                CoroutineScope(Dispatchers.IO).launch {
                    val paths = files.map { it.absolutePath }
                    comparisons = ImageComparator.compare(paths) { current, total, msg ->
                        progress = current
                        totalFiles = total
                        message = msg
                        println("$current/$total")
                    }
                    currentView = AppView.Comparison
                }
            }
        )

        AppView.Loading -> LoadingView(
            progress,
            totalFiles,
            message
        )

        AppView.Comparison -> ComparisonView(
            comparisons = comparisons,
            selectedComparison = selectedComparison,
            onSelectComparison = { selectedComparison = it },
            onDeleteComparison = { comp, path ->
                deleteFile(path)
                println("Deleted $path")
                
                comparisons.remove(comp)
                if (selectedComparison == comp) {
                    selectedComparison = null
                }
            }
        )
    }
}