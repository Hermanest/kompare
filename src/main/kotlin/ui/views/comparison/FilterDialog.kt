package ui.views.comparison

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterDialog(
    currentThreshold: Float,
    onDismiss: () -> Unit,
    onApply: (Float) -> Unit
) {
    var sliderValue by remember { mutableStateOf(currentThreshold * 100) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Filters") },
        backgroundColor = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.large,
        text = {
            Column {
                Text(
                    text = "Comparison Threshold: ${sliderValue.toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 0f..100f
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(sliderValue / 100) }) {
                Text(text = "Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}
