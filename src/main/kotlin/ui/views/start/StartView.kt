package ui.views.start

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import ui.ActionType
import ui.ComparisonStartData
import utils.lerp


@Composable
fun StartView(onAction: (ComparisonStartData) -> Unit) {
    var action by remember { mutableStateOf(ActionType.Find) }
    var settingsShown by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

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
            ActionButton("Find", "attach.svg") {
                action = ActionType.Find
                settingsShown = true
            }

            ActionButton("Analyze", "compare.svg") {
                action = ActionType.Analyze
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
                StartSettingsPanel(
                    modifier = Modifier.fillMaxSize(),
                    actionType = action,
                    onCancel = { settingsShown = false },
                    onProceed = {
                        it.action = action
                        onAction(it)
                        settingsShown = false
                    }
                )
            }
        }
    }
}