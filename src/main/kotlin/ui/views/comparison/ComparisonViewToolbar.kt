package ui.views.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.components.AreaHandle
import ui.components.ButtonTextField

@Composable
fun ComparisonViewToolbar(
    listWidth: Dp,
    onBack: () -> Unit,
    onListWidthChange: (Float) -> Unit,
    onListWidthStartedToChange: () -> Unit,
    onSweepDelete: () -> Unit,
    onOpenSettings: () -> Unit,
    onSearch: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(vertical = 8.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onBack,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text("Back")
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
        ) {
            // Custom button
            @Composable
            fun ToolbarButton(imageVector: ImageVector, onClick: () -> Unit) {
                FilledIconButton(
                    onClick = onClick,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null
                    )
                }
            }

            ToolbarButton(Icons.Filled.DeleteSweep) {
                onSweepDelete()
            }
            ToolbarButton(Icons.Filled.Save) {
                
            }
            ToolbarButton(Icons.Filled.Settings) {
                onOpenSettings()
            }
        }

        // Draggable handle to change the list size
        AreaHandle(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(6.dp)
                .fillMaxHeight(),
            onDrag = { onListWidthChange(it / 2f) },
            onPress = onListWidthStartedToChange,
        )

        Row(
            modifier = Modifier
                .width(listWidth)
                .padding(end = 12.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text by remember { mutableStateOf("") }

            ButtonTextField(
                value = text,
                onValueChange = {
                    text = it
                    onSearch(it)
                },
                icon = Icons.Default.Search
            )
        }
    }
}