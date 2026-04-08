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
        struct PrayerPayload: Codable {
            let data: [PrayerPayloadEntry]
        }
        struct PrayerPayloadEntry: Codable {
            let name: String
            let time: String
        }

        let payload = try await client.fetch(PrayerPayload.self, endpoint: .prayers)
        return payload.data.map { PrayerTime(name: $0.name, time: $0.time) }
    }
}
