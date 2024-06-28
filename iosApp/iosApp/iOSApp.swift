import SwiftUI
import Shared

@available(iOS 16.0, *)
@main
struct iOSApp: App {
    @ObservedObject var viewModel: LaunchViewModel = LaunchViewModel()
    @State private var path = NavigationPath()
    @State var loaded: Bool = true
    
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
    
    func goToView(nextStep: InitState) {
        switch nextStep {
            case InitState.placements:
                // TODO: add a destination from the TrialListView to Placements
                // right now, there is only one Placements destination
                path.append(nextStep)
            case InitState.ranks:
                path.append(FirstRunDestination.InitialRankList())
            case InitState.done:
                path.append(FirstRunDestination.MainScreen())
            default:
                break
        }
    }
    
	var body: some Scene {
		WindowGroup {
            NavigationStack(path: $path) {
                if (loaded) {
                    FirstRunView(onComplete: goToView)
                    .navigationDestination(for: InitState.self) { initState in
                        PlacementListView(
                            isFirstRun: true,
                            onRanksClicked: { goToView(nextStep: InitState.ranks) },
                            goToMainView: { goToView(nextStep: InitState.done) },
                            onPlacementSelected: { placement in
                                path.append(FirstRunDestination.PlacementDetails(placementId: placement.id))
                            }
                        )
                    }
                    .navigationDestination(for: FirstRunDestination.InitialRankList.self) { _ in
                        RankListView(
                            isFirstRun: true,
                            onAction: { action in
                                if action is RankListViewModel.ActionNavigateToPlacements {
                                    goToView(nextStep: InitState.placements)
                                } else if action is RankListViewModel.ActionNavigateToMainScreen {
                                    goToView(nextStep: InitState.done)
                                }
                            }
                        )
                    }
                    .navigationDestination(for: FirstRunDestination.MainScreen.self) { _ in
                        MainView(path: $path)
                    }
                    .navigationDestination(for: LadderDestination.RankList.self) { _ in
                        RankListView(
                            onAction: { action in
                                if action is RankListViewModel.ActionNavigateToMainScreen {
                                    path.removeLast()
                                }
                            }
                        )
                    }
                    .navigationDestination(for: FirstRunDestination.PlacementList.self) { _ in
                        PlacementListView(
                            onPlacementSelected: { placement in
                                path.append(FirstRunDestination.PlacementDetails(placementId: placement.id))
                            }
                        )
                    }
                    .navigationDestination(for: TrialDestination.TrialDetails.self) { destination in
                        TrialDetailsView(trial: destination.trial)
                    }
                    .navigationDestination(for: FirstRunDestination.PlacementDetails.self) { destination in
                        PlacementDetailsView(placementId: destination.placementId)
                    }
                } else {
                    // TODO: add splash screen once the loading state works properly
                    Text("splash screen")
                }
            }
            .accentColor(Color(.accent))
            // TODO: debug below for initializing the view
//            .onAppear {
//                viewModel.launchState.subscribe { state in
//                    if !loaded {
//                        if let currentState = state {
//                            goToView(nextStep: currentState)
//                        }
//                        loaded = true
//                    }
//                }
//            }
		}
	}
}

@available(iOS 16.0, *)
struct MainView: View {
    @Binding var path: NavigationPath
    
    var body: some View {
        TabView {
            PlayerProfileView(
                onAction: { action in
                    if action is PlayerProfileAction.ChangeRank {
                        path.append(LadderDestination.RankList())
                    }
                }
            ).tabItem {
                Label("Profile", systemImage: "person")
            }
            ScoreListView().tabItem {
                Label("Scores", systemImage: "music.note.list")
            }
            TrialListView(
                onTrialSelected: { trial in
                    path.append(TrialDestination.TrialDetails(trial: trial))
                },
                onPlacementsSelected: {
                    path.append(FirstRunDestination.PlacementList())
                }
            ).tabItem {
                Label("Trials", systemImage: "square.grid.2x2")
            }
            SettingsView().tabItem {
                Label("Settings", systemImage: "gear")
            }
        }
        .navigationBarBackButtonHidden(true)
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
