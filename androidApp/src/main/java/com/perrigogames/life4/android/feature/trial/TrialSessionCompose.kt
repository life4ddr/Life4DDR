package com.perrigogames.life4.android.feature.trial

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.util.MokoImage
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.feature.trials.view.*
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionAction
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionEvent
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionViewModel
import com.perrigogames.life4.util.ViewState
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.desc.color.getColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrialSession(
    trialId: String,
    modifier: Modifier = Modifier,
    viewModel: TrialSessionViewModel = viewModel(
        factory = createViewModelFactory { TrialSessionViewModel(trialId) }
    ),
    onClose: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val viewState by viewModel.state.collectAsState()
    val bottomSheetState by viewModel.bottomSheetState.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
        )
    )
    LaunchedEffect(bottomSheetState) {
        if (bottomSheetState != null) {
            scaffoldState.bottomSheetState.expand()
        } else {
            focusManager.clearFocus()
            scaffoldState.bottomSheetState.hide()
        }
    }

    when (val viewData = viewState) {
        ViewState.Loading -> {
            Text("Loading...")
        }
        is ViewState.Success<UITrialSession> -> {
            BackHandler {
                if (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
                    coroutineScope.launch {
                        bottomSheetState?.onDismissAction?.let {
                            viewModel.handleAction(it)
                        }
                    }
                } else {
                    onClose()
                }
            }

            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                modifier = modifier,
                sheetPeekHeight = 0.dp,
                sheetContent = {
                    when (val state = bottomSheetState) {
                        is UITrialBottomSheet.ImageCapture -> {
                            CameraBottomSheetContent(
                                onPhotoTaken = { uri ->
                                    viewModel.handleAction(state.createResultAction(uri.toString()))
                                },
                            )
                        }
                        is UITrialBottomSheet.Details -> {
                            SongEntryBottomSheetContent(
                                viewData = state,
                                onAction = {
                                    viewModel.handleAction(it)
                                },
                            )
                        }
                        is UITrialBottomSheet.DetailsPlaceholder,
                        null -> {}
                    }
                },
            ) { padding ->
                TrialSessionContent(
                    viewData = viewData.data,
                    modifier = Modifier.padding(padding),
                    onAction = { viewModel.handleAction(it) }
                )
            }
        }
        is ViewState.Error<Unit> -> {
            Text("Error loading Trial with ID $trialId")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TrialSessionEvent.Close -> onClose()
                TrialSessionEvent.HideBottomSheet -> {
                    focusManager.clearFocus()
                    scaffoldState.bottomSheetState.hide()
                }
            }
        }
    }
}

@Composable
fun TrialSessionContent(
    viewData: UITrialSession,
    modifier: Modifier = Modifier,
    onAction: (TrialSessionAction) -> Unit = {},
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
    ) {
        MokoImage(
            desc = viewData.backgroundImage,
            modifier = Modifier.matchParentSize()
                .blur(radius = 12.dp, BlurredEdgeTreatment.Unbounded),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillHeight,
            alpha = 0.3f,
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            TrialSessionHeader(
                viewData = viewData,
                onAction = onAction,
            )
            SizedSpacer(16.dp)

            AnimatedContent(
                targetState = viewData.content, label = "content",
                transitionSpec = {
                    slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) togetherWith
                            slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
                },
                modifier = Modifier.weight(1f)
            ) { content ->
                when (content) {
                    is UITrialSessionContent.Summary -> {
                        SummaryContent(content)
                    }
                    is UITrialSessionContent.SongFocused -> {
                        SongFocusedContent(
                            viewData = content,
                            onAction = onAction,
                        )
                    }
                }
            }
            SizedSpacer(32.dp)
            AnimatedContent(
                targetState = viewData.buttonText,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) {
                CTAButton(
                    text = it.toString(context),
                    onClick = { onAction(viewData.buttonAction) }
                )
            }
        }
    }
}

@Composable
fun TrialSessionHeader(
    viewData: UITrialSession,
    modifier: Modifier = Modifier,
    onAction: (TrialSessionAction) -> Unit = {},
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = viewData.trialTitle.toString(context),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = viewData.trialLevel.toString(context),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        SizedSpacer(16.dp)
        AnimatedContent(targetState = viewData.targetRank is UITargetRank.Achieved) {
            if (viewData.targetRank is UITargetRank.Achieved) {
                RankDisplay(
                    viewData = viewData.targetRank,
                    showSelectorIcon = false,
                    rankImageSize = 64.dp,
                    textStyle = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                RankSelector(
                    viewData = viewData.targetRank,
                    rankSelected = { onAction(TrialSessionAction.ChangeTargetRank(it)) }
                )
            }
        }
        SizedSpacer(16.dp)
        EXScoreBar(
            viewData = viewData.exScoreBar
        )
    }
}

@Composable
fun SummaryContent(
    viewData: UITrialSessionContent.Summary,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                viewData.items.getOrNull(0)?.let { item ->
                    SummaryJacketItem(viewData = item, modifier = Modifier.weight(1f))
                }
                viewData.items.getOrNull(1)?.let { item ->
                    SummaryJacketItem(viewData = item, modifier = Modifier.weight(1f))
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                viewData.items.getOrNull(2)?.let { item ->
                    SummaryJacketItem(viewData = item, modifier = Modifier.weight(1f))
                }
                viewData.items.getOrNull(3)?.let { item ->
                    SummaryJacketItem(viewData = item, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SongFocusedContent(
    viewData: UITrialSessionContent.SongFocused,
    modifier: Modifier = Modifier,
    onAction: (TrialSessionAction) -> Unit = {},
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            viewData.items.forEachIndexed { index, item ->
                InProgressJacketItem(
                    viewData = item,
                    onClick = item.tapAction?.let { { onAction(it) } }
                )
            }
        }

        SizedSpacer(32.dp)

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(viewData.focusedJacketUrl)
                .fallback(MR.images.trial_default.drawableResId)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
        )

        SizedSpacer(16.dp)
        Text(
            text = viewData.songTitleText.toString(context),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = viewData.difficultyClassText.toString(context),
                color = Color(viewData.difficultyClassColor.getColor(context)),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = viewData.difficultyNumberText.toString(context),
                fontWeight = FontWeight.Bold,
            )
        }
        SizedSpacer(32.dp)
    }
}

@Composable
fun EXScoreBar(
    viewData: UIEXScoreBar,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = viewData.labelText.toString(context),
            style = MaterialTheme.typography.titleLarge,
        )
        SizedSpacer(8.dp)
        if (viewData.hintCurrentEx != null) {
            Box(
                modifier = Modifier.weight(1f)
                    .height(8.dp)
            ) {
                LinearProgressIndicator(progress = { viewData.currentEx / viewData.maxEx.toFloat() })
                LinearProgressIndicator(progress = { viewData.hintCurrentEx!! / viewData.maxEx.toFloat() })
            }
        } else {
            LinearProgressIndicator(
                progress = { viewData.currentEx / viewData.maxEx.toFloat() },
                modifier = Modifier.weight(1f)
                    .height(8.dp)
            )
        }
        SizedSpacer(8.dp)
        Text(
            text = viewData.currentExText.toString(context),
            style = MaterialTheme.typography.titleLarge,
        )
        SizedSpacer(4.dp)
        Text(
            text = viewData.maxExText.toString(context),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun RowScope.SummaryJacketItem(
    viewData: UITrialSessionContent.Summary.Item,
    modifier: Modifier = Modifier.weight(1f)
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(viewData.jacketUrl)
                .fallback(MR.images.trial_default.drawableResId)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = viewData.difficultyClassText.toString(context),
                    color = Color(viewData.difficultyClassColor.getColor(context)),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = viewData.difficultyNumberText.toString(context)
                )
            }
            viewData.summaryContent?.let { content ->
                SummaryContent(content)
            }
        }
    }
}

@Composable
fun SummaryContent(
    viewData: UITrialSessionContent.Summary.SummaryContent,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        viewData.topText?.let { topText ->
            Text(
                text = topText.toString(context),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = viewData.bottomMainText.toString(context),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = viewData.bottomSubText.toString(context),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun RowScope.InProgressJacketItem(
    viewData: UITrialSessionContent.SongFocused.Item,
    modifier: Modifier = Modifier.weight(1f),
    onClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(viewData.jacketUrl)
                .fallback(MR.images.trial_default.drawableResId)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            viewData.topText?.let { topText ->
                Text(
                    text = topText.toString(context),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            viewData.bottomBoldText?.let { bottomText ->
                Text(
                    text = bottomText.toString(context),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// This can still be useful, but currently we don't use this here
//private fun Modifier.backgroundGradientMask(): Modifier = this
//    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
//    .drawWithContent {
//        drawContent()
//        drawRect(
//            brush = Brush.verticalGradient(
//                colors = listOf(Color.White, Color.Transparent, Color.White),
//                startY = 0f,
//                endY = size.height / 2,
//                tileMode = TileMode.Mirror
//            ),
//            blendMode = BlendMode.DstIn // Multiplies alpha from the gradient with the underlying image
//        )
//    }

@Composable
private fun CTAButton(
    text: String,
    onClick: () -> Unit = {},
) = Button(
    elevation = ButtonDefaults.elevatedButtonElevation(),
    colors = ButtonDefaults.elevatedButtonColors(),
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
