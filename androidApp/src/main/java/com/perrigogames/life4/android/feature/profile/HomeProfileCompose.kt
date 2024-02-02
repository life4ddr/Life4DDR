package com.perrigogames.life4.android.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.viewmodel.HomeProfileViewModel
import com.perrigogames.life4.viewmodel.UIHomeProfile
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun HomeProfileScreen(
    viewModel: HomeProfileViewModel = viewModel(
        factory = createViewModelFactory { HomeProfileViewModel() }
    ),
) {
    val state by viewModel.state.collectAsState()

    HomeProfileScreen(
        state = state,
    )
}

fun HomeProfileScreen(
    state: UIHomeProfile
) {
    // TODO
}