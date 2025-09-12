package ui

import LocalComparatorFactory
import androidx.compose.runtime.*
import core.AnalyzeResult
import core.ComparisonGroup
import core.FindMatchResult
import core.IComparisonProcessor
import core.IComparisonResult
import kotlinx.coroutines.launch
import ui.views.LoadingView
import ui.views.comparison.ComparisonView
import ui.views.start.StartView
import utils.deleteFile

enum class AppView {
    Start, Loading, Comparison
}

@Composable
fun App() {
    var currentView by remember { mutableStateOf(AppView.Start) }
    val comparisons = remember { mutableStateListOf<ComparisonGroup>() }
    var selectedComparison by remember { mutableStateOf<ComparisonGroup?>(null) }

    val comparatorFactory = LocalComparatorFactory.current
    var comparisonProcessor by remember { mutableStateOf<IComparisonProcessor?>(null) }
    
    val coroutineScope = rememberCoroutineScope()
    
    fun refreshComparisons() {
        comparisons.sortBy { it.mainPath }

        if (comparisons.size == 1) {
            selectedComparison = comparisons[0]
        }
    }

    when (currentView) {
        AppView.Start -> StartView(
            onAction = { data ->
                comparisonProcessor = comparatorFactory.createProcessor(data)

                coroutineScope.launch {
                    currentView = AppView.Loading
                    
                    comparisonProcessor!!.start()
                    comparisonProcessor!!.join()
                    
                    comparisons.clear()
                    
                    val result = comparisonProcessor!!.result
                    when (result) {
                        is AnalyzeResult -> comparisons.addAll(result.results)
                        is FindMatchResult -> comparisons.addAll(result.results)
                        null -> {}
                    }

                    currentView = AppView.Comparison
                }
            }
        )

        AppView.Loading -> LoadingView(
            processor = comparisonProcessor!!,
        )

        AppView.Comparison -> ComparisonView(
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
            },
            onFinish = {
                currentView = AppView.Start
            }
        )
    }
}