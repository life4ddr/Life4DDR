package com.perrigogames.life4.feature.settings.pages.songlock

import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.feature.songlist.SongDataManager
import com.perrigogames.life4.feature.songresults.ChartResultOrganizer
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SongLockPageProvider(): ViewModel(), KoinComponent {

    private val songDataManager: SongDataManager by inject()

    val data: CStateFlow<UISongLockPage> = songDataManager.libraryFlow
        .map { library ->
            library.charts
                .filter { it.playStyle == PlayStyle.SINGLE && it.lockType != null && it.lockType != 0 }
                .groupBy { it.lockType }
                .mapValues { (_, charts) ->
                    charts.groupBy { it.song }
                }
        }
        .map { lockTypes ->
            UISongLockPage(
                title = "Locked Songs".desc(), // FIXME
                sections = lockTypes.entries.map { (lockType, songs) ->
                    UISongLockSection(
                        title = ChartResultOrganizer.lockTypeName(lockType).desc(),
                        charts = songs.map { (song, charts) ->
                            val diffText = charts.joinToString {
                                val classString = it.difficultyClass.aggregateString(it.playStyle)
                                "$classString/${it.difficultyNumber}"
                            }
                            "${song.title} ($diffText)".desc()
                        }
                    )
                }
            )
        }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, UISongLockPage())
        .cStateFlow()
}