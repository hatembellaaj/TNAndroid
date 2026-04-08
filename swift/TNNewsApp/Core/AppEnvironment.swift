import Foundation

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
