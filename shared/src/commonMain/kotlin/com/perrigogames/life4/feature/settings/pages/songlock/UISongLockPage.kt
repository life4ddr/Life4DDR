package com.perrigogames.life4.feature.settings.pages.songlock

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

data class UISongLockPage(
    val title: StringDesc = "".desc(),
    val sections: List<UISongLockSection> = emptyList(),
)

data class UISongLockSection(
    val title: StringDesc,
    val charts: List<StringDesc>
)
