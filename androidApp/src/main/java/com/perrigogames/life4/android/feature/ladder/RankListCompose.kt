@file:Suppress("OPT_IN_USAGE")

package com.perrigogames.life4.android.feature.ladder

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.stringResource
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImageWithTitle
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.ladder.RankListViewModel
import com.perrigogames.life4.feature.ladder.UILadderRank
import com.perrigogames.life4.feature.ladder.UINoRank
import com.perrigogames.life4.feature.ladder.UIRankList

@Composable
fun RankSelection(
    data: UIRankList,
    modifier: Modifier = Modifier,
    onInput: (RankListViewModel.Input) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        RankCategorySelector(
            data = data,
            modifier = Modifier.padding(vertical = Paddings.MEDIUM),
            onInput = onInput
        )
        HorizontalDivider()

        AnimatedVisibility(visible = data.showRankSelector) {
            AnimatedContent(
                targetState = data.ranks to data.isRankSelectorCompressed,
                label = "anim_between_categories",
                transitionSpec = {
                    if (targetState.first == initialState.first) {
                        if (targetState.second) {
                            slideInVertically() + fadeIn() togetherWith
                                slideOutVertically() + fadeOut()
                        } else {
                            slideInVertically() + fadeIn() togetherWith
                                slideOutVertically() + fadeOut()
                        }
                    } else {
                        if (targetState.second) {
                            slideInVertically() + fadeIn() togetherWith
                                slideOutVertically() + fadeOut()
                        } else {
                            slideInVertically() + fadeIn() togetherWith
                                slideOutVertically() + fadeOut()
                        }
                    }
                }
            ) { (ranks, compress) ->
                RankDetailSelector(
                    availableRanks = ranks,
                    compress = compress,
                    modifier = Modifier.weight(1f),
                    noRank = data.noRankInfo,
                    onInput = onInput,
                )
            }
        }
    }
}

@Composable
fun RankDetailSelector(
    availableRanks: List<UILadderRank>,
    compress: Boolean,
    modifier: Modifier = Modifier,
    noRank: UINoRank = UINoRank.DEFAULT,
    onInput: (RankListViewModel.Input) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SizedSpacer(size = Paddings.HUGE)
        if (availableRanks.size < 5) {
            NoRankDetails(
                noRank = noRank,
                onInput = onInput,
            )
        } else {
            RankItemSelector(
                availableRanks = availableRanks,
                compressed = compress,
                onInput = onInput,
            )
        }
    }
}

@Composable
fun NoRankDetails(
    modifier: Modifier = Modifier,
    noRank: UINoRank = UINoRank.DEFAULT,
    onInput: (RankListViewModel.Input) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(noRank.bodyText.resourceId),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = Paddings.HUGE)
        )
        Button(
            onClick = { onInput(noRank.buttonInput) },
            modifier = Modifier
                .padding(horizontal = Paddings.HUGE)
                .padding(vertical = Paddings.HUGE)
        ) {
            Text(
                text = stringResource(noRank.buttonText.resourceId)
            )
        }
    }
}

@Composable
fun RankCategorySelector(
    data: UIRankList,
    modifier: Modifier = Modifier,
    onInput: (RankListViewModel.Input) -> Unit = {},
) {
    LazyRow(
        modifier = modifier
    ) {
        items(data.rankClasses) { category ->
            RankImageWithTitle(
                rank = category.rankClass?.toLadderRank(),
                modifier = Modifier
                    .padding(horizontal = Paddings.MEDIUM)
                    .padding(top = Paddings.MEDIUM),
                iconSize = 64.dp,
                selected = category.selected,
                text = stringResource(category.text),
                style = MaterialTheme.typography.titleSmall,
                onClick = { onInput(category.tapInput) }
            )
        }
    }
}

@Composable
fun RankItemSelector(
    availableRanks: List<UILadderRank>,
    compressed: Boolean,
    modifier: Modifier = Modifier,
    onInput: (RankListViewModel.Input) -> Unit = {},
) {
    Column(modifier = modifier) {
        if (compressed) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                availableRanks.forEach { rank ->
                    RankImageWithTitle(
                        rank = rank.rank,
                        modifier = Modifier
                            .padding(horizontal = Paddings.MEDIUM)
                            .padding(top = Paddings.MEDIUM),
                        selected = rank.selected,
                        iconSize = 48.dp,
                        text = stringResource(rank.text)
                    ) { onInput(rank.tapInput) }
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                availableRanks.subList(0, 3).forEach { rank ->
                    RankImageWithTitle(rank.rank) { onInput(rank.tapInput) }
                }
            }
            SizedSpacer(size = Paddings.LARGE)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                availableRanks.subList(3, 5).forEach { rank ->
                    RankImageWithTitle(rank.rank) { onInput(rank.tapInput) }
                }
            }
        }
    }
}

@Composable
fun RankSelectionMini(
    modifier: Modifier = Modifier,
    ranks: List<LadderRank> = LadderRank.entries,
    selectedRank: LadderRank?,
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
                RankImageWithTitle(
                    rank = null,
                    selected = selectedRank == null
                )
            }
        }
        items(ranks) { rank ->
            RankImageWithTitle(
                rank = rank,
                selected = rank == selectedRank,
                onClick = { onRankSelected(rank) }
            )
        }
        item {
            SizedSpacer(size = 10.dp)
        }
    }
}

//@Preview(widthDp = 360, heightDp = 640)
//@Composable
//fun RankSelectionPreview() {
//    LIFE4Theme {
//        Surface {
//            RankSelection(initialRank = LadderRank.GOLD3)
//        }
//    }
//}

@Preview(widthDp = 360)
@Composable
fun RankSelectionMiniPreview() {
    LIFE4Theme {
        Surface {
            RankSelectionMini(
                selectedRank = null,
                onRankSelected = {}
            )
        }
    }
}
