package ui.views.comparison.split

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.RelativeComparisonGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitView(
    modifier: Modifier = Modifier,
    group: RelativeComparisonGroup,
    onDelete: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(250.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        items(group.size) { index ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                val path = group.comparisons[index].path

                ImagePanel(
                    modifier = Modifier,
                    index = index,
                    group = group,
                    match = if (index > 0) group.comparisons[index].percentage else -1.0,
                    onDelete = { onDelete(path) }
                )
            }
        }
    }
}
