package com.perrigogames.life4.enums

import com.perrigogames.life4.data.StableId

/**
 * Enum class describing the possible states a ladder goal can be in.
 */
enum class GoalStatus(override val stableId: Long) : StableId {
    INCOMPLETE(0),
    COMPLETE(1),
    IGNORED(2),
    ;

    companion object {
        fun from(id: Long?) = entries.firstOrNull { it.stableId == id }
    }
}
