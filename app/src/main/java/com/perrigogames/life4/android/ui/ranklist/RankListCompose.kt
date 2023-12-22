@file:Suppress("OPT_IN_USAGE")

package com.perrigogames.life4.android.ui.ranklist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.nameRes
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImageWithTitle
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.LadderRankClass
import com.perrigogames.life4.enums.categoryNameRes
import com.perrigogames.life4.viewmodel.RankSelectionConfig
import com.perrigogames.life4.viewmodel.RankSelectionViewModel
import com.perrigogames.life4.viewmodel.UINoRank
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun RankSelection(
    modifier: Modifier = Modifier,
    config: RankSelectionConfig = RankSelectionConfig(),
    viewModel: RankSelectionViewModel = viewModel(
        factory = createViewModelFactory { RankSelectionViewModel(config) }
    ),
    onRankClicked: (LadderRank?) -> Unit = {},
    onRankRejected: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    RankSelection(
        modifier = modifier,
        ranks = state.ranks,
        noRank = state.noRank,
        initialRank = state.initialRank,
        onRankClicked = onRankClicked,
        onRankRejected = onRankRejected,
    )
}

@Composable
fun RankSelection(
    modifier: Modifier = Modifier,
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
    var compressSelectorPanel by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {
        LazyRow(
            modifier = Modifier.padding(vertical = Paddings.MEDIUM)
        ) {
            item { SizedSpacer(size = Paddings.LARGE) }
            items(categoriesList) { category ->
                RankCategoryImage(category) {
                    showSelectorPanel = true
                    compressSelectorPanel = false
                    selectedCategory = category
                    onRankClicked(null)
                }
                SizedSpacer(size = Paddings.LARGE)
            }
        }
        Divider()

        AnimatedVisibility(visible = showSelectorPanel) {
            AnimatedContent(
                targetState = selectedCategory to compressSelectorPanel,
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
            ) { (category, compress) ->
                val availableRanks = categories[category] ?: return@AnimatedContent
                RankDetailSelector(
                    availableRanks = availableRanks,
                    compress = compress,
                    modifier = Modifier.weight(1f),
                    noRank = noRank,
                    onCompressionChanged = { compressSelectorPanel = it },
                    onRankClicked = onRankClicked,
                    onRankRejected = onRankRejected,
                )
            }
        }
    }
}

@Composable
fun RankDetailSelector(
    availableRanks: List<LadderRank?>,
    compress: Boolean,
    modifier: Modifier = Modifier,
    noRank: UINoRank = UINoRank.DEFAULT,
    onCompressionChanged: (Boolean) -> Unit = {},
    onRankClicked: (LadderRank?) -> Unit = {},
    onRankRejected: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SizedSpacer(size = Paddings.HUGE)
        if (availableRanks.size < 5) {
            NoRankDetails(
                noRank = noRank,
                onRankRejected = onRankRejected
            )
        } else {
            RankCategorySelector(
                availableRanks = availableRanks.filterNotNull(),
                compressed = compress,
                onRankClicked = {
                    onCompressionChanged(true)
                    onRankClicked(it)
                }
            )
        }
    }
}

@Composable
fun NoRankDetails(
    modifier: Modifier = Modifier,
    noRank: UINoRank = UINoRank.DEFAULT,
    onRankRejected: () -> Unit = {},
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
}

@Composable
fun RankCategorySelector(
    availableRanks: List<LadderRank>,
    compressed: Boolean,
    modifier: Modifier = Modifier,
    onRankClicked: (LadderRank) -> Unit = {},
) {
    Column(modifier = modifier) {
        if (compressed) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                availableRanks.forEach { rank ->
                    RankImageWithTitle(
                        rank = rank,
                        iconSize = 48.dp,
                        text = stringResource(rank.categoryNameRes.resourceId)
                    ) { onRankClicked(rank) }
                }
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
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
