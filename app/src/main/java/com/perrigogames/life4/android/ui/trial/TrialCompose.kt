package com.perrigogames.life4.android.ui.trial

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.perrigogames.life4.android.R
import com.perrigogames.life4.android.compose.FontFamilies
import com.perrigogames.life4.android.compose.FontSizes
import com.perrigogames.life4.android.compose.Paddings
import com.perrigogames.life4.android.util.jacketResId
import com.perrigogames.life4.android.view.TrialJacketView
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.viewmodel.TrialJacketViewModel
import com.perrigogames.life4.viewmodel.TrialListState

@Composable
fun TrialJacketList(
    displayList: List<TrialListState.Item>,
    onTrialSelected: (Trial) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(Paddings.MEDIUM),
        horizontalArrangement = Arrangement.spacedBy(Paddings.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Paddings.MEDIUM),
    ) {
        displayList.forEach { displayItem ->
            when (displayItem) {
                is TrialListState.Item.Header -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Text(
                        text = displayItem.text,
                        fontSize = FontSizes.SMALL,
                        fontFamily = FontFamilies.AVENIR_NEXT,
                    )
                }
                is TrialListState.Item.Trial -> item {
                    TrialJacket(
                        viewModel = displayItem.viewModel,
                        onClick = onTrialSelected,
                    )
                }
            }
        }
    }
}

@Composable
fun TrialJacket(
    viewModel: TrialJacketViewModel,
    onClick: (Trial) -> Unit,
) {
    AndroidView(
        factory = { context ->
            TrialJacketView(context).apply { bind(viewModel) }
        },
        modifier = Modifier.clickable {
            onClick(viewModel.trial)
        }
    )
}

@Composable
fun TrialJacketCompose(
    viewModel: TrialJacketViewModel,
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
    viewModel: TrialJacketViewModel,
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
