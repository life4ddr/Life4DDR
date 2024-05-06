package com.perrigogames.life4.feature.trials

/**
 * A View state describing the Trial list and its contents
 */
data class UITrialList(
    val trials: List<Item> = emptyList(),
) {
    sealed class Item {
        class Trial(val data: UITrialJacket) : Item() {
            override fun toString() = "Trial: ${data.trial.name}"
        }

        class Header(val text: String) : Item() {
            override fun toString() = "Header: $text"
        }
    }
}
