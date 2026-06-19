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
import ui.views.comparison.models.UiComparisonGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupView(
    modifier: Modifier = Modifier,
    group: UiComparisonGroup
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(250.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        items(group.comparisons.size) { index ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                ImagePanel(
                    modifier = Modifier,
                    index = index,
                    group = group
                )
            }
        }
    }
}
