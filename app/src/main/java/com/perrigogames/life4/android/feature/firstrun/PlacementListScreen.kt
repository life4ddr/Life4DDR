package com.perrigogames.life4.android.feature.firstrun

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.LadderRankLevel3ParameterProvider
import com.perrigogames.life4.android.LightDarkModePreviews
import com.perrigogames.life4.android.LightDarkModeSystemPreviews
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.LadderRankClassTheme
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.AutoResizedText
import com.perrigogames.life4.android.view.compose.RankImage
import com.perrigogames.life4.data.trialrecords.UITrialMocks
import com.perrigogames.life4.data.trials.UIPlacement
import com.perrigogames.life4.data.trials.UIPlacementMocks
import com.perrigogames.life4.data.trials.UITrialSong
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.model.settings.InitState
import com.perrigogames.life4.viewmodel.PlacementListViewModel
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.colorResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementListScreen(
    viewModel: PlacementListViewModel = viewModel(
        factory = createViewModelFactory { PlacementListViewModel() }
    ),
    onPlacementSelected: (String) -> Unit,
    onRanksClicked: () -> Unit,
    goToMainScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var closeConfirmShown by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState()
    var selectedPlacement by remember { mutableStateOf<String?>(null) }

    BackHandler {
        scope.launch {
            if (modalBottomSheetState.isVisible) {
                modalBottomSheetState.hide()
            } else {
                modalBottomSheetState.show()
            }
        }
    }
    
    val data by viewModel.screenData.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = data.titleText.toString(context = context),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = Paddings.LARGE, start = Paddings.LARGE)
        )
        SizedSpacer(16.dp)
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                Text(
                    text = data.headerText.toString(context = context),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            items(data.placements) { placement ->
                SizedSpacer(16.dp)
                LadderRankClassTheme(ladderRankClass = placement.rankIcon.group) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large
                    ) {
                        PlacementItem(
                            data = placement,
                            expanded = selectedPlacement == placement.id,
                            onExpand = {
                                selectedPlacement = when {
                                    selectedPlacement == placement.id -> null
                                    else -> placement.id
                                }
                            },
                            onPlacementSelected = { onPlacementSelected(placement.id) }
                        )
                    }
                }
            }
        }

        SizedSpacer(Paddings.LARGE)

        Button(
            onClick = {
                viewModel.setFirstRunState(InitState.RANKS)
                onRanksClicked()
            },
        ) {
            Text(
                text = stringResource(MR.strings.select_rank_instead),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        TextButton(
            onClick = { closeConfirmShown = true },
        ) {
            Text(
                text = stringResource(MR.strings.start_no_rank),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        SizedSpacer(Paddings.LARGE)
    }
    if (closeConfirmShown) {
        ModalBottomSheet(
            onDismissRequest = { closeConfirmShown = false },
            sheetState = modalBottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Text(
                text = stringResource(MR.strings.placement_close_confirm_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = Paddings.HUGE)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(MR.strings.placement_close_confirm_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = Paddings.HUGE, vertical = Paddings.LARGE)
                    .align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = {
                    viewModel.setFirstRunState(InitState.DONE)
                    goToMainScreen()
                },
                modifier = Modifier
                    .padding(horizontal = Paddings.HUGE, vertical = Paddings.LARGE)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(MR.strings.close))
            }
        }
    }
}

@Composable
fun PlacementItem(
    data: UIPlacement,
    expanded: Boolean = false,
    onExpand: () -> Unit = {},
    onPlacementSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val arrowRotationDegrees by remember {
        derivedStateOf {
            if (expanded) 180f else 0f
        }
    }
    Column(modifier = modifier.clickable { onExpand() }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RankImage(
                rank = data.rankIcon,
                modifier = Modifier.size(64.dp)
            )
            SizedSpacer(16.dp)
            Text(
                text = stringResource(data.placementName),
                style = MaterialTheme.typography.headlineMedium,
                color = colorResource(data.color),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = data.difficultyRangeString,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(arrowRotationDegrees),
                contentDescription = if (expanded) "expanded" else "collapsed"
            )
        }
        AnimatedVisibility(expanded) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            ) {
                data.songs.forEach { song ->
                    PlacementSongItem(
                        data = song,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                SizedSpacer(16.dp)
                TextButton(
                    onClick = onPlacementSelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(MR.strings.placement_start),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun PlacementSongItem(
    data: UITrialSong,
    modifier: Modifier = Modifier,
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
            modifier = Modifier.size(64.dp)
        )
        SizedSpacer(16.dp)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            AutoResizedText(
                text = data.songNameText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = data.subtitleText,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        SizedSpacer(16.dp)
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
                    style = MaterialTheme.typography.titleSmall,
                    color = colorResource(data.difficultyClass.colorRes)
                )
                Text(
                    text = data.difficultyText,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(data.difficultyClass.colorRes)
                )
            }
        }
    }
}

@Composable
@LightDarkModeSystemPreviews
fun Preview_PlacementScreen() {
    LIFE4Theme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PlacementListScreen(
                onPlacementSelected = {},
                onRanksClicked = {},
                goToMainScreen = {}
            )
        }
    }
}

@Composable
@LightDarkModePreviews
fun Preview_PlacementItem(
    @PreviewParameter(LadderRankLevel3ParameterProvider::class) rank: LadderRank,
) {
    ThemedRankSurface(rank) {
        PlacementItem(
            data = UIPlacementMocks.createUIPlacementData(rankIcon = rank),
            onPlacementSelected = {}
        )
    }
}

@Composable
@LightDarkModePreviews
fun Preview_PlacementItemExpanded(
    @PreviewParameter(LadderRankLevel3ParameterProvider::class) rank: LadderRank,
) {
    ThemedRankSurface(rank) {
        PlacementItem(
            data = UIPlacementMocks.createUIPlacementData(
                rankIcon = rank
            ),
            expanded = true,
            onPlacementSelected = {}
        )
    }
}

@Composable
@Preview(widthDp = 480)
fun Preview_PlacementSongItem() {
    LIFE4Theme {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            PlacementSongItem(
                data = UITrialMocks.createUITrialSong()
            )
        }
    }
}

@Composable
private fun ThemedRankSurface(
    rank: LadderRank,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    content: @Composable () -> Unit,
) {
    LIFE4Theme {
        LadderRankClassTheme(ladderRankClass = rank.group) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = shape,
                modifier = modifier,
            ) {
                content()
            }
        }
    }
}