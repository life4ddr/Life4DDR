package com.perrigogames.life4trials.db

import com.perrigogames.life4trials.db.GoalStatus.INCOMPLETE
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Uid
import io.objectbox.converter.PropertyConverter
import java.util.*

@Entity @Uid(8378601917918727635L)
data class GoalStatusDB(val goalId: Long,
                        @Convert(converter = GoalStatusConverter::class, dbType = Int::class) var status: GoalStatus = INCOMPLETE,
                        var date: Date = Date(),
                        @Id var id: Long = 0)


class GoalStatusConverter: PropertyConverter<GoalStatus, Int> {
    override fun convertToDatabaseValue(status: GoalStatus?): Int = status?.id ?: INCOMPLETE.id
    override fun convertToEntityProperty(id: Int?): GoalStatus = GoalStatus.from(id) ?: INCOMPLETE
}


enum class GoalStatus(val id: Int) {
    INCOMPLETE(0), COMPLETE(1), IGNORED(2);

    companion object {
        fun from(id: Int?) = values().firstOrNull { it.id == id }
    }
}