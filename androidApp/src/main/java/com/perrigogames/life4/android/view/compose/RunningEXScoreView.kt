package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.R
import com.perrigogames.life4.data.TrialEXProgress
import dev.icerock.moko.resources.compose.colorResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun RunningEXScore(
    progress: TrialEXProgress,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        val missingText = stringResource(
            MR.strings.ex_score_missing_string_format,
            progress.currentExScore,
            progress.missingExScore * -1,
        )
        val goalText = if (progress.currentMaxExScore == progress.maxExScore)
            stringResource(
                MR.strings.ex_score_string_format,
                progress.currentMaxExScore,
            )
        else
            stringResource(
                MR.strings.ex_score_progress_format,
                progress.currentMaxExScore,
                progress.maxExScore,
            )

        Text(text = missingText, maxLines = 1)
        LinearProgressIndicator(
            progress = progress.currentExPercent,
            modifier = Modifier
                .height(8.dp)
                .align(Alignment.CenterVertically)
                .padding(horizontal = dimensionResource(R.dimen.content_padding_med))
                .weight(1F),
            color = colorResource(MR.colors.difficultyExpert),
            //FIXME background, background color, second progress
        )
        Text(text = goalText, maxLines = 1)
    }
}

@Preview
@Composable
fun RunningEXScorePreview() {
    AppCompatTheme {
        RunningEXScore(
            progress = TrialEXProgress(
                currentExScore = 350,
                currentMaxExScore = 550,
                maxExScore = 600,
            )
        )
    }
}