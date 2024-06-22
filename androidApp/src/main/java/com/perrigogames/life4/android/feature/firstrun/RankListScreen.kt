package com.perrigogames.life4.android.feature.firstrun

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.feature.ladder.LadderGoals
import com.perrigogames.life4.android.feature.ladder.RankSelection
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.AutoResizedText
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.viewmodel.RankListViewModel
import com.perrigogames.life4.viewmodel.UIFirstRunRankList
import com.perrigogames.life4.viewmodel.UIRankList
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun RankListScreen(
    isFirstRun: Boolean = false,
    viewModel: RankListViewModel = viewModel(
        factory = createViewModelFactory { RankListViewModel(isFirstRun) }
    ),
    onPlacementClicked: () -> Unit = {},
    goToMainScreen: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    RankListScreen(
        state = state,
        onNavigateUp = goToMainScreen,
        onPlacementClicked = {
            viewModel.moveToPlacements()
            onPlacementClicked()
        },
        onRankClicked = viewModel::setRankSelected,
        onRankSelected = { rank ->
            viewModel.saveRank(rank)
            goToMainScreen()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankListScreen(
    state: UIRankList,
    onNavigateUp: () -> Unit = {},
    onPlacementClicked: () -> Unit = {},
    onRankClicked: (LadderRank?) -> Unit = {},
    onRankSelected: (LadderRank?) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(state.titleText.resourceId),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    if (state.showBackButton) {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            SizedSpacer(32.dp)
            RankSelection(
                ranks = state.ranks,
                noRank = state.noRank,
                onRankClicked = onRankClicked,
                onRankRejected = { onRankSelected(null) },
            )

            val ladderData = state.ladderData
            if (ladderData != null) {
                LadderGoals(
                    data = ladderData,
                    modifier = Modifier.weight(1f),
                    onCompletedChanged = {},
                    onHiddenChanged = {}
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            state.firstRun?.let { firstRun ->
                FirstRunWidget(
                    data = firstRun,
                    onPlacementClicked = onPlacementClicked,
                )
            }
        }
    }
}

@Composable
fun ColumnScope.FirstRunWidget(
    data: UIFirstRunRankList,
    onPlacementClicked: () -> Unit = {},
) {
    AutoResizedText(
        text = stringResource(data.footerText.resourceId),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .padding(
                horizontal = Paddings.HUGE,
                vertical = Paddings.LARGE
            )
    )
    Button(
        onClick = onPlacementClicked,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Paddings.HUGE)
            .padding(bottom = Paddings.LARGE)
    ) {
        Text(text = stringResource(data.buttonText.resourceId))
    }
}
