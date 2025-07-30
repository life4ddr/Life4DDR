package com.perrigogames.life4.feature.trials.provider

import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.perrigogames.life4.MR
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.nameRes
import com.perrigogames.life4.feature.songlist.Chart
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.trials.data.Trial
import com.perrigogames.life4.feature.trials.data.TrialSong
import com.perrigogames.life4.feature.trials.enums.TrialRank
import com.perrigogames.life4.feature.trials.view.UITrialSessionContent
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionAction
import com.perrigogames.life4.longNumberString
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.color.asColorDesc
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TrialContentProvider(private val trial: Trial) : KoinComponent {

    private val songDataManager: SongDataManager by inject()

    fun provideSummary() : UITrialSessionContent.Summary {
        return UITrialSessionContent.Summary(
            items = trial.songs.mapToSongInfoUrlPair().map { (chart, url) ->
                UITrialSessionContent.Summary.Item(
                    jacketUrl = url,
                    difficultyClassText = chart.difficultyClass.nameRes.desc(),
                    difficultyClassColor = chart.difficultyClass.colorRes.asColorDesc(),
                    difficultyNumberText = chart.difficultyNumber.toString().desc(),
                    summaryContent = null,
                )
            }
        )
    }

    fun provideMidSession(session: InProgressTrialSession, stage: Int, targetRank: TrialRank) : UITrialSessionContent.SongFocused {
        val currentSong = trial.songs[stage]
        val currentChart = findChart(currentSong)
            ?: throw IllegalStateException("Song info not found for ${currentSong.skillId} / ${currentSong.difficultyClass}")
        val currentGoalSet = trial.goalSet(targetRank)
        val requiredClear = currentGoalSet?.clearIndexed?.get(stage)
        val requiredScore = currentGoalSet?.scoreIndexed?.get(stage) ?: 0

        return UITrialSessionContent.SongFocused(
            items = trial.songs.mapIndexed { index, song ->
                val result = session.results[index]
                UITrialSessionContent.SongFocused.Item(
                    jacketUrl = song.url,
                    topText = result?.score?.longNumberString()?.desc(),
                    bottomBoldText = when {
                        index == stage -> MR.strings.next_caps.desc()
                        result != null -> StringDesc.ResourceFormatted(MR.strings.ex_score_string_format, result.exScore ?: 0)
                        else -> null
                    },
                    bottomTagColor = song.chart.difficultyClass.colorRes.asColorDesc(),
                    tapAction = if (session.hasResult(index)) {
                        TrialSessionAction.EditItem(index)
                    } else {
                        null
                    },
                )
            },
            focusedJacketUrl = trial.songs[stage].url,
            songTitleText = KsoupEntities.decodeHtml(currentSong.chart.song.title).desc(),
            difficultyClassText = currentChart.difficultyClass.nameRes.desc(),
            difficultyClassColor = currentChart.difficultyClass.colorRes.asColorDesc(),
            difficultyNumberText = currentChart.difficultyNumber.toString().desc(),
            exScoreText = StringDesc.ResourceFormatted(MR.strings.ex_score_string_format, currentSong.ex),
            reminder = when {
                currentGoalSet == null -> null
                requiredClear != ClearType.CLEAR -> {
                    when (requiredClear) {
                        null -> null
                        ClearType.FAIL -> MR.strings.trial_reminder_fail_allowed.desc()
                        else -> {
                            StringDesc.ResourceFormatted(
                                MR.strings.trial_reminder_clear_type,
                                requiredClear.uiName
                            )
                        }
                    }
                }
                requiredScore != 0 -> {
                    StringDesc.ResourceFormatted(
                        MR.strings.trial_reminder_score,
                        requiredScore.longNumberString(),
                    )
                }
                else -> null
            }
        )
    }

    fun provideFinalScreen(session: InProgressTrialSession) : UITrialSessionContent.Summary {
        return UITrialSessionContent.Summary(
            items = session.trial.songs.zip(session.results) { song, result ->
                UITrialSessionContent.Summary.Item(
                    jacketUrl = song.url,
                    difficultyClassText = song.chart.difficultyClass.nameRes.desc(),
                    difficultyClassColor = song.chart.difficultyClass.colorRes.asColorDesc(),
                    difficultyNumberText = song.chart.difficultyNumber.toString().desc(),
                    summaryContent = UITrialSessionContent.Summary.SummaryContent(
                        topText = result!!.score?.longNumberString()?.desc(),
                        bottomMainText = StringDesc.ResourceFormatted(MR.strings.ex_score_string_format, result.exScore ?: 0),
                        bottomSubText = StringDesc.ResourceFormatted(MR.strings.ex_score_max_string_format, song.ex),
                    ),
                )
            }
        )
    }

    private fun List<TrialSong>.mapToSongInfoUrlPair() : List<Pair<Chart, String?>> = map { song ->
        song.chart to song.url
    }

    private fun findChart(song: TrialSong) : Chart? = songDataManager.getChart(
        skillId = song.skillId,
        playStyle = PlayStyle.SINGLE,
        difficultyClass = song.difficultyClass,
    )
}