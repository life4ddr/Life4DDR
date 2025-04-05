package com.perrigogames.life4.android.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale

@Composable
fun InteractiveImage(
    bitmap: ImageBitmap,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    // Remember state for scale, translation (drag), and rotation (if needed)
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var rotationState by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { _, pan, zoom, rotation ->
                    scale = (scale * zoom).coerceIn(0.5f, 5f) // Limit zoom between 0.5x and 5x
                    offset = Offset(offset.x + pan.x, offset.y + pan.y) // Update drag/translate
                    rotationState += rotation // Keep track of rotation if needed
                }
            )
        }
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    rotationZ = rotationState // You can remove this if rotation is not required
                )
        )
    }
}