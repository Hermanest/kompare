package ui.views.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ui.ActionType
import ui.ComparisonStartData
import ui.components.PathSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartSettingsPanel(
    modifier: Modifier = Modifier,
    actionType: ActionType,
    onCancel: () -> Unit,
    onProceed: (ComparisonStartData) -> Unit
) {
    var startData by remember { mutableStateOf(ComparisonStartData()) }

    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (actionType == ActionType.Find) {
                    PathSelector(false) {
                        startData.filePath = it
                    }
                }
                PathSelector(true) {
                    startData.directoryPath = it
                }

                Card(shape = MaterialTheme.shapes.large) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Base threshold ${(startData.baseThreshold * 100).toInt()}%")
                        Slider(
                            modifier = Modifier.width(300.dp),
                            value = startData.baseThreshold * 100f,
                            onValueChange = { startData.baseThreshold = it / 100f },
                            valueRange = 0f..100f
                        )
                    }
                }
            }

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(onClick = { onProceed(startData) }) {
                    Text("Proceed")
                }
            }
        }
    }
}