package ui.views.comparison.split

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import core.Comparison
import ui.components.ImageViewer
import ui.components.ImageViewerState
import ui.components.rememberImageViewerState
import ui.utils.getBitmapFromStorage

@Composable
fun SplitView(
    modifier: Modifier = Modifier,
    comparison: Comparison,
    onDelete: (String) -> Unit,
) {
    var imageViewerState1 by rememberImageViewerState()
    var imageViewerState2 by rememberImageViewerState()
    var cachedComparison by remember { mutableStateOf(comparison) }

    var syncImages by remember { mutableStateOf(true) }
    var resetOnSelect by remember { mutableStateOf(true) }

    if (cachedComparison != comparison) {
        cachedComparison = comparison
        if (resetOnSelect) {
            imageViewerState1.reset()
            imageViewerState2.reset()
        }
    }

    LaunchedEffect(Unit) {
        imageViewerState2 = imageViewerState1
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.weight(1f).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            ImagePanel(
                modifier = Modifier.weight(1f),
                path = comparison.path1,
                imageViewerState = imageViewerState1,
                onDelete = { onDelete(comparison.path1) }
            )

            ImagePanel(
                modifier = Modifier.weight(1f),
                path = comparison.path2,
                imageViewerState = imageViewerState2,
                onDelete = { onDelete(comparison.path2) }
            )
        }

        Surface(
            modifier = Modifier.height(100.dp).fillMaxWidth().padding(8.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            shape = MaterialTheme.shapes.medium,
                            onClick = {
                                syncImages = !syncImages
                                imageViewerState2 = if (!syncImages) {
                                    ImageViewerState()
                                } else {
                                    imageViewerState1
                                }
                            }
                        ) {
                            Text(if (syncImages) "Unsync Images" else "Sync Images")
                        }
                        Button(
                            shape = MaterialTheme.shapes.medium,
                            onClick = { resetOnSelect = !resetOnSelect }
                        ) {
                            Text(if (resetOnSelect) "Don't Reset on Select" else "Reset on Select")
                        }
                    }
                }

                Text(
                    text = "Match ${comparison.percentage()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(end = 16.dp),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
private fun ImagePanel(
    path: String,
    imageViewerState: ImageViewerState,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    Column(modifier = modifier) {
        val bitmap = path.getBitmapFromStorage()
        ImageViewer(
            bitmap = bitmap,
            modifier = Modifier.weight(1f),
            state = imageViewerState
        )
        ImageInfoPanel(
            path = path,
            bitmap = bitmap,
            modifier = Modifier.padding(top = 8.dp),
            onDelete = onDelete
        )
    }
}