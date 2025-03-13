package com.perrigogames.life4.android.feature.trial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.util.MokoImage
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.feature.trialsession.*
import com.perrigogames.life4.util.ViewState
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.desc.color.getColor

@Composable
fun TrialSession(
    trialId: String,
    modifier: Modifier = Modifier,
    viewModel: TrialSessionViewModel = viewModel(
        factory = createViewModelFactory { TrialSessionViewModel(trialId) }
    )
) {
    val _viewData by viewModel.state.collectAsState()
    when (val viewData = _viewData) {
        ViewState.Loading -> {
            Text("Loading...")
        }
        is ViewState.Success<UITrialSession> -> {
            TrialSessionContent(
                viewData = viewData.data,
                modifier = modifier,
                onAction = { viewModel.handleAction(it) }
            )
        }
        is ViewState.Error<Unit> -> {
            Text("Error loading Trial with ID $trialId")
        }
    }
}

@Composable
fun TrialSessionContent(
    viewData: UITrialSession,
    modifier: Modifier = Modifier,
    onAction: (TrialSessionAction) -> Unit = {},
) {
    Box(
        modifier = modifier
    ) {
        MokoImage(
            desc = viewData.backgroundImage,
            modifier = Modifier.matchParentSize()
                .backgroundGradientMask(),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
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
        RankSelector(
            viewData = viewData.targetRank,
            rankSelected = { onAction(TrialSessionAction.ChangeTargetRank(it)) }
        )
        SizedSpacer(16.dp)
        EXScoreBar(
            viewData = viewData.exScoreBar
        )
        SizedSpacer(16.dp)

        when (val content = viewData.content) {
            is UITrialSessionContent.Summary -> {
                SummaryContent(
                    viewData = content,
                    onAction = onAction,
                )
            }
            is UITrialSessionContent.SongFocused -> {
                SongFocusedContent(
                    viewData = content,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
fun SummaryContent(
    viewData: UITrialSessionContent.Summary,
    modifier: Modifier = Modifier,
    onAction: (TrialSessionAction) -> Unit = {},
) {
    val context = LocalContext.current
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
        CTAButton(
            text = viewData.buttonText.toString(context),
            onClick = { onAction(viewData.buttonAction) }
        )
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
            viewData.items.forEach { item ->
                InProgressJacketItem(
                    viewData = item,
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
        CTAButton(
            text = viewData.buttonText.toString(context),
            onClick = { onAction(viewData.buttonAction) }
        )
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
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
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
    }
}

@Composable
fun RowScope.InProgressJacketItem(
    viewData: UITrialSessionContent.SongFocused.Item,
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

private fun Modifier.backgroundGradientMask(): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White, Color.Transparent, Color.White),
                startY = 0f,
                endY = size.height / 2,
                tileMode = TileMode.Mirror
            ),
            blendMode = BlendMode.DstIn // Multiplies alpha from the gradient with the underlying image
        )
    }

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
