package com.perrigogames.life4.android.ui.firstrun

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
import com.perrigogames.life4.android.ui.ranklist.RankSelection
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.AutoResizedText
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.model.settings.InitState
import com.perrigogames.life4.viewmodel.RankListViewModel
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun FirstRunRankListScreen(
    viewModel: RankListViewModel = viewModel(
        factory = createViewModelFactory { RankListViewModel(isFirstRun = true) }
    ),
    onPlacementClicked: () -> Unit,
    onRankClicked: (LadderRank?) -> Unit,
    goToMainScreen: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

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
            onRankRejected = {
                viewModel.setFirstRunState(InitState.DONE)
                goToMainScreen()
            }
        )
        Spacer(modifier = Modifier.weight(1f))
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
            onClick = {
                viewModel.setFirstRunState(InitState.PLACEMENTS)
                onPlacementClicked()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Paddings.HUGE)
                .padding(bottom = Paddings.LARGE)
        ) {
            Text(text = stringResource(state.firstRun.buttonText.resourceId))
        }
    }
}