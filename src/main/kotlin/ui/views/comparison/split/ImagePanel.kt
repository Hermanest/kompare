package ui.views.comparison.split

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.utils.BitmapStorage
import ui.utils.getBitmapFromStorage
import ui.views.comparison.models.UiComparison
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

    Box(contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
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

        ImagePanelCover(comp)
    }
}

@Composable
fun BoxScope.ImagePanelCover(comp: UiComparison) {
    val (vector, color) = when {
        comp.isDeleted -> Icons.Filled.DeleteForever to Color.Red
        comp.isFiltered -> Icons.Filled.HideImage to Color.Gray
        else -> null to null
    }

    if (vector != null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .alpha(0.85f)
                .matchParentSize()
                .background(color = color!!.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = vector,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    }
}