package ui.views.comparison

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.views.comparison.models.UiComparisonGroup
import ui.views.comparison.models.UiComparisonsList

@Composable
fun ComparisonList(
    listWidth: Dp,
    comparisons: UiComparisonsList,
    selectedComparison: UiComparisonGroup?,
    onSelectComparison: (UiComparisonGroup) -> Unit,
) {
    Column(modifier = Modifier.width(listWidth)) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp, end = 10.dp)
        ) {
            items(comparisons.size) { i ->
                val comparison = comparisons[i]

                ComparisonListItem(
                    comparison,
                    isSelected = comparison == selectedComparison
                ) {
                    onSelectComparison(comparison)
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
                text = "Showing ${comparisons.size} results out of ${comparisons.totalSize}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}