@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.perrigogames.life4.android.view.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.perrigogames.life4.android.R
import com.perrigogames.life4.data.ApiPlayer
import com.perrigogames.life4.model.PlayerManager

@Composable
fun PlayerFoundView(player: ApiPlayer) {
    val paddingLarge = dimensionResource(id = R.dimen.content_padding_large)
    val secondaryInfoStyle = TextStyle(
        fontStyle = FontStyle.Italic
    )

    Column {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingLarge)
        ) {
            RankImage(
                rank = player.rank,
                modifier = Modifier.padding(vertical = paddingLarge)
            )

            // Header text
            Column(
                modifier = Modifier
                    .padding(start = paddingLarge)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = player.name,
                    fontSize = dimensionResource(id = R.dimen.profile_found_name_size).value.sp,
                    //fontFamily = TODO,
                )
                player.playerRivalCode?.let { rivalCode ->
                    Text(
                        text = rivalCode,
                        style = secondaryInfoStyle,
                    )
                }
                player.twitterHandle?.let { twitter ->
                    Text(
                        text = twitter,
                        style = secondaryInfoStyle,
                    )
                }
            }
        }

        // Body text
        Text(
            text = stringResource(R.string.player_found_prompt),
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.text_med).value.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun PlayerFoundViewPreview() {
    AppCompatTheme {
        PlayerFoundView(PlayerManager.TEST_API_PLAYER)
    }
}
