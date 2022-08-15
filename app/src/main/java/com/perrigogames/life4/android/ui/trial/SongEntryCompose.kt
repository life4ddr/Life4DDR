package com.perrigogames.life4.android.ui.trial

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4DDRTheme
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.viewmodel.SongEntryViewModel
import com.perrigogames.life4.viewmodel.SongEntryViewModel.InputFieldState
import dev.icerock.moko.mvvm.createViewModelFactory

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
                placeholderRes = R.string.score,
                modifier = Modifier.weight(0.4f),
                onTextValueChange = { viewModel.scoreText.value = it }
            )
            SongTextInput(
                text = exScoreText,
                state = exScoreState,
                placeholderRes = R.string.ex_score,
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
                    text = stringResource(id = R.string.passed),
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
                placeholderRes = R.string.misses,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.missesText.value = it }
            )
            SongTextInput(
                text = goodsText,
                state = goodsState,
                placeholderRes = R.string.goods,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.goodsText.value = it }
            )
            SongTextInput(
                text = greatsText,
                state = greatsState,
                placeholderRes = R.string.greats,
                modifier = Modifier.weight(0.25f),
                onTextValueChange = { viewModel.greatsText.value = it }
            )
            SongTextInput(
                text = perfectsText,
                state = perfectsState,
                placeholderRes = R.string.perfects,
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
    @StringRes placeholderRes: Int,
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
                Text(text = stringResource(id = placeholderRes))
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
            Text(text = stringResource(id = R.string.clear))
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = { onClick(SongClearButtonType.FC) }
        ) {
            Text(text = stringResource(id = R.string.clear_fc_short))
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = { onClick(SongClearButtonType.PFC) }
        ) {
            Text(text = stringResource(id = R.string.clear_pfc_short))
        }
        Button(
            modifier = Modifier.weight(1f),
            onClick = { onClick(SongClearButtonType.MFC) }
        ) {
            Text(text = stringResource(id = R.string.clear_mfc_short))
        }
    }
}

enum class SongClearButtonType {
    CLEAR, FC, PFC, MFC
}

@Composable
@Preview
fun SongClearWidgetsPreview() {
    LIFE4DDRTheme {
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