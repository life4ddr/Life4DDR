package com.perrigogames.life4.feature.trials

import dev.icerock.moko.resources.desc.StringDesc

/**
 * A View state describing the Trial list and its contents
 */
data class UITrialList(
    val trials: List<Item> = emptyList()
) {
    sealed class Item {
        data class Trial(val data: UITrialJacket) : Item()
        data class Header(val text: StringDesc) : Item()
    }
}