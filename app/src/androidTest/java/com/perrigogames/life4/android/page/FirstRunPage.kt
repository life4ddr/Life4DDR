package com.perrigogames.life4.android.page

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.perrigogames.life4.android.R

object FirstRunPage {
    val titleLabel = withId(R.id.text_title)
    val brandImage = withId(R.id.image_app_icon)
    val greetingsLabel = withId(R.id.text_greetings)
    val nameField = withId(R.id.field_name)
    val rivalField = withId(R.id.field_rival_code)
    val twitterField = withId(R.id.field_twitter)
    val placementRadio = withId(R.id.radio_method_placement)
    val selectRankRadio = withId(R.id.radio_method_selection)
    val noRankRadio = withId(R.id.radio_method_no_rank)
    val signInButton = withId(R.id.button_continue)
}

object ReturningUserPopup {
    val nameLabel = withId(R.id.text_player_name)
    val rivalLabel = withId(R.id.text_player_rival_code)
    val twitterLabel = withId(R.id.text_player_twitter)
    val rankImage = withId(R.id.image_rank)
}