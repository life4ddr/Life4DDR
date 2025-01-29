package com.perrigogames.life4.android.feature.banners

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.feature.banners.BannerManager
import com.perrigogames.life4.feature.banners.UIBanner
import dev.icerock.moko.resources.desc.color.getColor

@Composable
fun BannerContainer(
    data: UIBanner? = null,
    modifier: Modifier = Modifier,
) {
    var lastBanner: UIBanner by remember { mutableStateOf(BannerManager.DUMMY_BANNER) }
    if (data != null) {
        lastBanner = data
    }

    val context = LocalContext.current
    val text = lastBanner.text.toString(context)
    val backgroundColor = lastBanner.backgroundColor?.getColor(context)?.let { Color(it) }
    val textColor = lastBanner.textColor?.getColor(context)?.let { Color(it) }

    AnimatedVisibility(
        visible = data != null,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Text(
            text = text,
            color = textColor ?: MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .background(backgroundColor ?: MaterialTheme.colorScheme.secondaryContainer)
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp,
                    horizontal = 16.dp
                )
                .then(modifier)
        )
    }
}