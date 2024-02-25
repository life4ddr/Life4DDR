import SwiftUI
import Shared

@available(iOS 15.0, *)
@main
struct iOSApp: App {
//    init() {
//        Koin_iosKt.doInitKoin()
//    }
    
	var body: some Scene {
		WindowGroup {
            FirstRunView()
		}
	}
}
