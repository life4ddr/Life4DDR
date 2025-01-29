package com.perrigogames.life4.android.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Typography
import com.perrigogames.life4.android.feature.banners.BannerContainer
import com.perrigogames.life4.android.feature.ladder.LadderGoals
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImage
import com.perrigogames.life4.feature.ladder.RankListAction
import com.perrigogames.life4.feature.profile.PlayerInfoViewState
import com.perrigogames.life4.feature.profile.PlayerProfileAction
import com.perrigogames.life4.feature.profile.PlayerProfileViewModel
import com.perrigogames.life4.util.ViewState
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun PlayerProfileScreen(
    profileViewModel: PlayerProfileViewModel = viewModel(
        factory = createViewModelFactory { PlayerProfileViewModel() }
    ),
    onAction: (PlayerProfileAction) -> Unit,
) {
    val playerInfoViewState by profileViewModel.playerInfoViewState.collectAsState()
    val goalListViewState by profileViewModel.goalListViewModel.state.collectAsState()
    val goalData by remember { derivedStateOf { (goalListViewState as? ViewState.Success)?.data } }
    val goalError by remember { derivedStateOf { (goalListViewState as? ViewState.Error)?.error } }

    Column {
        PlayerProfileInfo(
            state = playerInfoViewState,
            modifier = Modifier.fillMaxWidth(),
            onRankClicked = { onAction(PlayerProfileAction.ChangeRank) }
        )
        BannerContainer(playerInfoViewState.banner)

        if (goalData != null) {
            LadderGoals(
                data = goalData!!,
                onCompletedChanged = { id ->
                    profileViewModel.goalListViewModel.handleAction(RankListAction.OnGoal.ToggleComplete(id))
                },
                onHiddenChanged = { id ->
                    profileViewModel.goalListViewModel.handleAction(RankListAction.OnGoal.ToggleHidden(id))
                },
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
            )
        }
        if (goalError != null) {
            Text(
                text = goalError!!,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
fun PlayerProfileInfo(
    state: PlayerInfoViewState,
    modifier: Modifier = Modifier,
    onRankClicked: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = state.username,
                style = Typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            SizedSpacer(8.dp)
            state.rivalCode?.let {  rivalCode ->
                Text(
                    text = rivalCode,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        RankImage(
            rank = state.rank,
            size = 64.dp,
            onClick = onRankClicked
        )
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
                    username = "KONNOR"
                )
            )
            SizedSpacer(16.dp)
            PlayerProfileInfo(
                PlayerInfoViewState(
                    username = "KONNOR",
                    rivalCode = "1234-5678"
                )
            )
        }
    }
}