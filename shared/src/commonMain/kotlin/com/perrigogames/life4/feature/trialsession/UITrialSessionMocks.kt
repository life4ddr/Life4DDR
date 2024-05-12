package com.perrigogames.life4.feature.trialsession

import com.perrigogames.life4.MR
import dev.icerock.moko.resources.desc.color.ColorDescResource
import dev.icerock.moko.resources.desc.image.ImageDescUrl

object UITrialSessionMocks {

    val initial = UITrialSession(
        trialTitle = "Sidequest",
        trialLevel = "LV 14",
        backgroundImage = ImageDescUrl("https://raw.githubusercontent.com/life4ddr/Life4DDR/develop/androidApp/src/main/res/drawable-xxhdpi/sidequest.webp"),
        exScoreBar = UIEXScoreBar(
            currentEx = 0,
            maxEx = 6762,
            currentExText = "0",
            maxExText = "/ 6762",
        ),
        targetRank = UITargetRank.InProgress(
            rankIcon = ImageDescUrl("https://raw.githubusercontent.com/life4ddr/Life4DDR/develop/androidApp/src/main/res/drawable-mdpi/cobalt_5.webp"),
            title = "COBALT",
            titleColor = ColorDescResource(MR.colors.cobalt),
            rankGoalItems = listOf(
                "20 or fewer Greats, Goods, or Misses",
                "230 missing EX or less (6532 EX)",
            ),
        ),
        content = UITrialSessionContent.Summary(
            items = listOf(
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Be_a_Hero!.webp",
                    difficultyClassText = "DIFFICULT",
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyDifficult),
                    difficultyNumberText = "13",
                ),
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Role-playing_game.webp",
                    difficultyClassText = "EXPERT",
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyExpert),
                    difficultyNumberText = "14",
                ),
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Kanata_no_Reflesia.webp",
                    difficultyClassText = "EXPERT",
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyExpert),
                    difficultyNumberText = "15",
                ),
                UITrialSessionContent.Summary.Item(
                    jacketUrl = "https://life4-mobile.s3.us-west-1.amazonaws.com/images/jackets/songs/Boss+Rush-jacket.webp",
                    difficultyClassText = "DIFFICULT",
                    difficultyClassColor = ColorDescResource(MR.colors.difficultyDifficult),
                    difficultyNumberText = "14",
                ),
            ),
            buttonText = "Start Trial",
            buttonAction = TrialSessionAction.StartTrial,
        ),
    )
}
