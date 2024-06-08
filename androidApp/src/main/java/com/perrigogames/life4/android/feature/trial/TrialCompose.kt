package com.perrigogames.life4.android.feature.trial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.FontFamilies
import com.perrigogames.life4.android.compose.FontSizes
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.util.jacketResId
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.feature.trials.TrialListViewModel
import com.perrigogames.life4.feature.trials.UITrialJacket
import com.perrigogames.life4.feature.trials.UITrialList
import dev.icerock.moko.mvvm.createViewModelFactory
import dev.icerock.moko.resources.compose.localized

@Composable
fun TrialListScreen(
    modifier: Modifier = Modifier,
    viewModel: TrialListViewModel = viewModel(
        factory = createViewModelFactory { TrialListViewModel() }
    ),
    onTrialSelected: (Trial) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    TrialJacketList(
        displayList = state!!.trials, // FIXME
        onTrialSelected = onTrialSelected,
        modifier = modifier,
    )
}

@Composable
fun TrialJacketList(
    displayList: List<UITrialList.Item>,
    onTrialSelected: (Trial) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(Paddings.MEDIUM),
        horizontalArrangement = Arrangement.spacedBy(Paddings.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Paddings.MEDIUM),
        modifier = modifier,
    ) {
        displayList.forEach { displayItem ->
            when (displayItem) {
                is UITrialList.Item.Header -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Text(
                        text = displayItem.text.localized(),
                        fontSize = FontSizes.SMALL,
                        fontFamily = FontFamilies.AVENIR_NEXT,
                    )
                }
                is UITrialList.Item.Trial -> item {
                    TrialJacket(
                        viewModel = displayItem.data,
                        onClick = onTrialSelected,
                    )
                }
            }
        }
    }
}

@Composable
fun TrialJacket(
    viewModel: UITrialJacket,
    onClick: (Trial) -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = modifier,
    ) {
        val (image, difficulty) = createRefs()
        Image(
            painter = painterResource(id = viewModel.trial.jacketResId(LocalContext.current)),
            contentDescription = null,
            modifier = Modifier.aspectRatio(1f)
//                .constrainAs(image) {
//                }
        )
        TrialDifficulty(
            viewModel = viewModel,
            modifier = Modifier.constrainAs(difficulty) {
                top.linkTo(parent.top, margin = Paddings.SMALL)
                start.linkTo(parent.start, margin = Paddings.SMALL)
            }
        )
    }
}

@Composable
fun TrialDifficulty(
    viewModel: UITrialJacket,
    modifier: Modifier,
) {
    viewModel.trial.difficulty?.let { difficulty ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.size(40.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.circle),
                alpha = 0.5f,
                contentDescription = null,
            )
            Text(
                text = difficulty.toString(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
