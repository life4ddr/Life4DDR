package com.perrigogames.life4.android.activity.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.perrigogames.life4.android.compose.LIFE4Theme
import com.perrigogames.life4.android.ui.trial.TrialJacketList
import com.perrigogames.life4.data.Song
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialState
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.TrialJacketCorner
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.enums.TrialType
import com.perrigogames.life4.viewmodel.TrialJacketViewModel
import com.perrigogames.life4.viewmodel.TrialListState

class TrialJacketTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LIFE4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TrialJacketList(
                        displayList = TEST_DATA,
                        onTrialSelected = {},
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LIFE4Theme {
        TrialJacketList(
            displayList = TEST_DATA,
            onTrialSelected = {},
        )
    }
}

private val TEST_DATA: List<TrialListState.Item> = listOf(
    testTrialItem(
        trial = testTrial(1, difficulty = 9),
        rank = null,
        exScore = null,
    ),
    testTrialItem(
        trial = testTrial(2, difficulty = 13),
        rank = TrialRank.SILVER,
        exScore = 3000,
    ),
    testTrialItem(
        trial = testTrial(3, difficulty = 16),
        overrideCorner = TrialJacketCorner.EVENT,
        rank = TrialRank.DIAMOND,
        exScore = 3500,
    ),
    testTrialItem(
        trial = testTrial(4, "Named Trial", difficulty = 18),
        rank = TrialRank.ONYX,
        exScore = 4000,
    ),
    testTrialItem(
        trial = testTrial(5, "Named Trial 2", difficulty = 18),
        overrideCorner = TrialJacketCorner.NEW,
        rank = TrialRank.ONYX,
        exScore = 4000,
    ),
)

private fun testTrialItem(
    trial: Trial,
    overrideCorner: TrialJacketCorner? = null,
    rank: TrialRank? = null,
    exScore: Int? = null,
) = TrialListState.Item.Trial(
    viewModel = TrialJacketViewModel(
        trial = trial,
        session = null, //FIXME
        overrideCorner = overrideCorner,
        rank = rank,
        exScore = exScore,
        tintOnRank = TrialRank.values().last()
    )
)

private fun testTrial(
    index: Int,
    name: String = "Test Trial $index",
    difficulty: Int,
    author: String? = null,
    state: TrialState = TrialState.ACTIVE,
) = Trial(
    id = "test_trial_$index",
    name = name,
    author = author,
    state = state,
    type = TrialType.TRIAL,
    songs = listOf(
        testSong(1, diff = 12, diffClass = DifficultyClass.BASIC, ex = 800),
        testSong(2, diff = 14, diffClass = DifficultyClass.DIFFICULT, ex = 1000),
        testSong(3, diff = 15, diffClass = DifficultyClass.EXPERT, ex = 1200),
        testSong(4, diff = 16, diffClass = DifficultyClass.CHALLENGE, ex = 1600),
    ),
    difficulty = difficulty,
)

private fun testSong(
    index: Int,
    name: String = "Song $index",
    diff: Int,
    diffClass: DifficultyClass,
    ex: Int,
) = Song(
    name = name,
    difficultyNumber = diff,
    difficultyClass = diffClass,
    ex = ex,
    url = null,
)