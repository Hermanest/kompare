package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DropdownSelectorMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    options: List<Pair<String, ImageVector?>>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable () -> Unit)? = null
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier
        ) {
            Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                options.forEachIndexed { index, option ->
                    val isSelected = option.first == selectedOption

                    DropdownSelectorMenuItem(
                        option = option,
                        toggleable = false,
                        active = isSelected,
                        onClick = {
                            onOptionSelected(option.first)
                            onDismissRequest()
                        }
                    )

                    if (index < options.lastIndex) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (bottomContent != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        bottomContent()
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownSelectorMenuItem(
    option: Pair<String, ImageVector?>,
    toggleable: Boolean,
    active: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        active && !toggleable -> MaterialTheme.colorScheme.primary
        active && toggleable -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

    val tintColor = when {
        active && !toggleable -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (option.second != null) {
                    Icon(
                        imageVector = option.second!!,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = tintColor
                    )
                }

                Text(
                    text = option.first,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tintColor,
                    modifier = Modifier.weight(1f)
                )

                if (toggleable) {
                    Box(
                        modifier = Modifier.size(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (active) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Checked",
                                modifier = Modifier.size(16.dp),
                                tint = tintColor
                            )
                        }
                    }
                }
            }
        },
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
    )
}