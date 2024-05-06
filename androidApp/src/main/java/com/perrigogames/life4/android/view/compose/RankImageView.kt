@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.drawableRes
import com.perrigogames.life4.enums.LadderRank

@Composable
fun RankImage(
    rank: LadderRank?,
    modifier: Modifier = Modifier,
    size: Dp = dimensionResource(R.dimen.profile_found_rank_size),
    onClick: () -> Unit = {},
) {
    val painter =
        painterResource(
            rank?.drawableRes ?: R.drawable.copper_1,
        )
    val colorMatrix = ColorMatrix().apply { setToSaturation(0f) }
    val colorFilter = if (rank != null) null else ColorFilter.colorMatrix(colorMatrix)
    Image(
        painter = painter,
        colorFilter = colorFilter,
        contentDescription = null, // FIXME
        modifier =
            modifier
                .size(size)
                .alpha(if (rank != null) 1F else 0.3F)
                .clickable { onClick() },
    )
}

@Preview
@Composable
fun RankImagePreviewNone() {
    MaterialTheme {
        RankImage(rank = null)
    }
}

@Preview
@Composable
fun RankImagePreviewCopper() {
    MaterialTheme {
        Column {
            LadderRank.entries
                .groupBy { it.group }
                .map { it.value }
                .forEach { group ->
                    Row {
                        group.forEach { rank ->
                            RankImage(size = 48.dp, rank = rank)
                        }
                    }
                }
        }
    }
}
