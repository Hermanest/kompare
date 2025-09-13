package ui.components

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SliderSetting(
    imageVector: ImageVector,
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0)
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    formatValue: ((Float) -> String)? = null
) {
    Row(
        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.size(30.dp).offset(y = (-4).dp)
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = label)

                if (formatValue != null) {
                    Text(text = formatValue(value))
                }
            }

            Slider(
                value = value,
                enabled = enabled,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                onValueChangeFinished = onValueChangeFinished,
            )
        }
    }
}