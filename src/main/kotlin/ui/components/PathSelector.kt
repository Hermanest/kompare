package ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import platform.platform

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PathSelector(
    borderStroke: BorderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
    onSelect: (String) -> Unit
) {
    var path by remember { mutableStateOf("Choose a directory") }
    var focused by remember { mutableStateOf(false) }

    val elevation = animateDpAsState(if (focused) 2.dp else 0.dp)
    val shape = RoundedCornerShape(40.dp)

    Surface(
        modifier = Modifier
            .border(borderStroke, shape)
            .fillMaxWidth()
            .height(200.dp)
            .onClick {
                path = platform.openFilePicker(true) ?: return@onClick
                onSelect(path)
            }
            .onPointerEvent(PointerEventType.Enter) {
                focused = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                focused = false
            },
        shape = shape,
        color = MaterialTheme.colorScheme.background,
        tonalElevation = elevation.value
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Text(
                path,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}