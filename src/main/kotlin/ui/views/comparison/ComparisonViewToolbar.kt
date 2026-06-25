package ui.views.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.components.AreaHandle
import ui.components.ButtonTextField
import ui.components.DropdownSelectorMenu
import ui.components.DropdownSelectorMenuItem

@Composable
fun ComparisonViewToolbar(
    listWidth: Dp,
    viewerActive: Boolean,
    onBack: () -> Unit,
    onListWidthChange: (Float) -> Unit,
    onListWidthStartedToChange: () -> Unit,
    onSweepDelete: () -> Unit,
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
            ToolbarButton(
                imageVector = Icons.Filled.DeleteSweep,
                enabled = viewerActive,
                onClick = onSweepDelete
            )

            var keepAnchorFirst by remember { mutableStateOf(false) }

            ToolbarDropdown(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                options = listOf(
                    "Similarity" to Icons.Filled.Percent,
                    "Alphabet" to Icons.Filled.Abc
                ),
                onOptionSelected = {},
                bottomContent = {
                    DropdownSelectorMenuItem(
                        option = "Keep anchor first" to Icons.Default.Anchor,
                        toggleable = true,
                        active = keepAnchorFirst,
                        onClick = { keepAnchorFirst = !keepAnchorFirst }
                    )
                }
            )

            ToolbarDropdown(
                imageVector = Icons.Filled.SortByAlpha,
                options = listOf(
                    "Ascending" to Icons.Filled.ArrowUpward,
                    "Descending" to Icons.Filled.ArrowDownward
                ),
                onOptionSelected = {}
            )
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

@Composable
fun ToolbarDropdown(
    imageVector: ImageVector,
    options: List<Pair<String, ImageVector?>>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable () -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options.firstOrNull()?.first ?: "") }

    Box(modifier = modifier) {
        ToolbarButton(
            imageVector = imageVector,
            onClick = { expanded = true }
        )

        DropdownSelectorMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = {
                selectedOption = it
                onOptionSelected(it)
            },
            bottomContent = bottomContent
        )
    }
}

@Composable
fun ToolbarButton(
    imageVector: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: (@Composable () -> Unit)? = null
) {
    FilledIconButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        enabled = enabled,
    ) {
        if (content == null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        } else {
            Box {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null
                )

                content
            }
        }
    }
}