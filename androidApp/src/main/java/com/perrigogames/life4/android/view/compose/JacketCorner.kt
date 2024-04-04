package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.perrigogames.life4.MR
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.enums.TrialJacketCorner
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun JacketCorner(
    corner: TrialJacketCorner,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
    ) {
        Image(
            painter = painterResource(when (corner) {
                TrialJacketCorner.NEW -> R.drawable.triangle_new
                TrialJacketCorner.EVENT -> R.drawable.triangle_event
                TrialJacketCorner.NONE -> error("Cannot make a JacketCorner for type NONE")
            }),
            contentDescription = "${corner.name} tag"
        )
        Text(
            text = stringResource(when (corner) {
                TrialJacketCorner.NEW -> MR.strings.new_tag
                TrialJacketCorner.EVENT -> MR.strings.event_tag
                TrialJacketCorner.NONE -> error("Cannot make a JacketCorner for type NONE")
            })
        )
    }
}

@Composable
@Preview(heightDp = 24)
fun JacketCornerPreview() {
    LIFE4Theme {
        Row {
            JacketCorner(TrialJacketCorner.NEW)
            JacketCorner(TrialJacketCorner.EVENT)
        }
    }
}