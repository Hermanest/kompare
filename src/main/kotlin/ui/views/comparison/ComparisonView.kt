package ui.views.comparison

import LocalNavController
import LocalProcessorProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.AnalyzeResult
import core.ComparisonGroup
import kotlinx.serialization.Serializable
import ui.views.comparison.split.GroupView
import ui.views.start.StartRoute
import ui.views.start.StartView
import utils.stableKey

@Serializable
object ComparisonRoute

@Composable
fun ComparisonView(
    onDeleteComparison: (ComparisonGroup, String) -> Unit
) {
    val navController = LocalNavController.current
    val result = LocalProcessorProvider.current.processor.result as AnalyzeResult

    val comparisons = result.results
    var selectedComparison by remember { mutableStateOf<ComparisonGroup?>(null) }
    var filterThreshold by remember { mutableStateOf(0.5f) }
    val comparisonsKey = comparisons.stableKey()

    val relativeComparisons = remember(comparisonsKey) {
        comparisons.associateWith { it.getComparisons() }
    }

    val relativeSelectedComparison = remember(selectedComparison, comparisonsKey) {
        if (selectedComparison != null) {
            relativeComparisons[selectedComparison]
        } else {
            null
        }
    }

    val filteredComparisons = remember(comparisonsKey, filterThreshold) {
        val threshold = filterThreshold.toDouble()

        relativeComparisons.values
            .map { it.withThreshold(threshold) }
            .filter { it.otherComparisons.isNotEmpty() }
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
            }
        )

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

                if (notEmpty && relativeSelectedComparison != null) {
                    GroupView(
                        modifier = Modifier.fillMaxSize(),
                        relativeSelectedComparison,
                        onDelete = {
                            onDeleteComparison(relativeSelectedComparison.parentGroup, it)
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
                    unfilteredComparisonsSize = relativeComparisons.size,
                    selectedComparison = relativeSelectedComparison,
                    onSelectComparison = { selectedComparison = it.parentGroup }
                )
            }
        }
    }
}