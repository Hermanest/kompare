package ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import utils.times

@Composable
fun ButtonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search...",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused)
            MaterialTheme.colorScheme.inversePrimary * 0.5f else
            MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 200)
    )

    val textColor = if (isFocused)
        MaterialTheme.colorScheme.onSurface else
        MaterialTheme.colorScheme.onPrimary

    val placeholderColor = textColor.copy(alpha = 0.5f)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = TextStyle(
            color = textColor,
            fontSize = 16.sp
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(backgroundColor, MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = placeholderColor
                        )
                    }

                    innerTextField()
                }
            }
        },
        modifier = modifier.height(50.dp)
    )
}