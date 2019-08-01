package com.perrigogames.life4trials.db

import com.perrigogames.life4trials.data.*
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
class SongDB(var title: String,
             var artist: String? = null,
             @Convert(converter = GameVersionConverter::class, dbType = Long::class) var version: GameVersion? = null,
             @Id var id: Long = 0) {

    @Backlink(to = "song")
    lateinit var charts: ToMany<ChartDB>
}

@Entity
class ChartDB(@Convert(converter = DifficultyClassConverter::class, dbType = Long::class) var difficultyClass: DifficultyClass = DifficultyClass.BEGINNER,
              var difficultyNumber: Int = 0,
              @Convert(converter = PlayStyleConverter::class, dbType = Long::class) var playStyle: PlayStyle = PlayStyle.SINGLE,
              @Id var id: Long = 0) {

    lateinit var song: ToOne<SongDB>

    @Backlink(to = "chart")
    lateinit var plays: ToMany<LadderResultDB>
}

@Entity
class LadderResultDB(var score: Int = 0,
                     @Convert(converter = ClearTypeConverter::class, dbType = Long::class) var clearType: ClearType = ClearType.NO_PLAY,
                     @Id var id: Long = 0) {

    lateinit var chart: ToOne<ChartDB>
}