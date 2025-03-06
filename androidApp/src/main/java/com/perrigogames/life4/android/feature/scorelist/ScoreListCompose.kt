package com.perrigogames.life4.android.feature.scorelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.feature.banners.BannerContainer
import com.perrigogames.life4.android.util.SizedSpacer
import com.perrigogames.life4.feature.songresults.ScoreListViewModel
import com.perrigogames.life4.feature.songresults.UIScore
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.colorResource

@Composable
fun ScoreListScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreListViewModel = viewModel(
        factory = createViewModelFactory { ScoreListViewModel() }
    ),
    showSanbaiLogin: (String) -> Unit = {},
) {
    val state = viewModel.state.collectAsState()
    var filterShowing by remember { mutableStateOf(false)}

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val authUrl = viewModel.getSanbaiUrl()
                    showSanbaiLogin(authUrl)
                },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                BannerContainer(state.value.banner)
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = { filterShowing = !filterShowing }
                ) {
                    Text(text = "Filter")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (filterShowing) {
                FilterPane(
                    data = state.value.filter,
                    modifier = Modifier.padding(horizontal = 32.dp),
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
}

@Composable
fun ScoreEntry(data: UIScore) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = data.titleText,
                maxLines = 1,
            )
            Row {
                Text(
                    text = data.difficultyText.toString(context),
                    color = colorResource(data.difficultyColor),
                    modifier = Modifier.weight(1f),
                )
                SizedSpacer(4.dp)
                Text(
                    text = data.scoreText.toString(context),
                    color = colorResource(data.scoreColor),
                )
            }
        }
        val flareResource = data.flareLevel?.let { flareImageResource(it) }
        if (flareResource != null) {
            Image(
                painter = painterResource(flareResource),
                contentDescription = "Flare level ${data.flareLevel}",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            SizedSpacer(32.dp)
        }
    }
}

fun flareImageResource(level: Int): Int? = when(level) {
    1 -> R.drawable.flare_1
    2 -> R.drawable.flare_2
    3 -> R.drawable.flare_3
    4 -> R.drawable.flare_4
    5 -> R.drawable.flare_5
    6 -> R.drawable.flare_6
    7 -> R.drawable.flare_7
    8 -> R.drawable.flare_8
    9 -> R.drawable.flare_9
    10 -> R.drawable.flare_ex
    else -> null
}
