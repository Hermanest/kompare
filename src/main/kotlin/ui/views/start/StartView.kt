package ui.views.start

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import core.IComparisonInitData
import utils.lerp

private typealias SettingsPanel = @Composable (onCancel: () -> Unit, onAction: (IComparisonInitData) -> Unit) -> Unit

@Composable
fun StartView(onAction: (IComparisonInitData) -> Unit) {
    var settingsView by remember { mutableStateOf<SettingsPanel?>(null) }
    var settingsShown by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    val onCancel = remember { { settingsShown = false } }

    LaunchedEffect(settingsShown) {
        progress.animateTo(
            if (settingsShown) 1f else 0f,
            animationSpec = tween(300)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton("Find", Icons.Rounded.Search) {
                settingsView = null
                settingsShown = true
            }

            ActionButton("Analyze", Icons.Rounded.Compare) {
                settingsView = { x, y -> AnalyzeSettingsPanel(x, y) }
                settingsShown = true
            }
        }

        if (settingsShown || progress.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(progress.value.lerp(0f, 1f))
                    .offset(x = progress.value.lerp(300f, 0f).dp)
            ) {
                settingsView!!(onCancel, onAction)
            }
        }
    }
}