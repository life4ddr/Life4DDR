package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.MR
import com.perrigogames.life4.data.InProgressTrialSession
import com.perrigogames.life4.data.Trial
import com.perrigogames.life4.data.TrialSong
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.colorRes
import com.perrigogames.life4.enums.nameRes
import com.perrigogames.life4.feature.songlist.Chart
import com.perrigogames.life4.feature.songlist.SongDataManager
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
            },
            buttonText = MR.strings.placement_start.desc(),
            buttonAction = TrialSessionAction.StartTrial,
        )
    }

    fun provideMidSession(session: InProgressTrialSession, stage: Int) : UITrialSessionContent.SongFocused {
        val currentSong = trial.songs[stage]
        val currentChart = findChart(currentSong)
            ?: throw IllegalStateException("Song info not found for ${currentSong.skillId} / ${currentSong.difficultyClass}")
        return UITrialSessionContent.SongFocused(
            items = trial.songs.mapIndexed { index, song ->
                val result = session.results[index]
                UITrialSessionContent.SongFocused.Item(
                    jacketUrl = song.url,
                    topText = result?.score?.longNumberString()?.desc(),
                    bottomBoldText = when {
                        index == stage -> MR.strings.next_caps.desc()
                        result != null -> StringDesc.ResourceFormatted(MR.strings.ex_score_string_format, result.exScore)
                        else -> null
                    },
                    bottomTagColor = song.chart.difficultyClass.colorRes.asColorDesc(),
                )
            },
            focusedJacketUrl = trial.songs[stage].url,
            songTitleText = currentSong.chart.song.title.desc(),
            difficultyClassText = currentChart.difficultyClass.nameRes.desc(),
            difficultyClassColor = currentChart.difficultyClass.colorRes.asColorDesc(),
            difficultyNumberText = currentChart.difficultyNumber.toString().desc(),
            buttonText = MR.strings.take_photo.desc(),
            buttonAction = TrialSessionAction.TakePhoto,
        )
    }

    fun provideFinalScreen() : UITrialSessionContent.Summary {
        TODO()
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