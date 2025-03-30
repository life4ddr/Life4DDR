package com.perrigogames.life4.android.util

import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.ImageDescResource
import dev.icerock.moko.resources.desc.image.ImageDescUrl
import kotlin.math.atan2

@Composable
fun MokoImage(
    desc: ImageDesc,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    alpha: Float = DefaultAlpha,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    asyncOptions: (ImageRequest.Builder.() -> ImageRequest.Builder) = { this },
) {
    when(desc) {
        is ImageDescResource -> {
            Image(
                painter = painterResource(desc.resource.drawableResId),
                modifier = modifier,
                contentDescription = contentDescription,
                alignment = alignment,
                alpha = alpha,
                contentScale = contentScale,
                colorFilter = colorFilter,
            )
        }
        is ImageDescUrl -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(desc.url)
                    .asyncOptions()
                    .build(),
                contentDescription = contentDescription,
                alignment = alignment,
                alpha = alpha,
                contentScale = contentScale,
                colorFilter = colorFilter,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

fun getSensorRotation(x: Float, y: Float): Int {
    val angle = atan2(-x, y) * (180 / Math.PI).toFloat()
    return when (angle) {
        in 45f..135f -> Surface.ROTATION_270
        in -45f..45f -> Surface.ROTATION_0
        in -135f..-45f -> Surface.ROTATION_90
        else -> Surface.ROTATION_180
    }
}
