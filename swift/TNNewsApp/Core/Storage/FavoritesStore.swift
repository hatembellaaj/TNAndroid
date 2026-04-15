import Foundation

protocol FavoritesStore {
    func all() -> [NewsItem]
    func contains(newsID: String) -> Bool
    func add(_ news: NewsItem)
    func remove(newsID: String)
}

final class UserDefaultsFavoritesStore: FavoritesStore {
    private let key = "tn.favorites"

    func all() -> [NewsItem] {
        guard let data = UserDefaults.standard.data(forKey: key),
              let items = try? JSONDecoder().decode([NewsItem].self, from: data) else {
            return []
        }
        return items
    }

    func contains(newsID: String) -> Bool {
        all().contains(where: { $0.id == newsID })
    }

    func add(_ news: NewsItem) {
        var items = all()
        guard items.contains(where: { $0.id == news.id }) == false else { return }
        items.append(news)
        persist(items)
    }

    func remove(newsID: String) {
        let filtered = all().filter { $0.id != newsID }
        persist(filtered)
    }

    private func persist(_ items: [NewsItem]) {
        guard let data = try? JSONEncoder().encode(items) else { return }
        UserDefaults.standard.set(data, forKey: key)
    }
}
