package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.enums.LadderRank

@Composable
fun RankImageWithTitle(
    rank: LadderRank?,
    iconSize: Dp = 84.dp,
    text: String = stringResource(rank?.nameRes ?: R.string.no_rank),
    style: TextStyle = MaterialTheme.typography.titleSmall,
    useRankColorText: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
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
    text: String = stringResource(rank?.nameRes ?: R.string.no_rank),
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