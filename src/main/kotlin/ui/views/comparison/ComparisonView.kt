package ui.views.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.Comparison
import ui.views.comparison.split.SplitView


@Composable
fun ComparisonView(
    comparisons: List<Comparison>,
    selectedComparison: Comparison?,
    onSelectComparison: (Comparison) -> Unit,
    onDeleteComparison: (Comparison, String) -> Unit
) {
    var filterThreshold by remember { mutableStateOf(0.9f) }
    var isFilterDialogOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp)
        ) {
            FilterPanel(modifier = Modifier.width(300.dp)) {
                isFilterDialogOpen = true
            }
            
            if (isFilterDialogOpen) {
                FilterDialog(
                    currentThreshold = filterThreshold,
                    onDismiss = { isFilterDialogOpen = false },
                    onApply = { newThreshold ->
                        filterThreshold = newThreshold
                        isFilterDialogOpen = false
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .padding(end = 10.dp)
            ) {
                LazyColumn {
                    val filteredComparisons = comparisons.filter { it.similarity >= filterThreshold }
                    items(filteredComparisons.size) { i ->
                        val comparison = filteredComparisons[i]
                        ComparisonListItem(
                            comparison,
                            isSelected = comparison == selectedComparison
                        ) {
                            onSelectComparison(comparison)
                        }
                    }
                }
            }

            if (selectedComparison != null) {
                SplitView(
                    modifier = Modifier.weight(1f),
                    selectedComparison,
                    onDelete = { onDeleteComparison(selectedComparison, it) }
                )
            }
        }
    }
}