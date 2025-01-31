package com.perrigogames.life4.android.feature.trial

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.feature.trialsession.TrialSessionViewModel
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun TrialSession(
    trialId: String,
    viewModel: TrialSessionViewModel = viewModel(
        factory = createViewModelFactory { TrialSessionViewModel(trialId) }
    )
) {

}