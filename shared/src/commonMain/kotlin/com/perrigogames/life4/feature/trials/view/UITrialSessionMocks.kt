package com.perrigogames.life4.feature.trials.view

import com.perrigogames.life4.MR
import com.perrigogames.life4.enums.TrialRank
import com.perrigogames.life4.feature.trials.viewmodel.TrialSessionAction
import dev.icerock.moko.resources.desc.color.ColorDescResource
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDescUrl

object UITrialSessionMocks {

    val initial = UITrialSession(
        trialTitle = "Sidequest".desc(),
        trialLevel = "LV 14".desc(),
        backgroundImage = ImageDescUrl("https://raw.githubusercontent.com/life4ddr/Life4DDR/develop/androidApp/src/main/res/drawable-xxhdpi/sidequest.webp"),
        exScoreBar = UIEXScoreBar(
            labelText = "EX".desc(),
            currentEx = 0,
            maxEx = 6762,
            currentExText = "0".desc(),
            maxExText = "/ 6762".desc(),
        ),
        targetRank = UITargetRank.InProgress(
            rank = TrialRank.COBALT,
            title = "COBALT".desc(),
            titleColor = ColorDescResource(MR.colors.cobalt),
            rankGoalItems = listOf(
                "20 or fewer Greats, Goods, or Misses".desc(),
                "230 missing EX or less (6532 EX)".desc(),
            ),
        ),
        content = UITrialSessionContent.Summary(
            items = listOf(
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Be_a_Hero!.webp",
                    difficultyClassText = "DIFFICULT".desc(),
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyDifficult),
                    difficultyNumberText = "13".desc(),
                ),
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Role-playing_game.webp",
                    difficultyClassText = "EXPERT".desc(),
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyExpert),
                    difficultyNumberText = "14".desc(),
                ),
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Kanata_no_Reflesia.webp",
                    difficultyClassText = "EXPERT".desc(),
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyExpert),
                    difficultyNumberText = "15".desc(),
                ),
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Boss+Rush-jacket.webp",
                    difficultyClassText = "DIFFICULT".desc(),
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyDifficult),
                    difficultyNumberText = "14".desc(),
                ),
            ),
        ),
        buttonText = "Start Trial".desc(),
        buttonAction = TrialSessionAction.StartTrial,
    )
}
