package com.perrigogames.life4.android.feature.ladder

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.stringResource
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.Life4Divider
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.ladder.*
import com.perrigogames.life4.util.ViewState
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.colorResource

@Composable
fun LadderGoalsScreen(
    modifier: Modifier = Modifier,
    targetRank: LadderRank? = null,
    viewModel: GoalListViewModel = viewModel(
        factory = createViewModelFactory { GoalListViewModel(GoalListConfig(targetRank)) }
    )
) {
    val state by viewModel.state.collectAsState()

    (state as? ViewState.Success)?.data?.let { data ->
        LadderGoals(
            data = data,
            onCompletedChanged = {},
            onHiddenChanged = {},
            onExpandChanged = {},
            modifier = modifier,
        )
    }
}

@Composable
fun LadderGoals(
    data: UILadderData,
    onCompletedChanged: (Long) -> Unit,
    onHiddenChanged: (Long) -> Unit,
    onExpandChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val goals = data.goals) {
        is UILadderGoals.SingleList -> {
            SingleGoalList(
                goals = goals,
                allowCompleting = data.allowCompleting,
                allowHiding = data.allowHiding,
                onCompletedChanged = onCompletedChanged,
                onHiddenChanged = onHiddenChanged,
                onExpandChanged = onExpandChanged,
                modifier = modifier,
            )
        }
        is UILadderGoals.CategorizedList -> {
            CategorizedList(
                goals = goals,
                allowCompleting = data.allowCompleting,
                allowHiding = data.allowHiding,
                onCompletedChanged = onCompletedChanged,
                onHiddenChanged = onHiddenChanged,
                onExpandChanged = onExpandChanged,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun SingleGoalList(
    goals: UILadderGoals.SingleList,
    allowCompleting: Boolean,
    allowHiding: Boolean,
    onCompletedChanged: (Long) -> Unit,
    onHiddenChanged: (Long) -> Unit,
    onExpandChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(all = 8.dp),
    ) {
        itemsIndexed(goals.items) { idx, goal ->
            if (idx > 0) {
                SizedSpacer(size = 4.dp)
            }
            LadderGoalItem(
                goal = goal,
                allowCompleting = allowCompleting,
                allowHiding = allowHiding,
                onCompletedChanged = onCompletedChanged,
                onHiddenChanged = onHiddenChanged,
                onExpandChanged = onExpandChanged,
                modifier = Modifier.fillParentMaxWidth(),
            )
        }
    }
}

@Composable
fun CategorizedList(
    goals: UILadderGoals.CategorizedList,
    allowCompleting: Boolean,
    allowHiding: Boolean,
    onCompletedChanged: (Long) -> Unit,
    onHiddenChanged: (Long) -> Unit,
    onExpandChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val aggregateItems = goals.categories
        .flatMap { (info, goals) -> listOf(info) + goals }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(all = 8.dp),
    ) {
        itemsIndexed(aggregateItems) { idx, item ->
            if (idx > 0) {
                SizedSpacer(size = 4.dp)
            }
            when(item) {
                is UILadderGoals.CategorizedList.Category -> {
                    Row(
                        modifier = Modifier.fillParentMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = item.title.toString(context)
                        )
                        item.goalText?.let {
                            Text(
                                text = it.toString(context)
                            )
                        }
                    }
                }
                is UILadderGoal -> {
                    LadderGoalItem(
                        goal = item,
                        expanded = item.detailItems.isNotEmpty(),
                        allowCompleting = allowCompleting,
                        allowHiding = allowHiding,
                        onCompletedChanged = onCompletedChanged,
                        onHiddenChanged = onHiddenChanged,
                        onExpandChanged = onExpandChanged,
                        modifier = Modifier.fillParentMaxWidth(),
                    )
                }
            }
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
    onExpandChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                onExpandChanged = onExpandChanged,
            )
            if (goal.detailItems.isNotEmpty()) {
                AnimatedVisibility(
                    visible = expanded,
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
                    color = colorResource(MR.colors.colorAccent),
                    trackColor = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.5f),
                    progress = { goal.progress!!.progressPercent },
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
    onExpandChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onExpandChanged(goal.id) },
    ) {
        Text(
            text = stringResource(goal.goalText),
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
        if (allowHiding && goal.canHide) { // FIXME these should be fixed so they have a state when we show these too
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth()) {
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
                        textAlign = TextAlign.End,
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
        onExpandChanged = { Toast.makeText(context, "Expanded changed: $it", Toast.LENGTH_SHORT).show() },
        onHiddenChanged = { Toast.makeText(context, "Hidden changed: $it", Toast.LENGTH_SHORT).show() },
    )
}

private val HORIZONTAL_PADDING = 12.dp
private val VERTICAL_PADDING = 12.dp
