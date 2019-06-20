package com.perrigogames.life4trials.db

import com.perrigogames.life4trials.db.GoalStatus.INCOMPLETE
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import java.util.*

@Entity
data class GoalStatusDB(@Id var goalId: Long,
                        var date: Date,
                        @Convert(converter = GoalStatusConverter::class, dbType = Int::class) var status: GoalStatus)


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