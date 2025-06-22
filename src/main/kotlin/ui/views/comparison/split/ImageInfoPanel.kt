package ui.views.comparison.split


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import utils.clamp
import utils.getFileSize
import utils.showInExplorer
import kotlin.io.path.Path

@Composable
fun ImageInfoPanel(
    path: String,
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
) {
    val resolution = remember(bitmap) { "${bitmap.width} x ${bitmap.height}" }
    val fileSize = remember(path) { getFileSize(path) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resolution: $resolution",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Size: $fileSize",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 4.dp)
                .clipToBounds()
        ) {
            val modifier = Modifier.align(Alignment.Center).run {
                if (textLayoutResult != null) {
                    val delta = textLayoutResult!!.multiParagraph.width - textLayoutResult!!.size.width

                    this.offset(x = -delta.coerceAtLeast(0f).dp / 2f)
                } else {
                    this
                }
            }

            Text(
                text = "${Path(path).fileName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                softWrap = false,
                modifier = modifier,
                onTextLayout = { textLayoutResult = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Button(
                shape = MaterialTheme.shapes.medium,
                onClick = { showDialog = true }
            ) {
                Text(text = "Delete")
            }
            Button(
                shape = MaterialTheme.shapes.medium,
                onClick = { showInExplorer(path) }
            ) {
                Text(
                    text = "Show",
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
        }
    }

    if (showDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                onDelete()
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

