import SwiftUI
import Shared

@available(iOS 16.0, *)
@main
struct iOSApp: App {
    init() {
      KoinKt.doInitKoin(
        appModule: nativeModule,
        extraAppModule: Koin_iosKt.makeIosExtraModule(defaults: UserDefaults.standard),
        appDeclaration: { _ in }
      )
    }
    
	var body: some Scene {
		WindowGroup {
      NavigationView {
        FirstRunView()
      }
		}
	}
}

//@available(iOS 16.0, *)
//var nativeModule: Koin_coreModule = MakeNativeModuleKt.makeNativeModule(
//    ignoresDataReader: iosDataReader(cachedFileName: GithubDataAPICompanion().IGNORES_FILE_NAME)
//)

@available(iOS 16.0, *)
var nativeModule: Koin_coreModule = KoinKt.makeNativeModule(
  appInfo: IosAppInfo(),
  platformStrings: DummyIosStrings(),
  ignoresReader: iosDataReader(
    fileResource: MR.files().ignore_lists,
    cachedFileName: GithubDataAPICompanion().IGNORES_FILE_NAME
  ),
  motdReader: iosDataReader(
    fileResource: MR.files().motd,
    cachedFileName: GithubDataAPICompanion().MOTD_FILE_NAME
  ),
  partialDifficultyReader: iosDataReader(
    fileResource: MR.files().partial_difficulties,
    cachedFileName: GithubDataAPICompanion().PARTIAL_DIFFICULTY_FILE_NAME
  ),
  placementsReader: iosUncachedDataReader(fileResource: MR.files().placements),
  ranksReader: iosDataReader(
    fileResource: MR.files().ranks,
    cachedFileName: GithubDataAPICompanion().RANKS_FILE_NAME
  ),
  songsReader: iosDataReader(
    fileResource: MR.files().songs,
    cachedFileName: GithubDataAPICompanion().SONGS_FILE_NAME
  ),
  trialsReader: iosDataReader(
    fileResource: MR.files().trials,
    cachedFileName: GithubDataAPICompanion().TRIALS_FILE_NAME
  ),
  notifications: DummyNotifications(),
  additionalItems: {_ in }
)

class IosAppInfo: AppInfo {
  var appId: String = "LIFE4DDR"
}

class DummyIosStrings: PlatformStrings {
  func clearString(ct: ClearType) -> String {
    return ""
  }
  
  func clearStringShort(ct: ClearType) -> String {
    return ""
  }
  
  func groupNameString(rank: LadderRank) -> String {
    return ""
  }
  
  func lampString(ct: ClearType) -> String {
    return ""
  }
  
  func nameString(rank: PlacementRank) -> String {
    return ""
  }
  
  func nameString(rank_ rank: LadderRank) -> String {
    return ""
  }
  
  func nameString(clazz: LadderRankClass) -> String {
    return ""
  }
  
  func nameString(rank__ rank: TrialRank) -> String {
    return ""
  }
  
  func toListString(list: [String], useAnd: Bool, caps: Bool) -> String {
    return ""
  }
  
  var notification: NotificationStrings = DummyStrings2()
  
  var rank: RankStrings = DummyWhatever()
  
  var trial: TrialStrings = Foo()
}

class DummyStrings2: NotificationStrings {
  var discordNameTitle: String {
    return ""
  }
  
  var exScoreTitle: String {
    return ""
  }
  
  var mainChannelDescription: String {
    return ""
  }
  
  var mainChannelTitle: String {
    return ""
  }
  
  var rivalCodeTitle: String {
    return ""
  }
  
  var twitterNameTitle: String {
    return ""
  }
}

class DummyWhatever: RankStrings {
  func averageScoreString(averageScore: Int32, groupString: String) -> String {
    return ""
  }
  
  func clearLampString(clearType: ClearType) -> String {
    return ""
  }
  
  func clearString(clearType: ClearType, useLamp: Bool) -> String {
    return ""
  }
  
  func clearString(clearType: ClearType, useLamp: Bool, groupString: String) -> String {
    return ""
  }
  
  func clearTypeString(clearType: ClearType) -> String {
    return ""
  }
  
  func diffNumAll(diffNum: Int32, allowsHigherDiffNum: Bool) -> String {
    return ""
  }
  
  func diffNumCount(count: Int32, diffNum: Int32, allowsHigherDiffNum: Bool) -> String {
    return ""
  }
  
  func diffNumSingle(diffNum: Int32, allowsHigherDiffNum: Bool) -> String {
    return ""
  }
  
  func difficultyClassSetModifier(groupString: String, diffClassSet: DifficultyClassSet, playStyle: PlayStyle) -> String {
    return ""
  }
  
  func exceptionsModifier(groupString: String, exceptions: Int32) -> String {
    return ""
  }
  
  func folderString(folderCount: Int32) -> String {
    return ""
  }
  
  func folderString(folderName: String) -> String {
    return ""
  }
  
  func getCalorieCountString(count: Int32) -> String {
    return ""
  }
  
  func getMFCPointString(count: Double) -> String {
    return ""
  }
  
  func getSongSetString(clearType: ClearType, difficulties: KotlinIntArray) -> String {
    return ""
  }
  
  func getTrialCountString(rank: TrialRank, count: Int32) -> String {
    return ""
  }
  
  func higherDiffNumSuffix(allowsHigherDiffNum: Bool) -> String {
    return ""
  }
  
  func scoreString(score: Int32, groupString: String) -> String {
    return ""
  }
  
  func songCountString(songCount: Int32) -> String {
    return ""
  }
  
  func songExceptionsModifier(groupString: String, songExceptions: [String]) -> String {
    return ""
  }
  
  func songListString(songs: [String]) -> String {
    return ""
  }
  
  
}

class Foo: TrialStrings {
  func allowedBadJudgments(bad: Int32) -> String {
    return ""
  }
  
  func allowedMissingExScore(bad: Int32, total: KotlinInt?) -> String {
    return ""
  }
  
  func allowedSongMisses(misses: Int32) -> String {
    return ""
  }
  
  func allowedTotalMisses(misses: Int32) -> String {
    return ""
  }
  
  func clearEverySong(clearType: ClearType) -> String {
    return ""
  }
  
  func clearFirstCountSongs(clearType: ClearType, songs: Int32) -> String {
    return ""
  }
  
  func clearTrial() -> String {
    return ""
  }
  
  func scoreCountOtherSongs(score: Int32, count: Int32) -> String {
    return ""
  }
  
  func scoreCountSongs(score: Int32, count: Int32) -> String {
    return ""
  }
  
  func scoreEveryOtherSong(score: Int32) -> String {
    return ""
  }
  
  func scoreEverySong(score: Int32) -> String {
    return ""
  }
  
  func scoreSingleSong(score: Int32, song: String) -> String {
    return ""
  }
  
  func scoreString(score: Int32) -> String {
    return ""
  }
  
  
}

class DummyNotifications: Notifications {
  
}
