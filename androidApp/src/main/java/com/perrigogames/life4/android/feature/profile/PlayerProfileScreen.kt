package com.perrigogames.life4.android.feature.profile

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Typography
import com.perrigogames.life4.android.feature.banners.BannerContainer
import com.perrigogames.life4.android.feature.ladder.LadderGoals
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImage
import com.perrigogames.life4.feature.profile.PlayerInfoViewState
import com.perrigogames.life4.feature.profile.PlayerProfileAction
import com.perrigogames.life4.feature.profile.PlayerProfileViewModel
import com.perrigogames.life4.util.ViewState
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    profileViewModel: PlayerProfileViewModel = viewModel(
        factory = createViewModelFactory { PlayerProfileViewModel() }
    ),
    onAction: (PlayerProfileAction) -> Unit,
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val playerInfoViewState by profileViewModel.playerInfoViewState.collectAsState()
    val goalListViewState by profileViewModel.goalListViewModel.state.collectAsState()
    val bottomSheetState = remember {
        SheetState(
            initialValue = SheetValue.Hidden,
            density = density,
            skipPartiallyExpanded = false
        )
    }
    val goalData by remember { derivedStateOf { (goalListViewState as? ViewState.Success)?.data } }
    val goalError by remember { derivedStateOf { (goalListViewState as? ViewState.Error)?.error } }

    BackHandler {
        if (bottomSheetState.isVisible) {
            scope.launch { bottomSheetState.hide() }
        } else {
            backDispatcher?.onBackPressed()
        }
    }

    LaunchedEffect(Unit) {
        profileViewModel.goalListViewModel.showBottomSheet.collect {
            bottomSheetState.expand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = bottomSheetState
        ),
        sheetContent = {
            if (goalData?.hasSubstitutions == true) {
                LadderGoals(
                    goals = goalData!!.substitutions!!,
                    onInput = { profileViewModel.goalListViewModel.handleAction(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                        .weight(1f)
                )
            }
        },
        sheetPeekHeight = 0.dp,
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            PlayerProfileInfo(
                state = playerInfoViewState,
                modifier = Modifier.fillMaxWidth(),
                onRankClicked = { onAction(PlayerProfileAction.ChangeRank) }
            )
            BannerContainer(playerInfoViewState.banner)

            if (goalData != null) {
                LadderGoals(
                    goals = goalData!!.goals,
                    onInput = { profileViewModel.goalListViewModel.handleAction(it) },
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