import Foundation

@MainActor
final class FavoritesViewModel: ObservableObject {
    @Published var items: [NewsItem] = []

    private let store: FavoritesStore

    init(store: FavoritesStore) {
        self.store = store
    }

    func load() {
        items = store.all()
    }

    func remove(_ id: String) {
        store.remove(newsID: id)
        load()
    }
}
