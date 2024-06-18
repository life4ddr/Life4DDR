package com.perrigogames.life4.android.feature.scorelist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.feature.songresults.ScoreListViewModel
import com.perrigogames.life4.feature.songresults.UIScore
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.colorResource

@Composable
fun ScoreListScreen(
    viewModel: ScoreListViewModel = viewModel(
        factory = createViewModelFactory { ScoreListViewModel() }
    ),
) {
    val state = viewModel.state.collectAsState()
    LazyColumn {
        items(state.value.scores) {
            ScoreEntry(it)
        }
    }
}

@Composable
fun ScoreEntry(data: UIScore) {
    Row {
        Text(
            text = data.leftText,
            color = colorResource(data.leftColor),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = data.rightText,
            color = colorResource(data.rightColor)
        )
    }
}