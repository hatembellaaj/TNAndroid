import Foundation

protocol ContentRepository {
    func fetchNews(language: AppLanguage) async throws -> [NewsItem]
    func fetchTop24(language: AppLanguage) async throws -> [NewsItem]
}

protocol PrayerRepository {
    func fetchPrayerTimes() async throws -> [PrayerTime]
}

struct RemoteContentRepository: ContentRepository {
    let client: HTTPClient

    func fetchNews(language: AppLanguage) async throws -> [NewsItem] {
        let feed = try await client.fetch(NewsFeed.self, endpoint: .newsInit(language))
        return feed.news
    }

    func fetchTop24(language: AppLanguage) async throws -> [NewsItem] {
        let feed = try await client.fetch(NewsFeed.self, endpoint: .plusLus(language))
        return feed.news
    }
}

struct RemotePrayerRepository: PrayerRepository {
    let client: HTTPClient

    func fetchPrayerTimes() async throws -> [PrayerTime] {
        // Temporary product decision: ignore the prayer API while it is HTTP-only.
        // Keeping this repository method avoids breaking call sites.
        _ = client
        return []
    }
}
