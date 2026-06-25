package ui.views.comparison

import LocalNavController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import ui.views.comparison.models.UiComparisonGroup
import ui.views.comparison.models.UiComparisonsList
import ui.views.comparison.models.UiGroupFilter
import ui.views.comparison.split.GroupView
import ui.views.start.StartRoute

@Serializable
object ComparisonRoute

@Composable
fun ComparisonView(comparisonsList: UiComparisonsList) {
    val navController = LocalNavController.current

    var selectedGroup by remember { mutableStateOf<UiComparisonGroup?>(null) }
    var filtersOpened by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(UiGroupFilter(comparisonsList.groupingThreshold, null)) }

    Column(modifier = Modifier.fillMaxSize()) {
        var listWidth by remember { mutableStateOf(300f) }
        // This approach allows to eliminate cases when cursor goes
        // far away and then starts moving back, increasing the size
        val actualListWidth = listWidth.coerceAtLeast(300f)

        ComparisonViewToolbar(
            listWidth = actualListWidth.dp,
            viewerActive = selectedGroup != null,
            onBack = {
                navController.popBackStack(StartRoute, false)
            },
            onListWidthChange = {
                listWidth -= it
            },
            onListWidthStartedToChange = {
                // Once the pointer is released, we reset achieved width to the actual width
                // so the cursor will start moving the handle immediately next time we drag
                listWidth = actualListWidth
            },
            onSweepDelete = {
                selectedGroup?.deleteSweep()
            },
            onOpenSettings = {
                filtersOpened = true
            },
            onSearch = {
                filter = filter.copy(filterPhrase = it)
            }
        )

        if (filtersOpened) {
            FiltersDialog(
                filter = filter,
                onDismiss = {
                    filtersOpened = false
                },
                onApply = {
                    filter = it
                    filtersOpened = false
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val notEmpty = comparisonsList.isNotEmpty()

                if (notEmpty && selectedGroup != null) {
                    GroupView(
                        modifier = Modifier.fillMaxSize(),
                        group = selectedGroup!!
                    )
                } else {
                    Text(
                        text = if (notEmpty) "Select something fist" else "Nothing to show",
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (comparisonsList.totalSize > 0) {
                ComparisonList(
                    listWidth = actualListWidth.dp,
                    comparisons = comparisonsList,
                    selectedComparison = selectedGroup,
                    onSelectComparison = { selectedGroup = it }
                )
            }
        }
    }
}