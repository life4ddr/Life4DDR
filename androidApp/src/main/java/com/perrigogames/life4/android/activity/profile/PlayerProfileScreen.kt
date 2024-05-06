package com.perrigogames.life4.android.activity.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Typography
import com.perrigogames.life4.android.feature.ladder.LadderGoals
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.feature.laddergoals.RankListAction
import com.perrigogames.life4.feature.profile.PlayerInfoViewState
import com.perrigogames.life4.feature.profile.PlayerProfileAction
import com.perrigogames.life4.feature.profile.PlayerProfileViewModel
import com.perrigogames.life4.util.ViewState
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun PlayerProfileScreen(
    profileViewModel: PlayerProfileViewModel =
        viewModel(
            factory = createViewModelFactory { PlayerProfileViewModel() },
        ),
    onAction: (PlayerProfileAction) -> Unit,
) {
    val playerInfoViewState by profileViewModel.playerInfoViewState.collectAsState()
    val goalListViewState by profileViewModel.goalListViewModel.state.collectAsState()
    val goalData by remember { derivedStateOf { (goalListViewState as? ViewState.Success)?.data } }
    val goalError by remember { derivedStateOf { (goalListViewState as? ViewState.Error)?.error } }

    Column {
        PlayerProfileInfo(state = playerInfoViewState)

        if (goalData != null) {
            LadderGoals(
                data = goalData!!,
                onCompletedChanged = { id ->
                    profileViewModel.goalListViewModel.handleAction(RankListAction.OnGoal.ToggleComplete(id))
                },
                onHiddenChanged = { id ->
                    profileViewModel.goalListViewModel.handleAction(RankListAction.OnGoal.ToggleHidden(id))
                },
                modifier =
                    Modifier.fillMaxWidth()
                        .weight(1f),
            )
        }
        if (goalError != null) {
            Text(
                text = goalError!!,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f),
            )
        }
    }
}

@Composable
fun PlayerProfileInfo(
    state: PlayerInfoViewState,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = state.username,
            style = Typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        state.rivalCode?.let { rivalCode ->
            Text(
                text = rivalCode,
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
@Preview(widthDp = 480)
fun PlayerProfilePreview() {
    LIFE4Theme {
        PlayerProfileScreen {}
    }
}

@Composable
@Preview(widthDp = 480)
fun PlayerProfileInfoPreview() {
    LIFE4Theme {
        Column {
            PlayerProfileInfo(
                PlayerInfoViewState(
                    username = "KONNOR",
                ),
            )
            SizedSpacer(16.dp)
            PlayerProfileInfo(
                PlayerInfoViewState(
                    username = "KONNOR",
                    rivalCode = "1234-5678",
                ),
            )
        }
    }
}
