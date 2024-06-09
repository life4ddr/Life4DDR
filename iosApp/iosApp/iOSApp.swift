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
    
    @State private var path = NavigationPath()
    
    func goToView(nextStep: InitState) {
        path.append(nextStep)
    }
    
	var body: some Scene {
		WindowGroup {
            NavigationStack(path: $path) {
                FirstRunView(onComplete: goToView)
                .navigationDestination(for: InitState.self) { initState in
                    switch initState {
                        case InitState.placements:
                            // PlacementListView()
                            Text("Placement View not implemented yet")
                                .navigationBarBackButtonHidden(true)
                        case InitState.ranks:
                            FirstRunRankListView()
                        case InitState.done:
                            // MainView()
                            Text("Main View not implemented yet")
                                .navigationBarBackButtonHidden(true)
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
