package com.perrigogames.life4.android.feature.firstrun

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.LadderRankClassTheme
import com.perrigogames.life4.android.feature.trial.CameraBottomSheetContent
import com.perrigogames.life4.android.feature.trial.LargeCTAButton
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImage
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.nameRes
import com.perrigogames.life4.feature.placements.PlacementDetailsAction
import com.perrigogames.life4.feature.placements.PlacementDetailsEvent
import com.perrigogames.life4.feature.placements.PlacementDetailsViewModel
import com.perrigogames.life4.feature.placements.UIPlacementDetails
import dev.icerock.moko.mvvm.createViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementDetailsScreen(
    placementId: String,
    viewModel: PlacementDetailsViewModel = viewModel(
        factory = createViewModelFactory { PlacementDetailsViewModel(placementId) }
    ),
    onBackPressed: () -> Unit = {},
    onNavigateToMainScreen: (String?) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = viewModel.state.collectAsState()
    var dialogData by remember { mutableStateOf<PlacementDetailsEvent.ShowTooltip?>(null) }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        )
    )

    fun hideBottomSheet() = scope.launch {
        scaffoldState.bottomSheetState.hide()
    }

    BackHandler {
        onBackPressed()
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PlacementDetailsEvent.NavigateToMainScreen -> {
                    onNavigateToMainScreen(event.submissionUrl?.toString(context))
                }
                PlacementDetailsEvent.ShowCamera -> {
                    scaffoldState.bottomSheetState.expand()
                }
                is PlacementDetailsEvent.ShowTooltip -> {
                    dialogData = event
                }
            }
        }
    }

    dialogData?.let { data ->
        val onAction = { viewModel.handleAction(data.ctaAction) }
        AlertDialog(
            onDismissRequest = onAction,
            confirmButton = {
                TextButton(onClick = onAction) {
                    Text(data.ctaText.toString(context))
                }
            },
            title = { Text(text = data.title.toString(context)) },
            text = { Text(text = data.message.toString(context)) }
        )
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                BackHandler {
                    hideBottomSheet()
                }
                CameraBottomSheetContent {
                    hideBottomSheet()
                    viewModel.handleAction(PlacementDetailsAction.PictureTaken)
                }
            }
        }
    ) {
        PlacementDetailsContent(
            viewData = state.value,
            modifier = Modifier.fillMaxSize(),
            onAction = viewModel::handleAction,
        )
    }
}

@Composable
fun PlacementDetailsContent(
    viewData: UIPlacementDetails,
    modifier: Modifier = Modifier,
    onAction: (PlacementDetailsAction) -> Unit,
) {
    val context = LocalContext.current
    LadderRankClassTheme(ladderRankClass = viewData.rankIcon.group) {
        Column(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp, vertical = 32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                RankImage(
                    rank = viewData.rankIcon,
                    size = 64.dp,
                )
                Text(
                    text = viewData.rankIcon.group.nameRes.getString(context),
                    style = MaterialTheme.typography.headlineLarge,
                    color = viewData.rankIcon.colorRes.getColor(context).let { Color(it) },
                )
            }
            SizedSpacer(32.dp)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                viewData.descriptionPoints.forEachIndexed { index, text ->
                    if (index > 0) {
                        SizedSpacer(8.dp)
                    }
                    Text(
                        text = text.toString(context),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                SizedSpacer(32.dp)

                viewData.songs.forEach { song ->
                    PlacementDetailsSongItem(
                        data = song,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SizedSpacer(16.dp)
                }
            }

            LargeCTAButton(
                text = viewData.ctaText.toString(context),
                onClick = { onAction(viewData.ctaAction) }
            )
        }
    }
}
