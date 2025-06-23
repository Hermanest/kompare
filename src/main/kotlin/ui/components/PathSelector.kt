package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import utils.openFilePicker

@Composable
fun PathSelector(directory: Boolean, onSelect: (String) -> Unit) {
    var path by remember { mutableStateOf("Choose a " + if (directory) "directory" else "file") }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 10.dp,
                top = 6.dp,
                bottom = 6.dp,
                end = 6.dp
            ).width(250.dp)
        ) {
            Text(
                path,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )

            Button(
                onClick = {
                    path = openFilePicker(directory) ?: return@Button
                    onSelect(path)
                },
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AttachFile,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).fillMaxSize()
                )
            }
        }
    }
}