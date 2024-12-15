package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import utils.clamp

class ImageViewerState {
    var posX by mutableStateOf(0f)
    var posY by mutableStateOf(0f)
    var scale by mutableStateOf(1f)
    
    fun reset() {
        posX = 0f
        posY = 0f
        scale = 1f
    }
}

@Composable
fun rememberImageViewerState(): MutableState<ImageViewerState> {
    return remember { mutableStateOf(ImageViewerState()) }
}

@Composable
fun ImageViewer(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    maxZoom: Float = 4f,
    minZoom: Float = 1f
) {
    var state by rememberImageViewerState()

    ImageViewer(
        bitmap,
        modifier,
        maxZoom,
        minZoom,
        state
    ) 
}

@Composable
fun ImageViewer(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    maxZoom: Float = 4f,
    minZoom: Float = 1f,
    state: ImageViewerState
) {
    var snapEnabled by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    var st by remember { mutableStateOf(state) }
    
    if (st != state) {
        st = state
    }

    fun recalculateBounds(
        posX: Float = st.posY,
        posY: Float = st.posY,
        scale: Float = st.scale
    ) {
        st.scale = scale.clamp(minZoom, maxZoom)
        
        val thresholdX = (st.scale * size.width / 2) - size.width / 2
        val thresholdY = (st.scale * size.height / 2) - size.height / 2

        st.posX = posX.clamp(-thresholdX, thresholdX)
        st.posY = posY.clamp(-thresholdY, thresholdY)
    }

    BoxWithConstraints(modifier) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            var changes = event.changes.first()
                            when (event.type) {
                                PointerEventType.Scroll -> {
                                    recalculateBounds(scale = st.scale + changes.scrollDelta.y * 0.01f)
                                }

                                PointerEventType.Press -> {
                                    snapEnabled = true
                                }

                                PointerEventType.Release -> {
                                    snapEnabled = false
                                }

                                PointerEventType.Move -> {
                                    if (!snapEnabled) continue
                                    val delta = changes.position - changes.previousPosition
                                    recalculateBounds(st.posX + delta.x, st.posY + delta.y)
                                }
                            }
                        }
                    }
                }
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .onSizeChanged {
                        size = it
                        recalculateBounds()
                    }
                    .fillMaxSize()
                    .clipToBounds()
                    .graphicsLayer(
                        scaleX = st.scale,
                        scaleY = st.scale,
                        translationX = st.posX,
                        translationY = st.posY
                    )
            )
        }
    }
}
