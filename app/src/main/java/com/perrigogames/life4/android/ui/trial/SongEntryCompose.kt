package com.perrigogames.life4.android.ui.trial

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
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun SongEntryControls(
    songIndex: Int,
    showAdvanced: Boolean,
    requireAllData: Boolean,
    viewModel: SongEntryViewModel = viewModel(
        factory = createViewModelFactory {
            SongEntryViewModel(songIndex, showAdvanced, requireAllData)
        }
    ),
) {
    val scoreText: String by viewModel.scoreText.collectAsState()
    val exScoreText: String by viewModel.exScoreText.collectAsState()
    val missesText: String by viewModel.missesText.collectAsState()
    val goodsText: String by viewModel.goodsText.collectAsState()
    val greatsText: String by viewModel.greatsText.collectAsState()
    val perfectsText: String by viewModel.perfectsText.collectAsState()

    val passedChecked: Boolean by viewModel.passedChecked.collectAsState()

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Paddings.SMALL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = scoreText,
                onValueChange = { viewModel.scoreText.value = it },
                modifier = Modifier.weight(0.5f),
                placeholder = {
                    Text(text = stringResource(id = R.string.score))
                }
            )
            TextField(
                value = exScoreText,
                onValueChange = { viewModel.exScoreText.value = it },
                modifier = Modifier.weight(0.25f),
                placeholder = {
                    Text(text = stringResource(id = R.string.ex_score))
                }
            )

            Checkbox(
                checked = passedChecked,
                onCheckedChange = { viewModel.passedChecked.value = it }
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Paddings.SMALL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = missesText,
                onValueChange = { viewModel.missesText.value = it },
                modifier = Modifier.weight(0.25f),
                placeholder = {
                    Text(text = stringResource(id = R.string.misses))
                }
            )
            TextField(
                value = goodsText,
                onValueChange = { viewModel.goodsText.value = it },
                modifier = Modifier.weight(0.25f),
                placeholder = {
                    Text(text = stringResource(id = R.string.goods))
                }
            )
            TextField(
                value = greatsText,
                onValueChange = { viewModel.greatsText.value = it },
                modifier = Modifier.weight(0.25f),
                placeholder = {
                    Text(text = stringResource(id = R.string.greats))
                }
            )
            TextField(
                value = perfectsText,
                onValueChange = { viewModel.perfectsText.value = it },
                modifier = Modifier.weight(0.25f),
                placeholder = {
                    Text(text = stringResource(id = R.string.perfects))
                }
            )
        }
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
            SongEntryControls(songIndex = 0, showAdvanced = true, requireAllData = false)
        }
    }
}