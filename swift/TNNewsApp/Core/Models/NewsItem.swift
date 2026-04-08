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

struct NewsFeed: Codable {
    let news: [NewsItem]

    enum CodingKeys: String, CodingKey {
        case news = "data"
    }
}
