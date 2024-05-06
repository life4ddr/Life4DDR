package com.perrigogames.life4.android.activity.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.viewmodel.VersionsDialogViewModel
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun VersionsDialog(
    viewModel: VersionsDialogViewModel =
        viewModel(
            factory = createViewModelFactory { VersionsDialogViewModel() },
        ),
    onDismiss: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    VersionsDialog(
        appVersion = state.appVersion,
        ignoreListVersion = state.ignoreListVersion,
        ladderDataVersion = state.ladderDataVersion,
        motdVersion = state.motdVersion,
        songListVersion = state.songListVersion,
        trialDataVersion = state.trialDataVersion,
        onDismiss = onDismiss,
    )
}

@Composable
fun VersionsDialog(
    appVersion: String,
    ignoreListVersion: String,
    ladderDataVersion: String,
    motdVersion: String,
    songListVersion: String,
    trialDataVersion: String,
    onDismiss: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Column {
            Text(text = "App version: $appVersion")
            Text(text = "Ignore list version: $ignoreListVersion")
            Text(text = "Ladder data version: $ladderDataVersion")
            Text(text = "MOTD version: $motdVersion")
            Text(text = "Song list version: $songListVersion")
            Text(text = "Trial data version: $trialDataVersion")
        }
    }
}

@Composable
@Preview
fun VersionsDialogPreview() {
    LIFE4Theme {
        VersionsDialog(
            appVersion = "1.0.0",
            ignoreListVersion = "1.0.0",
            ladderDataVersion = "1.0.0",
            motdVersion = "1.0.0",
            songListVersion = "1.0.0",
            trialDataVersion = "1.0.0",
            onDismiss = {},
        )
    }
}
