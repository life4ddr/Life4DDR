package com.perrigogames.life4.android.feature.trial

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.feature.trials.SongEntryViewModel
import com.perrigogames.life4.feature.trials.SongEntryViewModel.InputFieldState
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SongEntryControls(
    viewModel: SongEntryViewModel,
) {
    val scoreText: String by viewModel.scoreText.collectAsState()
    val exScoreText: String by viewModel.exScoreText.collectAsState()
    val missesText: String by viewModel.missesText.collectAsState()
    val goodsText: String by viewModel.goodsText.collectAsState()
    val greatsText: String by viewModel.greatsText.collectAsState()
    val perfectsText: String by viewModel.perfectsText.collectAsState()

    val scoreState: InputFieldState by viewModel.scoreState.collectAsState()
    val exScoreState: InputFieldState by viewModel.exScoreState.collectAsState()
    val missesState: InputFieldState by viewModel.missesState.collectAsState()
    val goodsState: InputFieldState by viewModel.goodsState.collectAsState()
    val greatsState: InputFieldState by viewModel.greatsState.collectAsState()
    val perfectsState: InputFieldState by viewModel.perfectsState.collectAsState()

    val passedChecked: Boolean by viewModel.passedChecked.collectAsState()

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Paddings.SMALL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SongTextInput(
                text = scoreText,
                state = scoreState,
                placeholderRes = MR.strings.score,
                modifier = Modifier.weight(0.4f),
                onTextValueChange = { viewModel.scoreText.value = it }
            )
            SongTextInput(
                text = exScoreText,
                state = exScoreState,
                placeholderRes = MR.strings.ex_score,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.exScoreText.value = it }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = passedChecked,
                    onCheckedChange = { viewModel.passedChecked.value = it },
                )
                Text(
                    text = stringResource(MR.strings.passed),
                    modifier = Modifier.padding(end = Paddings.MEDIUM)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Paddings.SMALL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SongTextInput(
                text = missesText,
                state = missesState,
                placeholderRes = MR.strings.misses,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.missesText.value = it }
            )
            SongTextInput(
                text = goodsText,
                state = goodsState,
                placeholderRes = MR.strings.goods,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.goodsText.value = it }
            )
            SongTextInput(
                text = greatsText,
                state = greatsState,
                placeholderRes = MR.strings.greats,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.greatsText.value = it }
            )
            SongTextInput(
                text = perfectsText,
                state = perfectsState,
                placeholderRes = MR.strings.perfects,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.perfectsText.value = it }
            )
        }
    }
}

@Composable
private fun SongTextInput(
    text: String,
    state: InputFieldState,
    spaceInvisible: Boolean = true,
    placeholderRes: StringResource,
    modifier: Modifier = Modifier,
    onTextValueChange: (String) -> Unit,
) {
    if (state.visible) {
        TextField(
            value = text,
            enabled = state.enabled,
            isError = state.hasError,
            onValueChange = onTextValueChange,
            modifier = modifier,
            placeholder = {
                Text(text = stringResource(placeholderRes))
            }
        )
    } else if (spaceInvisible) {
        Spacer(modifier = modifier)
    }
}

@Composable
fun SongClearButtons(
    modifier: Modifier = Modifier,
    onClick: (SongClearButtonType) -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = Paddings.SMALL),
        horizontalArrangement = Arrangement.spacedBy(Paddings.SMALL),
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = { onClick(SongClearButtonType.CLEAR) }
        ) {
            Text(text = stringResource(MR.strings.clear))
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = { onClick(SongClearButtonType.FC) }
        ) {
            Text(text = stringResource(MR.strings.clear_fc_short))
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = { onClick(SongClearButtonType.PFC) }
        ) {
            Text(text = stringResource(MR.strings.clear_pfc_short))
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = { onClick(SongClearButtonType.MFC) }
        ) {
            Text(text = stringResource(MR.strings.clear_mfc_short))
        }
    }
}

enum class SongClearButtonType {
    CLEAR, FC, PFC, MFC
}

@Composable
@Preview
fun SongClearWidgetsPreview() {
    LIFE4Theme {
        Column {
            SongClearButtons(modifier = Modifier.fillMaxWidth()) {}
            SongEntryControls(viewModel(
                factory = createViewModelFactory {
                    SongEntryViewModel(0, SongEntryViewModel.EntryState.FULL, false)
                }
            ))
        }
    }
}