package com.perrigogames.life4.android.ui.ladder

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.ui.Life4Divider
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.laddergoals.LadderGoalsConfig
import com.perrigogames.life4.feature.laddergoals.LadderGoalsViewModel
import com.perrigogames.life4.feature.laddergoals.UILadderData
import com.perrigogames.life4.feature.laddergoals.UILadderDetailItem
import com.perrigogames.life4.feature.laddergoals.UILadderGoal
import com.perrigogames.life4.feature.laddergoals.UILadderGoals
import com.perrigogames.life4.feature.laddergoals.UILadderMocks
import com.perrigogames.life4.feature.laddergoals.UILadderProgress
import com.perrigogames.life4.feature.laddergoals.why
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.colorResource

@Composable
fun LadderGoalsScreen(
    targetRank: LadderRank?,
    modifier: Modifier = Modifier,
    viewModel: LadderGoalsViewModel = viewModel(
        factory = createViewModelFactory { LadderGoalsViewModel(LadderGoalsConfig(targetRank)) }
    )
) {
    val state by viewModel.stateFlow.collectAsState()

    LadderGoals(
        data = state,
        onCompletedChanged = {},
        onHiddenChanged = {},
        modifier = modifier,
    )
}

@Composable
fun LadderGoals(
    data: UILadderData,
    onCompletedChanged: (Long) -> Unit,
    onHiddenChanged: (Long) -> Unit,
//    onExpandChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(all = 8.dp),
    ) {
        when (val goals = data.goals) {
            is UILadderGoals.SingleList -> {
                itemsIndexed(goals.items) { idx, goal ->
                    if (idx > 0) {
                        SizedSpacer(size = 4.dp)
                    }
                    LadderGoalItem(
                        goal = goal,
                        allowCompleting = data.allowCompleting,
                        allowHiding = data.allowHiding,
                        onCompletedChanged = onCompletedChanged,
                        onHiddenChanged = onHiddenChanged,
                        modifier = Modifier.fillParentMaxWidth(),
                    )
                }
            }
            is UILadderGoals.CategorizedList -> item { Text("FIXME") }
        }
    }
}

@Composable
fun LadderGoalItem(
    goal: UILadderGoal,
    expanded: Boolean = false,
    allowCompleting: Boolean = true,
    allowHiding: Boolean = true,
    onCompletedChanged: (Long) -> Unit,
    onHiddenChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(expanded) }
    val isHidden by remember { derivedStateOf { goal.hidden } }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isHidden) 0.5f else 1f),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isHidden) 0.5f else 1f)
        ) {
            LadderGoalHeaderRow(
                goal = goal,
                allowCompleting = allowCompleting,
                allowHiding = allowHiding,
                onCompletedChanged = onCompletedChanged,
                onHiddenChanged = onHiddenChanged,
            )
            if (goal.detailItems.isNotEmpty()) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(expandFrom = Alignment.Top),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top),
                ) {
                    Column {
                        Life4Divider()
                        SizedSpacer(VERTICAL_PADDING)
                        LadderGoalDetailShade(
                            items = goal.detailItems,
                            modifier = Modifier
                                .padding(horizontal = HORIZONTAL_PADDING)
                                .padding(bottom = VERTICAL_PADDING)
                        )
                    }
                }
            }
            if (goal.progress != null) {
                LinearProgressIndicator(
                    color = androidx.compose.ui.res.colorResource(R.color.colorAccent),
                    trackColor = MaterialTheme.colorScheme.surface,
                    progress = goal.progress!!.progressPercent,
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun LadderGoalHeaderRow(
    goal: UILadderGoal,
    allowCompleting: Boolean = true,
    allowHiding: Boolean = true,
    onCompletedChanged: (Long) -> Unit,
    onHiddenChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = goal.goalText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = VERTICAL_PADDING)
                .padding(start = HORIZONTAL_PADDING)
        )
        goal.progress?.let { progress ->
            Text(
                text = progress.progressText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (allowCompleting) { // FIXME these should be fixed so they have a state when we show these too
            Checkbox(
                checked = goal.completed,
                onCheckedChange = { onCompletedChanged(goal.id) },
            )
        }
        if (allowHiding) { // FIXME these should be fixed so they have a state when we show these too
            Icon(
                painter = painterResource(R.drawable.ic_eye),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = if (goal.hidden) "Hidden" else "Visible",
                modifier = Modifier
                    .clickable { onHiddenChanged(goal.id) }
                    .safeContentPadding()
                    .padding(end = HORIZONTAL_PADDING)
            )
        }
    }
}

@Composable
private fun LadderGoalDetailShade(
    items: List<UILadderDetailItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items) { item ->
            Row(modifier = Modifier.fillParentMaxWidth()) {
                Text(
                    text = item.leftText,
                    color = item.leftColor?.let { colorResource(it) } ?: MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(item.leftWeight)
                )
                if (item.rightText != null) {
                    SizedSpacer(8.dp)
                    Text(
                        text = item.rightText!!,
                        color = item.rightColor?.let { colorResource(it) } ?: MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(item.rightWeight)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(widthDp = 480)
fun LadderGoalItemPreview() {
    LIFE4Theme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { with(UILadderMocks) {
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's."))
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's.", completed = true,))
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's.", hidden = true,))
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's.", completed = true, hidden = true,))
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's.", progress = UILadderProgress(count = 2, max = 10)))
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's.", progress = UILadderProgress(progressPercent = 0.2f, progressText = "200 /\n1000")))
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's.", progress = UILadderProgress(count = 7, max = 10)))
            previewGoalItem(createUILadderGoal(goalText = "Perfect full combo clear any 3 songs by NAOKI, kors k, or dj TAKA in a single session between 9PM-5AM local time during a full moon."))
        } }
    }
}

@Composable
@Preview(widthDp = 480)
fun LadderGoalItemDetailPreview() {
    LIFE4Theme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { with(UILadderMocks) {
            previewGoalItem(createUILadderGoal(goalText = "Clear any 10 L5's.", detailItems = detailItems))
            previewGoalItem(createUILadderGoal(
                goalText = "Clear any 10 L5's.",
                detailItems = detailItems,
                progress = UILadderProgress(count = 7, max = 10)
            ))
        } }
    }
}

@Composable
@Preview(widthDp = 480)
fun LadderGoalItemDetailVariantPreview() {
    LIFE4Theme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { with(UILadderMocks) {
            previewGoalItem(createUILadderGoal(
                goalText = "Clear any 10 L5's.",
                detailItems = detailItems,
                hidden = true,
            ))
            previewGoalItem(createUILadderGoal(
                goalText = "Clear any 10 L5's.",
                detailItems = detailItems.map {
                    it.copy(leftText = it.leftText.repeat(3))
                }
            ))
        } }
    }
}

@Composable
@Preview(widthDp = 480)
fun Why() {
    val data by remember { mutableStateOf(why.shuffled()) }
    LIFE4Theme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { with(UILadderMocks) {
            previewGoalItem(createUILadderGoal(
                goalText = "Perfect full combo clear any 3 songs by NAOKI, kors k, or dj TAKA in a single session between 9PM-5AM local time during a full moon.",
                detailItems = data,
                completed = true,
            ))
        } }
    }
}

val detailItems = with(UILadderMocks) { listOf(
    createSongDetailItem(songName = "L'amour et la libert&eacute;(DDR Ver.)", difficultyClass = DifficultyClass.BEGINNER),
    createSongDetailItem(songName = "LOVE&hearts;SHINE", difficultyClass = DifficultyClass.BASIC),
    createSongDetailItem(songName = "Miracle Moon ～L.E.D.LIGHT STYLE MIX～", difficultyClass = DifficultyClass.DIFFICULT),
    createSongDetailItem(songName = "PARANOIA survivor", difficultyClass = DifficultyClass.EXPERT),
    createSongDetailItem(songName = "PARANOIA survivor MAX", difficultyClass = DifficultyClass.CHALLENGE),
    createSongDetailItem(songName = "Pink Rose", difficultyClass = DifficultyClass.BEGINNER),
    createSongDetailItem(songName = "SO IN LOVE", difficultyClass = DifficultyClass.BASIC),
    createSongDetailItem(songName = "STAY (Organic house Version)", difficultyClass = DifficultyClass.DIFFICULT),
    createSongDetailItem(songName = "stoic (EXTREME version)", difficultyClass = DifficultyClass.EXPERT),
    createSongDetailItem(songName = "sync (EXTREME version)", difficultyClass = DifficultyClass.CHALLENGE),
    createSongDetailItem(songName = "TEARS"),
) }

@Composable
private fun previewGoalItem(
    goal: UILadderGoal,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val context = LocalContext.current
    LadderGoalItem(
        goal = goal,
        modifier = modifier,
        expanded = goal.detailItems.isNotEmpty(),
        onCompletedChanged = { Toast.makeText(context, "Completed changed: $it", Toast.LENGTH_SHORT).show() },
        onHiddenChanged = { Toast.makeText(context, "Hidden changed: $it", Toast.LENGTH_SHORT).show() },
    )
}

private val HORIZONTAL_PADDING = 12.dp
private val VERTICAL_PADDING = 12.dp
