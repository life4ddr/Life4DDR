package com.perrigogames.life4.android.feature.firstrun

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.feature.ladder.LadderGoals
import com.perrigogames.life4.android.feature.ranklist.RankSelection
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.AutoResizedText
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.viewmodel.RankListViewModel
import com.perrigogames.life4.viewmodel.UIRankList
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun FirstRunRankListScreen(
    viewModel: RankListViewModel = viewModel(
        factory = createViewModelFactory { RankListViewModel(isFirstRun = true) }
    ),
    onPlacementClicked: () -> Unit = {},
    goToMainScreen: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    FirstRunRankListScreen(
        state = state,
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

@Composable
fun FirstRunRankListScreen(
    state: UIRankList,
    onPlacementClicked: () -> Unit = {},
    onRankClicked: (LadderRank?) -> Unit = {},
    onRankSelected: (LadderRank?) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(state.titleText.resourceId),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(top = Paddings.LARGE, start = Paddings.LARGE)
        )
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
            AutoResizedText(
                text = stringResource(state.footerText.resourceId),
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
                Text(text = stringResource(firstRun.buttonText.resourceId))
            }
        }
    }
}