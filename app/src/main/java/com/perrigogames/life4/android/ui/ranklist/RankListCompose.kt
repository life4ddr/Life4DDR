@file:Suppress("OPT_IN_USAGE")

package com.perrigogames.life4.android.ui.ranklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.colorRes
import com.perrigogames.life4.android.compose.FontFamilies
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImage
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RankSelection(
    ranks: List<LadderRank> = LadderRank.values().toList(),
    showNone: Boolean = true,
    initialRank: LadderRank? = null,
    onRankClicked: (LadderRank?) -> Unit = {},
) {
    val categories by remember { mutableStateOf(ranks.groupBy { it.group }) }
    val categoriesList by remember { mutableStateOf(categories.keys.toList()) }
    var selectedCategory by remember { mutableStateOf(initialRank?.group) }

    Column {
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            if (showNone) {
                item {
                    RankCategoryImage(null) { selectedCategory = null }
                }
            }
            items(categoriesList) { category ->
                RankCategoryImage(category) { selectedCategory = category }
            }
        }

        AnimatedVisibility(visible = selectedCategory != null) {
            val availableRanks = categories[selectedCategory] ?: return@AnimatedVisibility
            Column {
                Divider()
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    availableRanks.subList(0, 3).forEach { rank ->
                        RankImageWithTitle(rank) { onRankClicked(rank) }
                    }
                }
                SizedSpacer(size = 16.dp)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableRanks.subList(3, 5).forEach { rank ->
                        RankImageWithTitle(rank) { onRankClicked(rank) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankCategoryImage(
    category: LadderRankClass?,
    onClick: () -> Unit
) {
    RankImageWithTitle(
        rank = category?.toLadderRank(),
        iconSize = 48.dp,
        text = stringResource(category?.nameRes ?: R.string.no_rank),
        textWidth = 48.dp,
        style = MaterialTheme.typography.titleSmall,
        onClick = onClick
    )
}

@Composable
private fun RankImageWithTitle(
    rank: LadderRank?,
    iconSize: Dp = 64.dp,
    text: String = stringResource(rank?.nameRes ?: R.string.no_rank),
    textWidth: Dp? = null,
    style: TextStyle = MaterialTheme.typography.titleSmall,
    useRankColorText: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
            .clickable { onClick() }
    ) {
        RankImage(
            rank = rank,
            size = iconSize,
        )
        val modifier = Modifier.let {
            if (textWidth != null) it.widthIn(max = textWidth)
            else it
        }
        Text(
            text = text,
            modifier = modifier,
            color = if (rank != null && useRankColorText)
                colorResource(rank.colorRes)
            else
                MaterialTheme.colorScheme.onSurface,
            style = style,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.didOverflowWidth) {
                    style = style.copy(fontSize = style.fontSize * 0.9)
                } else {
                    readyToDraw = true
                }
            }
        )
    }
}

@Composable
fun RankSelectionMini(
    ranks: List<LadderRank> = LadderRank.values().toList(),
    showNone: Boolean = true,
) {

}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun RankSelectionPreview() {
    LIFE4Theme {
        Surface {
            RankSelection(initialRank = LadderRank.GOLD3)
        }
    }
}

@Preview(widthDp = 360)
@Composable
fun RankSelectionMiniPreview() {
    LIFE4Theme {
        Surface {
            RankSelectionMini()
        }
    }
}
