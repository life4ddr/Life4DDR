package com.perrigogames.life4.android.ui.profile

import androidx.compose.runtime.Composable

@Composable
fun HomeProfileScreen(
    viewModel: HomeProfileViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    HomeProfileScreen(
        state = state,
    )
}