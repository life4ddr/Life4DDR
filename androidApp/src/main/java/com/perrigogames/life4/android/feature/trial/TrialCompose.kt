package com.perrigogames.life4.android.feature.trial

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.compose.FontFamilies
import com.perrigogames.life4.android.compose.FontSizes
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.stringResource
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.JacketCorner
import com.perrigogames.life4.android.view.compose.RankImage
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.view.UIPlacementBanner
import com.perrigogames.life4.feature.trials.view.UITrialJacket
import com.perrigogames.life4.feature.trials.view.UITrialList
import com.perrigogames.life4.feature.trials.viewmodel.TrialListViewModel
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDescResource

@Composable
fun TrialListScreen(
    modifier: Modifier = Modifier,
    viewModel: TrialListViewModel = viewModel(
        factory = createViewModelFactory { TrialListViewModel() }
    ),
    onTrialSelected: (Trial) -> Unit = {},
    onPlacementsSelected: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    var quickAddDialogTrial by remember { mutableStateOf<Trial?>(null) }
    var deleteRecordsConfirmationTrial by remember { mutableStateOf<Trial?>(null) }

    Column(modifier = modifier) {
        state.placementBanner?.let { banner ->
            PlacementBanner(
                banner = banner,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = Paddings.MEDIUM)
                    .padding(top = Paddings.LARGE),
                onPlacementsSelected = onPlacementsSelected,
            )
        }

        TrialJacketList(
            displayList = state.trials, // FIXME
            onTrialSelected = onTrialSelected,
            onTrialQuickAddSelected = { quickAddDialogTrial = it },
            onTrialClearRecordsSelected = { deleteRecordsConfirmationTrial = it },
            modifier = Modifier.weight(1f),
        )
    }

    quickAddDialogTrial?.let { trial ->
        TrialQuickAddDialog(
            availableRanks = trial.goals?.map { it.rank } ?: listOf(TrialRank.COPPER),
            onSubmit = { rank, exScore ->
                viewModel.addTrialPlay(trial, rank, exScore)
                quickAddDialogTrial = null
            },
            onDismiss = { quickAddDialogTrial = null }
        )
    }

    deleteRecordsConfirmationTrial?.let { trial ->
        AlertDialog(
            onDismissRequest = { deleteRecordsConfirmationTrial = null },
            title = { Text(MR.strings.trial_clear_records_title.desc().localized()) },
            text = { Text(
                StringDesc.ResourceFormatted(
                    MR.strings.trial_clear_records_body,
                    trial.name
                ).localized()
            ) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearTrialData(trial.id)
                        deleteRecordsConfirmationTrial = null
                    },
                    content = { Text(MR.strings.yes.desc().localized()) }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { deleteRecordsConfirmationTrial = null },
                    content = { Text(MR.strings.no.desc().localized()) }
                )
            },
        )
    }
}

@Composable
fun PlacementBanner(
    banner: UIPlacementBanner,
    modifier: Modifier = Modifier,
    onPlacementsSelected: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onPlacementsSelected() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(banner.text),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        SizedSpacer(Paddings.MEDIUM)
        banner.ranks.forEach { rank ->
            RankImage(
                rank = rank,
                size = 24.dp,
                onClick = null
            )
        }
    }
}

@Composable
fun TrialJacketList(
    displayList: List<UITrialList.Item>,
    onTrialSelected: (Trial) -> Unit,
    onTrialQuickAddSelected: (Trial) -> Unit,
    onTrialClearRecordsSelected: (Trial) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(Paddings.MEDIUM),
        horizontalArrangement = Arrangement.spacedBy(Paddings.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Paddings.MEDIUM),
        modifier = modifier,
    ) {
        displayList.forEach { displayItem ->
            when (displayItem) {
                is UITrialList.Item.Header -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Text(
                        text = displayItem.text.localized(),
                        fontSize = FontSizes.SMALL,
                        fontFamily = FontFamilies.AVENIR_NEXT,
                    )
                }
                is UITrialList.Item.Trial -> item {
                    TrialJacket(
                        viewData = displayItem.data,
                        onClick = { onTrialSelected(displayItem.data.trial) },
                        onQuickAddSelected = { onTrialQuickAddSelected(displayItem.data.trial) },
                        onClearRecordsSelected = { onTrialClearRecordsSelected(displayItem.data.trial) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrialJacket(
    viewData: UITrialJacket,
    onClick: () -> Unit,
    onQuickAddSelected: () -> Unit,
    onClearRecordsSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var contextMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = { contextMenuExpanded = true },
            )
            .then(modifier),
    ) {
        Image(
            painter = (viewData.trial.coverResource as? ImageDescResource)?.let {
                painterResource(it.resource)
            } ?: painterResource(MR.images.trial_default),
            contentDescription = null,
            modifier = Modifier.aspectRatio(1f)
                .alpha(viewData.viewAlpha)
        )
        viewData.trial.difficulty?.let { diffNum ->
            TrialDifficulty(
                difficulty = diffNum,
                modifier = Modifier.align(Alignment.TopStart)
                    .padding(Paddings.SMALL)
            )
        }
        AnimatedContent(
            targetState = viewData.cornerType,
            transitionSpec = { fadeIn() togetherWith  fadeOut() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) { type ->
            JacketCorner(type)
        }

        val rank = viewData.rank
        val exScore = viewData.exScore
        if (rank != null && exScore != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Paddings.SMALL),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Paddings.SMALL)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.8f
                        ),
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(Paddings.SMALL),
            ) {
                RankImage(
                    rank = rank.parent,
                    size = 32.dp,
                )
                Text(
                    text = exScore.toString(context),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        DropdownMenu(
            expanded = contextMenuExpanded,
            onDismissRequest = { contextMenuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Quick Add") },
                onClick = {
                    onQuickAddSelected()
                    contextMenuExpanded = false
                },
            )
            DropdownMenuItem(
                text = { Text("Clear Records") },
                onClick = {
                    onClearRecordsSelected()
                    contextMenuExpanded = false
                }
            )
        }
    }
}

@Composable
fun TrialDifficulty(
    difficulty: Int,
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                shape = CircleShape
            ),
    ) {
        Text(
            text = difficulty.toString(),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
