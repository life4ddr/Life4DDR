package com.perrigogames.life4.android.feature.firstrun

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.feature.ladder.LadderGoals
import com.perrigogames.life4.android.feature.ladder.RankSelection
import com.perrigogames.life4.android.stringResource
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.AutoResizedText
import com.perrigogames.life4.feature.ladder.RankListViewModel
import com.perrigogames.life4.feature.ladder.UIFooterData
import com.perrigogames.life4.feature.ladder.UIRankList
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun RankListScreen(
    isFirstRun: Boolean = false,
    viewModel: RankListViewModel = viewModel(
        factory = createViewModelFactory { RankListViewModel(isFirstRun) }
    ),
    onAction: (RankListViewModel.Action) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actions.collect(onAction)
    }
    RankListScreen(
        state = state,
        onInput = { viewModel.onInputAction(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankListScreen(
    state: UIRankList,
    onInput: (RankListViewModel.Input) -> Unit = {},
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
                        IconButton(
                            onClick = { onInput(RankListViewModel.Input.RankRejected) }
                        ) {
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
                data = state,
                onInput = onInput,
            )

            val ladderData = state.ladderData
            if (ladderData != null) {
                LadderGoals(
                    goals = ladderData.goals,
                    modifier = Modifier.weight(1f),
                    onInput = { onInput(RankListViewModel.Input.GoalList(it)) },
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            state.footer?.let { firstRun ->
                FirstRunWidget(
                    data = firstRun,
                    onInput = onInput,
                )
            }
        }
    }
}

@Composable
fun FirstRunWidget(
    data: UIFooterData,
    onInput: (RankListViewModel.Input) -> Unit = {},
) {
    data.footerText?.let { footer ->
        AutoResizedText(
            text = stringResource(footer),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = Paddings.HUGE)
                .padding(top = Paddings.LARGE)
        )
    }
    Button(
        onClick = { onInput(data.buttonInput) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Paddings.HUGE,
                vertical = Paddings.LARGE
            )
    ) {
        Text(text = stringResource(data.buttonText))
    }
}
