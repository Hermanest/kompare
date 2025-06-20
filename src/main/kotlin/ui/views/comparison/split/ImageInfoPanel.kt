package ui.views.comparison.split


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import utils.getFileSize
import utils.showInExplorer

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

