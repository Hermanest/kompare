package ui

import androidx.compose.runtime.*
import core.ComparisonGroup
import core.ComparisonProcessor
import core.comparators.SsimComparator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.views.LoadingView
import ui.views.comparison.ComparisonView
import ui.views.start.StartView
import utils.deleteFile
import java.io.File

enum class AppView {
    Start, Loading, Comparison
}

@Composable
fun App() {
    var currentView by remember { mutableStateOf(AppView.Start) }
    val comparisons = remember { mutableStateListOf<ComparisonGroup>() }
    var selectedComparison by remember { mutableStateOf<ComparisonGroup?>(null) }
    var startData by remember { mutableStateOf<ComparisonStartData?>(null) }

    var totalFiles by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }

    val processor = remember { ComparisonProcessor(SsimComparator) }

    fun refreshComparisons() {
        comparisons.sortBy { it.mainPath }

        if (comparisons.size == 1) {
            selectedComparison = comparisons[0]
        }
    }

    when (currentView) {
        AppView.Start -> StartView(
            onAction = { data ->
                startData = data
                currentView = AppView.Loading

                val files = File(data.directoryPath)
                    .listFiles()
                    ?.filter {
                        it.isFile && when (it.extension) {
                            "png", "jpg", "jpeg" -> true
                            else -> false
                        }
                    }
                    ?.map { it.path } ?: emptyList()

                CoroutineScope(Dispatchers.IO).launch {
                    message = "Analyzing"
                    when (data.action) {
                        // Find duplicates for the file
                        ActionType.Find -> {

                        }
                        // Analyze the whole directory
                        ActionType.Analyze -> {
                            val results = processor.compare(
                                files,
                                { stage ->
                                    message = stage
                                },
                                { current, total ->
                                    progress = current
                                    totalFiles = total
                                }
                            )

                            comparisons.addAll(results)
                            refreshComparisons()
                        }
                    }
                    println(comparisons.size)
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
            startData = startData!!,
            comparisons = comparisons,
            selectedComparison = selectedComparison,
            onSelectComparison = { selectedComparison = it },
            onDeleteComparison = { group, path ->
                if (!comparisons.remove(group)) {
                    println("Failed to remove $path?")
                } else {
                    deleteFile(path)
                    println("Deleted $path")

                    val newComparisons = group.comparisons.filter { it.path1 != path && it.path2 != path }
                    
                    if (newComparisons.isNotEmpty()) {
                        val newGroup = ComparisonGroup(newComparisons)
                        comparisons.add(newGroup)

                        // Keep the same main path even it wasn't removed
                        if (path != group.mainPath) {
                            newGroup.setMainPath(group.mainPath)
                        }

                        // Keep the same group if it still has items
                        if (selectedComparison == group) {
                            selectedComparison = if (newComparisons.isEmpty()) null else newGroup
                        }
                    }
                    
                    refreshComparisons()
                }
            }
        )
    }
}