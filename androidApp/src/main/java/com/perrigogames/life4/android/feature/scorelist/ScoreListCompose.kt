package com.perrigogames.life4.android.feature.scorelist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.feature.banners.BannerContainer
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.feature.songresults.ScoreListViewModel
import com.perrigogames.life4.feature.songresults.UIScore
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.colorResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreListScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreListViewModel = viewModel(
        factory = createViewModelFactory { ScoreListViewModel() }
    ),
    onBackPressed: () -> Unit,
    showSanbaiLogin: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val state = viewModel.state.collectAsState()

    BackHandler { 
        onBackPressed()
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                BackHandler {
                    scope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                }
            }
            FilterPane(
                data = state.value.filter,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp),
                onAction = { viewModel.handleFilterAction(it) }
            )
        },
        modifier = modifier,
    ) { outterPadding ->
        Scaffold(
            modifier = Modifier.padding(outterPadding),
            floatingActionButton = {
                SongListFloatingActionButtons(
                    onFilterPressed = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                    onSyncScoresPressed = {
                        scope.launch {
                            if (!viewModel.refreshSanbaiScores()) {
                                val authUrl = viewModel.getSanbaiUrl()
                                showSanbaiLogin(authUrl)
                            }
                        }
                    }
                )
            },
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    BannerContainer(state.value.banner)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.value.scores) {
                        ScoreEntry(it)
                    }
                }
            }
        }
    }
}

@Composable
private fun SongListFloatingActionButtons(
    onFilterPressed: () -> Unit,
    onSyncScoresPressed: () -> Unit,
) {
    var isFabExpanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.End,
    ) {
        AnimatedVisibility(visible = isFabExpanded) {
            Column {
                SmallFloatingActionButton(
                    onClick = {
                        onFilterPressed()
                        isFabExpanded = false
                    },
                ) {
                    Icon(
                        painterResource(R.drawable.filter_list_24px),
                        contentDescription = "Change Filters"
                    )
                }
                SizedSpacer(8.dp)
                SmallFloatingActionButton(
                    onClick = {
                        onSyncScoresPressed()
                        isFabExpanded = false
                    },
                ) {
                    Icon(
                        painterResource(R.drawable.sync_24px),
                        contentDescription = "Sync Sanbai Scores"
                    )
                }
                SizedSpacer(8.dp)
            }
        }

        FloatingActionButton(
            onClick = { isFabExpanded = !isFabExpanded },
        ) {
            Icon(
                painterResource(if (isFabExpanded) R.drawable.close_24px else R.drawable.more_vert_24px),
                contentDescription = if (isFabExpanded) "Close" else "Expand"
            )
        }
    }
}

@Composable
fun ScoreEntry(data: UIScore) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = data.titleText,
                maxLines = 1,
            )
            Row {
                Text(
                    text = data.difficultyText.toString(context),
                    color = colorResource(data.difficultyColor),
                    modifier = Modifier.weight(1f),
                )
                SizedSpacer(4.dp)
                Text(
                    text = data.scoreText.toString(context),
                    color = colorResource(data.scoreColor),
                )
            }
        }
        val flareResource = data.flareLevel?.let { flareImageResource(it) }
        if (flareResource != null) {
            Image(
                painter = painterResource(flareResource),
                contentDescription = "Flare level ${data.flareLevel}",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            SizedSpacer(32.dp)
        }
    }
}

fun flareImageResource(level: Int): Int? = when(level) {
    1 -> R.drawable.flare_1
    2 -> R.drawable.flare_2
    3 -> R.drawable.flare_3
    4 -> R.drawable.flare_4
    5 -> R.drawable.flare_5
    6 -> R.drawable.flare_6
    7 -> R.drawable.flare_7
    8 -> R.drawable.flare_8
    9 -> R.drawable.flare_9
    10 -> R.drawable.flare_ex
    else -> null
}
