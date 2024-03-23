import SwiftUI
import Shared

@available(iOS 16.0, *)
@main
struct iOSApp: App {
//    init() {
//        Koin_iosKt.doInitKoin()
//    }
    
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
