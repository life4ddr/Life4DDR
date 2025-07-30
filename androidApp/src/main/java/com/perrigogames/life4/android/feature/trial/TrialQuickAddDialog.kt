package com.perrigogames.life4.android.feature.trial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.nameRes
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.view.UITargetRank
import dev.icerock.moko.resources.desc.color.asColorDesc
import dev.icerock.moko.resources.desc.desc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrialQuickAddDialog(
    availableRanks: List<TrialRank>,
    onSubmit: (TrialRank, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var trialRank by remember { mutableStateOf(TrialRank.ONYX) }
    val uiTrialRank by remember {
        derivedStateOf {
            UITargetRank.Selection(
                rank = trialRank,
                title = trialRank.nameRes.desc(),
                titleColor = trialRank.colorRes.asColorDesc(),
                rankGoalItems = emptyList(),
                availableRanks = availableRanks
            )
        }
    }
    var exScoreText by remember { mutableStateOf(TextFieldValue()) }
    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                RankDropdown(
                    viewData = uiTrialRank,
                    rankSelected = { rank -> trialRank = rank }
                )
                TextField(
                    value = exScoreText,
                    onValueChange = { exScoreText = it },
                    placeholder = { Text("EX Score") },
                )
                TextButton(
                    onClick = { onSubmit(trialRank, exScoreText.text.toInt()) },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Submit") }
            }
        }
    }
}