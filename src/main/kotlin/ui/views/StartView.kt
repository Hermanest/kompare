package ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView


@Composable
fun StartView(onImport: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select a folder to import images",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Button(
                onClick = {
                    val folderPath = pickFolder()
                    if (folderPath != null) {
                        onImport(folderPath)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .defaultMinSize(minWidth = 150.dp, minHeight = 50.dp)
            ) {
                Text(
                    text = "Import",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

private fun pickFolder(): String? {
    val chooser = JFileChooser(FileSystemView.getFileSystemView())
    chooser.dialogTitle = "Select a Folder"
    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    chooser.isAcceptAllFileFilterUsed = false

    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile.absolutePath
    } else {
        null
    }
}

