package com.perrigogames.life4.android

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.perrigogames.life4.enums.LadderRank

// region Annotations

@Preview(
    name = "light mode",
    group = "light dark modes",
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name = "dark mode",
    group = "light dark modes",
    uiMode = UI_MODE_NIGHT_YES,
)
annotation class LightDarkModePreviews

@Preview(
    name = "light mode",
    group = "light dark modes",
    uiMode = UI_MODE_NIGHT_NO,
    showSystemUi = true,
)
@Preview(
    name = "dark mode",
    group = "light dark modes",
    uiMode = UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
annotation class LightDarkModeSystemPreviews

// endregion

// region Data Providers

class LadderRankParameterProvider : PreviewParameterProvider<LadderRank> {
    override val values = LadderRank.entries.asSequence()
}

class LadderRankLevel1ParameterProvider : PreviewParameterProvider<LadderRank> {
    override val values = LadderRank.entries.filter { it.classPosition == 1 }.asSequence()
}

class LadderRankLevel2ParameterProvider : PreviewParameterProvider<LadderRank> {
    override val values = LadderRank.entries.filter { it.classPosition == 2 }.asSequence()
}

class LadderRankLevel3ParameterProvider : PreviewParameterProvider<LadderRank> {
    override val values = LadderRank.entries.filter { it.classPosition == 3 }.asSequence()
}

class LadderRankLevel4ParameterProvider : PreviewParameterProvider<LadderRank> {
    override val values = LadderRank.entries.filter { it.classPosition == 4 }.asSequence()
}

class LadderRankLevel5ParameterProvider : PreviewParameterProvider<LadderRank> {
    override val values = LadderRank.entries.filter { it.classPosition == 5 }.asSequence()
}

// endregion