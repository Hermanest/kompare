package ui.views.comparison.split

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import core.RelativeComparisonGroup
import ui.utils.getBitmapFromStorage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePanel(
    index: Int,
    group: RelativeComparisonGroup,
    match: Double,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    var dialogOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        if (dialogOpen) {
            var previewIndex by remember(index) { mutableStateOf(index) }

            val previewPath = group.combinedComparisons[previewIndex].path
            val previewBitmap = previewPath.getBitmapFromStorage()

            ExpandedViewDialog(
                bitmap = previewBitmap,
                path = previewPath,
                onClose = { dialogOpen = false },
                onNext = {
                    if (previewIndex < group.combinedComparisons.size - 1) {
                        previewIndex++
                    }
                },
                onPrev = {
                    if (previewIndex > 0) {
                        previewIndex--
                    }
                }
            )
        }

        val path = group.combinedComparisons[index].path
        val bitmap = path.getBitmapFromStorage()

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