package ui.views.comparison

import LocalFileManager
import LocalNavController
import LocalProcessorProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.AnalyzeInitData
import core.AnalyzeResult
import core.ComparisonGroup
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ui.views.comparison.split.GroupView
import ui.views.start.StartRoute

@Serializable
object ComparisonRoute

@Composable
fun ComparisonView() {
    val navController = LocalNavController.current
    val fileManager = LocalFileManager.current
    val processor = LocalProcessorProvider.current.processor

    val result = processor.result as AnalyzeResult
    val initData = processor.initData as AnalyzeInitData
    val comparisons = result.results

    var selectedComparison by remember { mutableStateOf<ComparisonGroup?>(null) }
    val filteredComparisons = remember { mutableStateListOf<ComparisonGroup>() }

    var filterOffThreshold by remember { mutableStateOf(initData.filterOffThreshold) }
    var filterText by remember { mutableStateOf("") }

    var settingsOpened by remember { mutableStateOf(false) }

    // TODO: create a shared data source
    LaunchedEffect(comparisons, filterOffThreshold, filterText) {
        // Notifies the collection only once at the end of scope
        Snapshot.withoutReadObservation {
            filteredComparisons.clear()

            comparisons.forEach {
                it.relative.filterBy(
                    threshold = filterOffThreshold,
                    phrase = filterText
                )

                if (it.relative.combinedComparisons.isNotEmpty()) {
                    filteredComparisons.add(it)
                }
            }
        }

        if (selectedComparison?.relative?.combinedComparisons?.isEmpty() ?: false) {
            selectedComparison = null
        }

        launch {
            result.onGroupRemoved.collect {
                filteredComparisons.remove(it)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        var listWidth by remember { mutableStateOf(300f) }
        // This approach allows to eliminate cases when cursor goes
        // far away and then starts moving back, increasing the size
        val actualListWidth = listWidth.coerceAtLeast(300f)

        ComparisonViewToolbar(
            listWidth = actualListWidth.dp,
            onBack = {
                navController.popBackStack(StartRoute, false)
            },
            onListWidthChange = {
                listWidth -= it
            },
            onListWidthStartedToChange = {
                // Once the pointer is released, we reset achieved width to the actual width
                // so the cursor will start moving the handle immediately next time we drag
                listWidth = actualListWidth
            },
            onSweepDelete = {

            },
            onOpenSettings = {
                settingsOpened = true
            },
            onSearch = {
                filterText = it
            }
        )

        if (settingsOpened) {
            FiltersDialog(
                initData = initData,
                currentThreshold = filterOffThreshold,
                onDismiss = {
                    settingsOpened = false
                },
                onApply = {
                    filterOffThreshold = it
                    settingsOpened = false
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val notEmpty = filteredComparisons.isNotEmpty()

                if (notEmpty && selectedComparison != null) {
                    GroupView(
                        modifier = Modifier.fillMaxSize(),
                        selectedComparison!!.relative,
                        onDelete = {
                            //onDeleteComparison(relativeSelectedComparison.parentGroup, it)
                        }
                    )
                } else {
                    Text(
                        text = if (notEmpty) "Select something fist" else "Nothing to show",
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (comparisons.size > 1) {
                ComparisonList(
                    listWidth = actualListWidth.dp,
                    comparisons = filteredComparisons,
                    unfilteredComparisonsSize = comparisons.size,
                    selectedComparison = selectedComparison,
                    onSelectComparison = { selectedComparison = it }
                )
            }
        }
    }
}