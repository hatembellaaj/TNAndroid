import SwiftUI
import Foundation

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

@MainActor
final class AppSettingsStore: ObservableObject {
    @Published var selectedLanguage: AppLanguage {
        didSet { UserDefaults.standard.set(selectedLanguage.rawValue, forKey: Keys.language) }
    }

    @Published var notificationsEnabled: Bool {
        didSet { UserDefaults.standard.set(notificationsEnabled, forKey: Keys.notifications) }
    }

    private enum Keys {
        static let language = "tn.lang"
        static let notifications = "tn.notifications"
    }

    init() {
        let raw = UserDefaults.standard.string(forKey: Keys.language) ?? AppLanguage.fr.rawValue
        self.selectedLanguage = AppLanguage(rawValue: raw) ?? .fr
        self.notificationsEnabled = UserDefaults.standard.object(forKey: Keys.notifications) as? Bool ?? true
    }
}

@MainActor
final class AppEnvironment: ObservableObject {
    let client: HTTPClient
    let contentRepository: ContentRepository
    let prayerRepository: PrayerRepository
    let favoritesStore: FavoritesStore
    let notificationRouter: NotificationRouter
    let syncOrchestrator: SyncOrchestrator

    init() {
        self.client = URLSessionHTTPClient()
        self.favoritesStore = UserDefaultsFavoritesStore()
        self.contentRepository = RemoteContentRepository(client: client)
        self.prayerRepository = RemotePrayerRepository(client: client)
        self.notificationRouter = NotificationRouter()
        self.syncOrchestrator = SyncOrchestrator(contentRepository: contentRepository)
    }
}
