package com.perrigogames.life4.android.feature.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.feature.profile.MainScreenProfileViewModel
import com.perrigogames.life4.feature.profile.UIMainScreenProfile
import com.perrigogames.life4.feature.profile.UIProfileInfoCard
import dev.icerock.moko.mvvm.createViewModelFactory

@Composable
fun MainScreenProfile(
    viewModel: MainScreenProfileViewModel =
        viewModel(
            factory = createViewModelFactory { MainScreenProfileViewModel() },
        ),
) {
}

@Composable
fun MainScreenProfileContent(state: UIMainScreenProfile) {
    Column {
        ProfileCard(
            state = state.infoCard,
            modifier = Modifier.fillMaxWidth(),
        )
//        LadderGoals(data = state.ladderData, onCompletedChanged = , onHiddenChanged = )
    }
}

@Composable
fun ProfileCard(
    state: UIProfileInfoCard,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = state.name,
                maxLines = 1,
            )
            Text(
                text = state.rivalCode,
                maxLines = 1,
            )
        }
    }
}
