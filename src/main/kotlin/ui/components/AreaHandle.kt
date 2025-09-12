package ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AreaHandle(
    modifier: Modifier = Modifier,
    onDrag: (Float) -> Unit,
    onPress: () -> Unit,
) {
    val state = rememberDraggableState(onDrag)

    Surface(
        modifier = modifier
            .pointerHoverIcon(PointerIcon.Hand)
            .draggable(
                state = state,
                orientation = Orientation.Horizontal,
            )
            .onPointerEvent(
                eventType = PointerEventType.Press,
                onEvent = { onPress() }
            ),
        color = MaterialTheme.colorScheme.onSecondary,
        shape = MaterialTheme.shapes.medium,
        content = {}
    )
}