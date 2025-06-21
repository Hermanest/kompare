package ui.views.comparison.split

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.onClick
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import core.ComparisonGroup
import core.RelativeComparisonGroup
import ui.utils.getBitmapFromStorage

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
                    path = path,
                    match = if (index > 0) group.comparisons[index].percentage else -1.0,
                    onDelete = { onDelete(path) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImagePanel(
    path: String,
    match: Double,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    var dialogOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val bitmap = path.getBitmapFromStorage()

        if (dialogOpen) {
            ExpandedViewDialog(bitmap, path) {
                dialogOpen = false
            }
        }
        Image(
            bitmap = bitmap,
            modifier = Modifier.onClick(onClick = {
                dialogOpen = true
            }),
            contentDescription = null
        )
        Text(
            text = if (match >= 0) "Match: $match%" else "Main Image",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        ImageInfoPanel(
            path = path,
            bitmap = bitmap,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            onDelete = onDelete
        )
    }
}