package com.perrigogames.life4.android.feature.firstrun

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.feature.placements.PlacementDetailsViewModel
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun PlacementDetailsScreen(
    placementId: String,
    viewModel: PlacementDetailsViewModel =
        viewModel(
            factory = createViewModelFactory { PlacementDetailsViewModel(placementId) },
        ),
) {
}
