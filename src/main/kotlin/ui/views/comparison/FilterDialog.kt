package ui.views.comparison

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.views.comparison.models.UiGroupFilter
import kotlin.math.round

@Composable
fun FiltersDialog(
    filter: UiGroupFilter,
    onDismiss: () -> Unit,
    onApply: (UiGroupFilter) -> Unit
) {
    var sliderValue by remember { mutableStateOf(filter.filterOffThreshold * 100) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Filters") },
        backgroundColor = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.large,
        text = {
            Column {
                Text(
                    text = "Min. Similarity: ${round(sliderValue * 100f) / 100f}%",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = (filter.filterOffThreshold * 100f)..100f
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    filter.copy(filterOffThreshold = sliderValue)
                )
            }) {
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
