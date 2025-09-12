package ui.views.start

import LocalNavController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import ui.views.start.analyze.AnalyzeSettingsRoute

@Serializable
object StartRoute

@Composable
fun StartView() {
    val navController = LocalNavController.current
    
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(26.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton("Find", Icons.Rounded.Search) {
            
        }

        ActionButton("Analyze", Icons.Rounded.Compare) {
            navController.navigate(AnalyzeSettingsRoute)
        }
    }
}