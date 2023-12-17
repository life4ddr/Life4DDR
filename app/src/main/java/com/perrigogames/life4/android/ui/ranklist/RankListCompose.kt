@file:Suppress("OPT_IN_USAGE")

package com.perrigogames.life4.android.ui.ranklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImageWithTitle
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import com.perrigogames.life4.viewmodel.UINoRank

@Composable
fun RankSelection(
    ranks: List<LadderRank?> = LadderRank.values().toList(),
    noRank: UINoRank = UINoRank.DEFAULT,
    initialRank: LadderRank? = null,
    onRankClicked: (LadderRank?) -> Unit = {},
    onRankRejected: () -> Unit = {},
) {
    val categories by remember { mutableStateOf(ranks.groupBy { it?.group }) }
    val categoriesList by remember { mutableStateOf(categories.keys.toList()) }
    var selectedCategory by remember { mutableStateOf(initialRank?.group) }
    var showSelectorPanel by remember { mutableStateOf(false) }

    Column {
        LazyRow(
            modifier = Modifier.padding(vertical = Paddings.MEDIUM)
        ) {
            item { SizedSpacer(size = Paddings.LARGE) }
            items(categoriesList) { category ->
                RankCategoryImage(category) {
                    showSelectorPanel = true
                    selectedCategory = category
                }
                SizedSpacer(size = Paddings.LARGE)
            }
        }
        Divider()

        AnimatedVisibility(visible = showSelectorPanel) {
            val availableRanks = categories[selectedCategory] ?: return@AnimatedVisibility
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                SizedSpacer(size = Paddings.HUGE)
                if (availableRanks.size < 5) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(noRank.bodyText.resourceId),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(horizontal = Paddings.HUGE)
                        )
                        Button(
                            onClick = onRankRejected,
                            modifier = Modifier
                                .padding(horizontal = Paddings.HUGE)
                                .padding(vertical = Paddings.HUGE)
                        ) {
                            Text(
                                text = stringResource(noRank.buttonText.resourceId)
                            )
                        }
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        availableRanks.subList(0, 3).forEach { rank ->
                            RankImageWithTitle(rank) { onRankClicked(rank) }
                        }
                    }
                    SizedSpacer(size = Paddings.LARGE)
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
}

@Composable
private fun RankCategoryImage(
    category: LadderRankClass?,
    onClick: () -> Unit
) {
    RankImageWithTitle(
        rank = category?.toLadderRank(),
        iconSize = 64.dp,
        text = stringResource(category?.nameRes ?: R.string.no_rank),
        style = MaterialTheme.typography.titleSmall,
        onClick = onClick
    )
}

@Composable
fun RankSelectionMini(
    modifier: Modifier = Modifier,
    ranks: List<LadderRank> = LadderRank.values().toList(),
    showNone: Boolean = true,
    onRankSelected: (LadderRank?) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        item {
            SizedSpacer(size = 10.dp)
        }
        if (showNone) {
            item {
                RankImageWithTitle(rank = null)
            }
        }
        items(ranks) { rank ->
            RankImageWithTitle(
                rank = rank,
                onClick = { onRankSelected(rank) }
            )
        }
        item {
            SizedSpacer(size = 10.dp)
        }
    }
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
            RankSelectionMini(
                onRankSelected = {}
            )
        }
    }
}
