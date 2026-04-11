import SwiftUI

@main
struct TNNewsAppApp: App {
    @StateObject private var settings = AppSettingsStore()
    @StateObject private var env = AppEnvironment()

    var body: some Scene {
        WindowGroup {
            SplashView()
                .environmentObject(settings)
                .environmentObject(env)
        }
    }
}
