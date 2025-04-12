package com.perrigogames.life4.android.feature.firstrun

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.AutoResizedText
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.feature.trials.view.UITrialSong
import dev.icerock.moko.resources.compose.colorResource

@Composable
fun PlacementDetailsSongItem(
    data: UITrialSong,
    modifier: Modifier = Modifier,
) {
    PlacementSongItem(
        data = data,
        modifier = modifier,
        jacketSize = 96.dp,
        detailSpacing = 0.dp,
        titleTextLines = 3,
        titleTextStyle = MaterialTheme.typography.titleLarge,
        mixTextStyle = MaterialTheme.typography.titleMedium,
        difficultyClassTextStyle = MaterialTheme.typography.titleMedium,
        difficultyNumberTextStyle = MaterialTheme.typography.titleLarge,
    )
}

@Composable
fun PlacementSongItem(
    data: UITrialSong,
    modifier: Modifier = Modifier,
    jacketSize: Dp = 64.dp,
    detailSpacing: Dp = 16.dp,
    titleTextLines: Int = 1,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
    mixTextStyle: TextStyle = MaterialTheme.typography.titleSmall,
    difficultyClassTextStyle: TextStyle = MaterialTheme.typography.titleSmall,
    difficultyNumberTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.jacketUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(jacketSize)
        )
        SizedSpacer(16.dp)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (titleTextLines == 1) {
                AutoResizedText(
                    text = data.songNameText,
                    style = titleTextStyle,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = data.songNameText,
                    style = titleTextStyle,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = titleTextLines,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = data.subtitleText,
                style = mixTextStyle,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        SizedSpacer(detailSpacing)
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.width(50.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = data.chartString,
                    style = difficultyClassTextStyle,
                    color = colorResource(data.difficultyClass.colorRes)
                )
                Text(
                    text = data.difficultyText,
                    style = difficultyNumberTextStyle,
                    color = colorResource(data.difficultyClass.colorRes)
                )
            }
        }
    }
}
