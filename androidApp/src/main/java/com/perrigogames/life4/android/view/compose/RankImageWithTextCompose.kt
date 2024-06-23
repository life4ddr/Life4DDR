package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.nameRes
import dev.icerock.moko.resources.compose.colorResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun RankImageWithTitle(
    rank: LadderRank?,
    iconSize: Dp = 84.dp,
    text: String = stringResource(rank?.nameRes ?: MR.strings.no_rank),
    style: TextStyle = MaterialTheme.typography.titleSmall,
    selected: Boolean = false,
    useRankColorText: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .let {
                if (selected) {
                    it.background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                    )
                } else {
                    it
                }
            }
    ) {
        RankImage(
            rank = rank,
            size = iconSize,
            onClick = onClick,
        )
        RankText(
            rank = rank,
            text = text,
            textWidth = iconSize,
            style = style,
            useRankColorText = useRankColorText
        )
    }
}

@Composable
fun RankText(
    rank: LadderRank?,
    modifier: Modifier = Modifier,
    text: String = stringResource(rank?.nameRes ?: MR.strings.no_rank),
    textWidth: Dp? = null,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    useRankColorText: Boolean = false
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.height(32.dp)
    ) {
        AutoResizedText(
            text = text,
            modifier = Modifier
                .let {
                    if (textWidth != null) it.widthIn(max = textWidth)
                    else it
                },
            color = if (rank != null && useRankColorText)
                colorResource(rank.colorRes)
            else
                MaterialTheme.colorScheme.onSurface,
            style = style,
        )
    }
}