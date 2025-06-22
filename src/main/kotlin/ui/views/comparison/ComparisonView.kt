package ui.views.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.Comparison
import core.ComparisonGroup
import ui.ComparisonStartData
import ui.views.comparison.split.SplitView
import utils.stableKey

@Composable
fun ComparisonView(
    startData: ComparisonStartData,
    comparisons: List<ComparisonGroup>,
    selectedComparison: ComparisonGroup?,
    onSelectComparison: (ComparisonGroup) -> Unit,
    onDeleteComparison: (ComparisonGroup, String) -> Unit,
    onFinish: () -> Unit
) {
    var filterThreshold by remember { mutableStateOf(startData.baseThreshold) }
    var isParamsDialogOpen by remember { mutableStateOf(false) }

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

            Button(
                onClick = onFinish,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Back")
            }

            Button(
                onClick = { isParamsDialogOpen = true },
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Filters")
            }
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

                if (notEmpty && relativeSelectedComparison != null) {
                    SplitView(
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
                Column(modifier = Modifier.width(300.dp)) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp)
                            .padding(top = 4.dp)
                    ) {
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Showing ${filteredComparisons.size} results out of ${relativeComparisons.size}",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}