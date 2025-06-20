package ui.views.comparison.split

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ui.components.ImageViewer
import ui.components.ImageViewerState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExpandedViewDialog(bitmap: ImageBitmap, path: String, onClose: () -> Unit) {
    val state by remember { mutableStateOf(ImageViewerState()) }

    AlertDialog(
        modifier = Modifier.padding(10.dp),
        onDismissRequest = onClose,
        backgroundColor = Color.Transparent,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        text = {
            ImageViewer(
                modifier = Modifier.padding(bottom = 10.dp),
                bitmap = bitmap,
                state = state
            )
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Image location: $path", 
                    textAlign = TextAlign.Left,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
                TextButton(onClick = onClose) {
                    Text(text = "Close")
                }
            }
        },
        confirmButton = { },
        title = { }
    )
}