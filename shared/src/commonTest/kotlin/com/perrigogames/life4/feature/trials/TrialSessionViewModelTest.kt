package com.perrigogames.life4.feature.trials

import com.perrigogames.life4.db.SelectBestSessions
import com.perrigogames.life4.enums.LadderRank
import com.perrigogames.life4.feature.profile.UserRankManager
import com.perrigogames.life4.feature.songlist.Chart
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.enums.TrialRank
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.perrigogames.life4.feature.trials.manager.TrialDataManager
import com.perrigogames.life4.feature.trials.manager.TrialRecordsManager
import com.perrigogames.life4.feature.trials.view.UITrialSession
import com.perrigogames.life4.feature.trials.viewmodel.SongEntryViewModel.Companion.ID_EX_SCORE
import com.perrigogames.life4.feature.trials.viewmodel.SongEntryViewModel.Companion.ID_GOODS
import com.perrigogames.life4.feature.trials.viewmodel.SongEntryViewModel.Companion.ID_GREATS
import com.perrigogames.life4.feature.trials.viewmodel.SongEntryViewModel.Companion.ID_MISSES
import com.perrigogames.life4.feature.trials.viewmodel.SongEntryViewModel.Companion.ID_PERFECTS
import com.perrigogames.life4.feature.trials.viewmodel.SongEntryViewModel.Companion.ID_SCORE
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionAction
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionViewModel
import com.perrigogames.life4.util.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.generated.fake
import org.kodein.mock.generated.injectMocks
import org.kodein.mock.tests.TestsWithMocks
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@UsesFakes(Chart::class)
class TrialSessionViewModelTest : TestsWithMocks() {
    override fun setUpMocks() = mocker.injectMocks(this)

    @Mock lateinit var songDataManager: SongDataManager
    @Mock lateinit var trialDataManager: TrialDataManager
    @Mock lateinit var trialRecordsManager: TrialRecordsManager
    @Mock lateinit var userRankManager: UserRankManager

    private lateinit var subject: TrialSessionViewModel
    private val session get() = (subject.state.value as ViewState.Success<UITrialSession>).data

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                module {
                    single { songDataManager }
                    single { trialDataManager }
                    single { trialRecordsManager }
                    single { userRankManager }
                }
            )
        }

        every { songDataManager.getChart(isAny(), isAny(), isAny()) } returns fake<Chart>()
    }

    fun TestScope.customSetup(
        rank: LadderRank? = null,
    ) {
        val trials = Json.decodeFromString<List<Trial>>(TRIAL_DATA)
            .map { trial ->
                trial.copy(songs = trial.songs.map { song ->
                    song.chart = fake<Chart>()
                    song
                })
            }
        every { trialDataManager.trialsFlow } returns flowOf(trials)
            .stateIn(this, SharingStarted.Eagerly, trials)

        every { trialRecordsManager.bestSessions } returns flowOf<List<SelectBestSessions>>(emptyList())
            .stateIn(this, SharingStarted.Eagerly, emptyList())

        every { userRankManager.rank } returns flowOf(rank)
            .stateIn(this, SharingStarted.Eagerly, rank)
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test a slowly descending level`() = runTest {
        customSetup()
        subject = TrialSessionViewModel(TRIAL_ID)

        assertEquals(TrialRank.SILVER, session.targetRank.rank)
        subject.handleAction(TrialSessionAction.ChangeTargetRank(TrialRank.ONYX))
        subject.handleAction(TrialSessionAction.StartTrial)
        advanceUntilIdle()

        subject.handleAction(TrialSessionAction.PhotoTaken("", 0))
        setStats(ex = 1712) // total -55
        advanceUntilIdle()
        assertEquals(1712, session.exScoreBar.currentEx)
        assertEquals(TrialRank.ONYX, session.targetRank.rank)

        subject.handleAction(TrialSessionAction.PhotoTaken("", 1))
        setStats(ex = 1816) // total -177
        advanceUntilIdle()
        assertEquals(3528, session.exScoreBar.currentEx)
        assertEquals(TrialRank.AMETHYST, session.targetRank.rank)

        subject.handleAction(TrialSessionAction.PhotoTaken("", 2))
        setStats(ex = 1917) // total -258
        advanceUntilIdle()
        assertEquals(5445, session.exScoreBar.currentEx)
        assertEquals(TrialRank.PEARL, session.targetRank.rank)

        subject.handleAction(TrialSessionAction.PhotoTaken("", 3))
        setStats(ex = 1475) // total -316
        advanceUntilIdle()
        assertEquals(6920, session.exScoreBar.currentEx)
        assertEquals(TrialRank.COBALT, session.targetRank.rank)
    }

    private fun setStats(
        score: Int? = null,
        ex: Int,
        misses: Int? = null,
        goods: Int? = null,
        greats: Int? = null,
        perfects: Int? = null,
    ) {
        score?.let { subject.handleAction(TrialSessionAction.ChangeText(ID_SCORE, it.toString())) }
        subject.handleAction(TrialSessionAction.ChangeText(ID_EX_SCORE, ex.toString()))
        misses?.let { subject.handleAction(TrialSessionAction.ChangeText(ID_MISSES, it.toString())) }
        goods?.let { subject.handleAction(TrialSessionAction.ChangeText(ID_GOODS, it.toString())) }
        greats?.let { subject.handleAction(TrialSessionAction.ChangeText(ID_GREATS, it.toString())) }
        perfects?.let { subject.handleAction(TrialSessionAction.ChangeText(ID_PERFECTS, it.toString())) }
        subject.handleAction(TrialSessionAction.AdvanceStage)
    }

    companion object {
        const val TRIAL_ID = "rendition"
        const val TRIAL_DATA = """
[
    {
        "id": "rendition",
        "name": "Rendition",
        "type": "trial",
        "songs": [
            { "skillId": "", "difficulty_class": "expert", "ex": 1767, "url": "" },
            { "skillId": "", "difficulty_class": "expert", "ex": 1938, "url": "" },
            { "skillId": "", "difficulty_class": "expert", "ex": 1998, "url": "" },
            { "skillId": "", "difficulty_class": "challenge", "ex": 1533, "url": "" }
        ],
        "difficulty": 15,
        "goals": [
            {
                "rank": "silver",
                "clear_indexed": [ "clear", "clear", "clear", "fail" ],
                "score_indexed": [ 0, 0, 0, 300000 ]
            },
            {
                "rank": "gold",
                "score": [ 900000, 900000, 900000, 860000 ]
            },
            {
                "rank": "platinum",
                "score": [ 965000, 965000, 965000, 920000 ]
            },
            {
                "rank": "diamond",
                "score": [ 975000, 975000, 975000, 955000 ],
                "ex_missing": 450
            },
            {
                "rank": "cobalt",
                "score": [ 980000, 980000, 980000, 970000 ],
                "ex_missing": 320
            },
            {
                "rank": "pearl",
                "judge": 16,
                "ex_missing": 270
            },
            {
                "rank": "amethyst",
                "ex_missing": 200
            },
            {
                "rank": "emerald",
                "ex_missing": 160
            },
            {
                "rank": "onyx",
                "ex_missing": 120
            }
        ],
        "total_ex": 7236
    }
]
        """
    }
}