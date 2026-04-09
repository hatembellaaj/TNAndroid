import Foundation

struct NewsItem: Identifiable, Codable, Hashable {
    let id: String
    let title: String
    let summary: String
    let content: String
    let imageURL: String?
    let shareURL: String?
    let date: String?
    let language: AppLanguage
    let type: String
    let isPaywall: Bool
}

struct NewsFeed: Decodable {
    let news: [NewsItem]

    private enum CodingKeys: String, CodingKey {
        case data
        case results
        case news
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        if let items = try? container.decode([NewsItem].self, forKey: .data) {
            self.news = items
            return
        }

        if let items = try? container.decode([NewsItem].self, forKey: .results) {
            self.news = items
            return
        }

        if let items = try? container.decode([NewsItem].self, forKey: .news) {
            self.news = items
            return
        }

        self.news = []
    }
}
