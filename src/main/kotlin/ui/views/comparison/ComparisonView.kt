package ui.views.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.Comparison
import core.ComparisonGroup
import ui.ComparisonStartData
import ui.views.comparison.split.SplitView

@Composable
fun ComparisonView(
    startData: ComparisonStartData,
    comparisons: List<ComparisonGroup>,
    selectedComparison: ComparisonGroup?,
    onSelectComparison: (ComparisonGroup) -> Unit,
    onDeleteComparison: (ComparisonGroup, Comparison, String) -> Unit
) {
    var filterThreshold by remember { mutableStateOf(startData.baseThreshold) }
    var isParamsDialogOpen by remember { mutableStateOf(false) }

    val relativeComparisons = remember(comparisons) {
        comparisons.associateWith { it.getComparisons() }
    }

    val relativeSelectedComparison = remember(selectedComparison) {
        if (selectedComparison != null) {
            relativeComparisons[selectedComparison]
        } else {
            null
        }
    }

    val filteredComparisons = remember(comparisons, filterThreshold) {
        val threshold = filterThreshold.toDouble()

        relativeComparisons.values
            .map { it.withThreshold(threshold) }
            .filter { it.comparisons.isNotEmpty() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isParamsDialogOpen) {
                ViewParamsDialog(
                    startData = startData,
                    currentThreshold = filterThreshold,
                    onDismiss = { isParamsDialogOpen = false },
                    onApply = { newThreshold ->
                        filterThreshold = newThreshold
                        isParamsDialogOpen = false
                    }
                )
            }
            Text(
                "Results",
                style = MaterialTheme.typography.titleLarge,
            )
            Button(
                onClick = { isParamsDialogOpen = true },
                shape = MaterialTheme.shapes.medium
            ) {
                Text("View Settings")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
        ) {
            if (comparisons.size > 1) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(end = 10.dp)
                ) {
                    LazyColumn {
                        items(filteredComparisons.size) { i ->
                            val comparison = filteredComparisons[i]
                            
                            ComparisonListItem(
                                comparison,
                                isSelected = comparison == relativeSelectedComparison
                            ) {
                                onSelectComparison(comparison.parentGroup)
                            }
                        }
                    }
                }
            }

            if (filteredComparisons.isNotEmpty()) {
                if (relativeSelectedComparison != null) {
                    SplitView(
                        modifier = Modifier.weight(1f),
                        relativeSelectedComparison,
                        //TODO: fix deletion
                        onDelete = { 
                            onDeleteComparison(relativeSelectedComparison.parentGroup, Comparison("", "", 0.0), it) 
                        }
                    )
                }
            } else {
                Text("Nothing to show")
            }
        }
    }
}