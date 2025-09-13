package ui.views.start.analyze

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.ImageAspectRatio
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.AnalyzeInitData
import core.IComparisonInitData
import ui.components.PathSelector
import ui.components.SliderSetting
import kotlin.math.round

@Composable
fun AnalyzeSettingsPanel(onCancel: () -> Unit, onProceed: (IComparisonInitData) -> Unit) {
    var directoryPath by remember { mutableStateOf<String?>(null) }
    var filterOffThreshold by remember { mutableStateOf(50f) }
    var imageResolution by remember { mutableStateOf(128f) }

    val canProceed = directoryPath != null

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.width(400.dp)
            ) {
                PathSelector {
                    directoryPath = it
                }

                Column(modifier = Modifier.padding(top = 10.dp)) {
                    SliderSetting(
                        imageVector = Icons.Filled.Filter,
                        label = "Comparison threshold",
                        formatValue = { "${it.toInt()}%" },
                        value = filterOffThreshold,
                        onValueChange = { filterOffThreshold = it },
                        valueRange = 0f..100f
                    )

                    SliderSetting(
                        imageVector = Icons.Filled.ImageAspectRatio,
                        label = "Image resolution",
                        value = imageResolution,
                        formatValue = { "${it}px" },
                        onValueChange = { imageResolution = round(it) },
                        valueRange = 128f..1024f,
                        steps = (1024 - 128) / 64 - 1
                    )
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
                            filterOffThreshold / 100f
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