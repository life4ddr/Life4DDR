package com.perrigogames.life4.android.feature.trial

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.android.view.compose.RankImage
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.enums.nameRes
import com.perrigogames.life4.feature.trials.view.UITargetRank
import dev.icerock.moko.resources.desc.color.getColor

@Composable
fun RankSelector(
    viewData: UITargetRank,
    rankSelected: (TrialRank) -> Unit = {},
) {
    val context = LocalContext.current
    var dropdownExpanded: Boolean by remember { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                CardRankDisplay(
                    viewData = viewData,
                    showSelectorIcon = viewData is UITargetRank.Selection,
                    modifier = Modifier.clickable(
                        enabled = viewData is UITargetRank.Selection,
                    ) { dropdownExpanded = true }
                )
                if (viewData is UITargetRank.Selection) {
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                    ) {
                        viewData.availableRanks.forEach { rank ->
                            DropdownMenuItem(
                                text = { Text(rank.nameRes.getString(context)) },
                                leadingIcon = {
                                    RankImage(
                                        rank = rank.parent,
                                        size = 32.dp
                                    )
                                },
                                onClick = {
                                    rankSelected(rank)
                                    dropdownExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }

        SizedSpacer(8.dp)
        AnimatedContent(targetState = viewData.rankGoalItems) { items ->
            Column {
                items.forEach { goal ->
                    Text(
                        text = goal.toString(context)
                    )
                }
            }
        }
    }
}

@Composable
fun CardRankDisplay(
    viewData: UITargetRank,
    showSelectorIcon: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = modifier,
    ) {
        RankDisplay(
            viewData = viewData,
            showSelectorIcon = showSelectorIcon,
            rankImageSize = 32.dp,
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun RankDisplay(
    viewData: UITargetRank,
    showSelectorIcon: Boolean,
    rankImageSize: Dp,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AnimatedContent(
            targetState = viewData,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { viewData ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RankImage(
                    rank = viewData.rank.parent,
                    size = rankImageSize,
                )
                Text(
                    text = viewData.title.toString(context),
                    color = Color(viewData.titleColor.getColor(context)),
                    style = textStyle,
                )
            }
        }

        AnimatedVisibility(visible = showSelectorIcon) {
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select rank")
        }
        SizedSpacer(4.dp)
    }
}
