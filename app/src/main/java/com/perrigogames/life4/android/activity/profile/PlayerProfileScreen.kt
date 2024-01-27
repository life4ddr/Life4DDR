package com.perrigogames.life4.android.activity.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Typography
import com.perrigogames.life4.android.ui.ladder.LadderGoals
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.enums.TrialJacketCorner
import com.perrigogames.life4.feature.laddergoals.RankListAction
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
        PlayerProfileInfo(state = playerInfoViewState)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            val modifier = Modifier
                .weight(1f)
                .aspectRatio(2f)
            SizedSpacer(16.dp)
            ProfileButton(
                title = stringResource(R.string.trials),
                icon = R.drawable.life4_trials_logo_invert,
                corner = TrialJacketCorner.NONE,
                iconScale = 1.3f,
                modifier = modifier,
                onClick = { onAction(PlayerProfileAction.Trials) },
            )
            SizedSpacer(16.dp)
            ProfileButton(
                title = stringResource(R.string.title_activity_settings),
                icon = R.drawable.ic_cogwheel,
                corner = TrialJacketCorner.NONE,
                iconScale = 0.8f,
                modifier = modifier,
                onClick = { onAction(PlayerProfileAction.Settings) },
            )
            SizedSpacer(16.dp)
        }

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
) {
    Column(modifier) {
        Text(
            text = state.username,
            style = Typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        state.rivalCode?.let {  rivalCode ->
            Text(
                text = rivalCode,
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileButton(
    title: String,
    @DrawableRes icon: Int,
    corner: TrialJacketCorner,
    iconScale: Float = 1f,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        content = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title.toUpperCase(Locale.current),
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.TopStart)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                )
                Image(
                    painter = painterResource(icon),
                    contentDescription = "$title button",
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.lighting(multiply = Color.White, add = Color.White),
                    modifier = Modifier.align(Alignment.Center)
                        .padding(top = 4.dp)
                        .fillMaxSize(0.5f * iconScale),
                )
                corner?.let {
                    // FIXME
                }
            }
        },
        modifier = modifier,
    )
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