import SwiftUI

struct SplashView: View {
    @EnvironmentObject private var settings: AppSettingsStore
    @EnvironmentObject private var env: AppEnvironment
    @State private var isReady = false

    var body: some View {
        Group {
            if isReady {
                RootTabView()
            } else {
                VStack(spacing: 12) {
                    ProgressView()
                    Text("Chargement...")
                }
                .task {
                    await env.syncOrchestrator.initialRefresh(language: settings.selectedLanguage)
                    try? await Task.sleep(nanoseconds: 900_000_000)
                    isReady = true
                }
            }
        }
    }
}
