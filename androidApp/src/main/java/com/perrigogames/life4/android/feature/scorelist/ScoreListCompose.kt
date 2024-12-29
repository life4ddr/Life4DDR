package com.perrigogames.life4.android.feature.scorelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.util.SizedSpacer
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
    var filterShowing by remember { mutableStateOf(false)}

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = { filterShowing = !filterShowing }
        ) {
            Text(text = "Filter")
        }
        if (filterShowing) {
            FilterPane(
                data = state.value.filter,
                modifier = Modifier.padding(horizontal = 16.dp),
                onAction = { viewModel.handleFilterAction(it) }
            )
            SizedSpacer(16.dp)
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.value.scores) {
                ScoreEntry(it)
            }
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
