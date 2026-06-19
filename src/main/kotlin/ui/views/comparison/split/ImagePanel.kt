package ui.views.comparison.split

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.utils.BitmapStorage
import ui.utils.getBitmapFromStorage
import ui.views.comparison.models.UiComparisonGroup

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePanel(
    index: Int,
    group: UiComparisonGroup,
    modifier: Modifier = Modifier
) {
    var dialogOpen by remember { mutableStateOf(false) }

    val comp = group.comparisons[index]
    val deletedOrFiltered = comp.isDeleted || comp.isFiltered

    Column(
        modifier = modifier.alpha(if (deletedOrFiltered) 0.5F else 1F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (dialogOpen) {
            var previewIndex by remember(index) { mutableStateOf(index) }

            val previewPath = group.comparisons[previewIndex].path
            val previewBitmap = previewPath.getBitmapFromStorage()

            ExpandedImageView(
                bitmap = previewBitmap,
                path = previewPath,
                onClose = { dialogOpen = false },
                onNext = {
                    if (previewIndex < group.comparisons.size - 1) {
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

        val bitmap = BitmapStorage.getBitmap(comp.path)

        Image(
            bitmap = bitmap,
            modifier = Modifier.onClick(onClick = {
                dialogOpen = true
            }),
            contentDescription = null
        )
        Text(
            text = if (index != 0) "Match: ${comp.percentage}%" else "Main Image",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(2.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        ImageDetailsPanel(
            path = comp.path,
            deleted = comp.isDeleted,
            bitmap = bitmap,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            onDeleteOrRestore = {
                if (!comp.isDeleted) {
                    group.delete(index)
                } else {
                    group.restore(index)
                }
            }
        )
    }
}