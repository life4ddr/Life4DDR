package com.perrigogames.life4.android.feature.scorelist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songresults.UIFilterAction
import com.perrigogames.life4.feature.songresults.UIFilterView
import dev.icerock.moko.resources.compose.localized
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPane(
    data: UIFilterView,
    modifier: Modifier = Modifier,
    onAction: (UIFilterAction) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        data.playStyleSelector?.let { selector ->
            SingleChoiceSegmentedButtonRow {
                selector.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = item.selected,
                        onClick = { onAction(item.action) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = PlayStyle.entries.size),
                    ) {
                        Text(text = item.text.localized())
                    }
                }
            }
        }
        Row {
            data.difficultyClassSelector.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Switch(
                        checked = item.selected,
                        onCheckedChange = { onAction(item.action) }
                    )
                    Text(text = item.text.localized())
                }
            }
        }

        SizedSpacer(8.dp)
        Text(
            text = data.difficultyNumberTitle.localized(),
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.labelLarge,
        )
        RangeSlider(
            value = data.difficultyNumberRange.innerFloatRange,
            valueRange = data.difficultyNumberRange.outerFloatRange,
            steps = data.difficultyNumberRange.outerRange.count() - 2,
            onValueChange = { range ->
                onAction(
                    UIFilterAction.SetDifficultyNumberRange(range.start.roundToInt(), range.endInclusive.roundToInt())
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = data.difficultyNumberRange.innerRange.first().toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.1f)
            )
            Spacer(Modifier.weight(0.8f))
            Text(
                text = data.difficultyNumberRange.innerRange.last().toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.1f)
            )
        }

        SizedSpacer(8.dp)
        Text(
            text = data.clearTypeTitle.localized(),
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.labelLarge,
        )
        RangeSlider(
            value = data.clearTypeRange.innerFloatRange,
            valueRange = data.clearTypeRange.outerFloatRange,
            steps = data.clearTypeRange.outerRange.count() - 2,
            onValueChange = { range ->
                onAction(
                    UIFilterAction.SetClearTypeRange(range.start.roundToInt(), range.endInclusive.roundToInt())
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = ClearType.entries[data.clearTypeRange.innerRange.first()].uiName.localized(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.3f)
            )
            Spacer(Modifier.weight(0.2f))
            Text(
                text = ClearType.entries[data.clearTypeRange.innerRange.last()].uiName.localized(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.3f)
            )
        }

        SizedSpacer(8.dp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = data.scoreRangeBottomValue?.toString().orEmpty(),
                onValueChange = { onAction(UIFilterAction.SetScoreRange(first = it.trim().toInt())) },
                placeholder = { Text(text = data.scoreRangeBottomHint.localized()) },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = data.scoreRangeTopValue?.toString().orEmpty(),
                onValueChange = { onAction(UIFilterAction.SetScoreRange(last = it.toInt())) },
                placeholder = { Text(text = data.scoreRangeTopHint.localized()) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
