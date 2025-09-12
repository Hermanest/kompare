package ui.views.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import core.AnalyzeInitData
import core.IComparisonInitData
import ui.components.PathSelector

@Composable
fun AnalyzeSettingsPanel(onCancel: () -> Unit, onProceed: (IComparisonInitData) -> Unit) {
    var directoryPath by remember { mutableStateOf<String?>(null) }
    var filterOffThreshold by remember { mutableStateOf(50f) }

    val canProceed = directoryPath != null

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PathSelector(true) {
                    directoryPath = it
                }

                Card(shape = MaterialTheme.shapes.large) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Base threshold ${filterOffThreshold.toInt()}%")
                        Slider(
                            modifier = Modifier.width(300.dp),
                            value = filterOffThreshold,
                            onValueChange = { filterOffThreshold = it / 100f },
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

                Button(
                    onClick = {
                        val data = AnalyzeInitData(
                            directoryPath!!,
                            filterOffThreshold
                        )
                        
                        onProceed(data)
                    },
                    enabled = canProceed
                ) {
                    Text("Proceed")
                }
            }
        }
    }
}