package com.perrigogames.life4trials.util

import com.perrigogames.life4.enums.ClearType
import com.perrigogames.life4.enums.DifficultyClass
import com.perrigogames.life4.enums.GameVersion
import com.perrigogames.life4.enums.PlayStyle
import io.objectbox.converter.PropertyConverter

class DifficultyClassConverter: PropertyConverter<DifficultyClass, Long> {
    override fun convertToDatabaseValue(property: DifficultyClass?): Long = (property ?: DifficultyClass.BEGINNER).stableId
    override fun convertToEntityProperty(id: Long?): DifficultyClass? = DifficultyClass.parse(id)
}

class ClearTypeConverter: PropertyConverter<ClearType, Long> {
    override fun convertToDatabaseValue(property: ClearType?): Long = (property ?: ClearType.NO_PLAY).stableId
    override fun convertToEntityProperty(id: Long?): ClearType? = ClearType.parse(id)
}

class GameVersionConverter: PropertyConverter<GameVersion, Long> {
    override fun convertToDatabaseValue(property: GameVersion?): Long = (property ?: GameVersion.UNKNOWN).stableId
    override fun convertToEntityProperty(id: Long?): GameVersion? = GameVersion.parse(id)
}

class PlayStyleConverter: PropertyConverter<PlayStyle, Long> {
    override fun convertToDatabaseValue(property: PlayStyle?): Long = (property ?: PlayStyle.SINGLE).stableId
    override fun convertToEntityProperty(id: Long?): PlayStyle? = PlayStyle.parse(id)
}
