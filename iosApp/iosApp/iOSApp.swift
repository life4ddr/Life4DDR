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
        _ = LadderDataManager()
        _ = MotdManager()
        _ = PlacementManager()
        _ = SongDataManager()
        _ = TrialManager()
    }
    
    @State private var path = NavigationPath()
    
    func goToView(nextStep: InitState) {
        path.append(nextStep)
    }
    
	var body: some Scene {
		WindowGroup {
            NavigationStack(path: $path) {
                // TODO: implement view model to direct player to correct screen upon launch
                FirstRunView(onComplete: goToView)
                .navigationDestination(for: InitState.self) { initState in
                    switch initState {
                        case InitState.placements:
                            PlacementListView(
                                isFirstRun: true,
                                onRanksClicked: { goToView(nextStep: InitState.ranks) },
                                goToMainView: { goToView(nextStep: InitState.done) }
                            )
                        case InitState.ranks:
                            RankListView(isFirstRun: true, onAction: { action in
                                if action is RankListViewModel.ActionNavigateToPlacements {
                                    goToView(nextStep: InitState.placements)
                                } else if action is RankListViewModel.ActionNavigateToMainScreen {
                                    goToView(nextStep: InitState.done)
                                }
                            })
                        case InitState.done:
                            MainView()
                        default:
                            Text("Not implemented")
                    }
                }
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

class DummyNotifications: Notifications {
  
}
