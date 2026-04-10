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

    enum CodingKeys: String, CodingKey {
        case id
        case idNews
        case newsID = "News_ID"
        case title
        case titre
        case titleNews
        case newsTitre = "News_Titre"
        case summary
        case description
        case resume
        case newsDescription = "News_Description"
        case content
        case contenu
        case newsContent = "News_Contenu"
        case imageURL
        case imageUrlNews
        case newsImageURL = "News_Url_Image"
        case shareURL
        case shareUrlNews
        case newsShareURL = "News_Url_Partage"
        case date
        case newsDate = "News_Date"
        case newsDateFormatted = "News_Format_Date"
        case language
        case type
        case typeNews
        case newsListCategory = "News_list_category"
        case isPaywall
        case paywall = "is_paywall"
    }

    init(from decoder: Decoder) throws {
        let c = try decoder.container(keyedBy: CodingKeys.self)

        let rawTitle = try c.decodeFirstString(for: [.newsTitre, .title, .titre, .titleNews]) ?? "Sans titre"
        let rawSummary = try c.decodeFirstString(for: [.newsDescription, .description, .resume, .summary]) ?? ""

        id = try c.decodeFirstString(for: [.newsID, .id, .idNews]) ?? UUID().uuidString
        title = NewsItem.clean(rawTitle)
        summary = NewsItem.clean(rawSummary)
        content = NewsItem.clean(try c.decodeFirstString(for: [.newsContent, .content, .contenu]) ?? "")
        imageURL = try c.decodeFirstString(for: [.newsImageURL, .imageURL, .imageUrlNews])
        shareURL = try c.decodeFirstString(for: [.newsShareURL, .shareURL, .shareUrlNews])
        date = try c.decodeFirstString(for: [.newsDateFormatted, .newsDate, .date])
        language = .fr
        type = try c.decodeFirstString(for: [.newsListCategory, .type, .typeNews]) ?? ""
        isPaywall = (try c.decodeIfPresent(Bool.self, forKey: .paywall)) ?? (try c.decodeIfPresent(Bool.self, forKey: .isPaywall)) ?? false
    }

    private static func clean(_ text: String) -> String {
        text
            .replacingOccurrences(of: "&quot;", with: "\"")
            .replacingOccurrences(of: "&amp;", with: "&")
            .replacingOccurrences(of: "&#039;", with: "'")
            .replacingOccurrences(of: "&nbsp;", with: " ")
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }
}

private extension KeyedDecodingContainer where K == NewsItem.CodingKeys {
    func decodeFirstString(for keys: [K]) throws -> String? {
        for key in keys {
            if let value = try decodeIfPresent(String.self, forKey: key), !value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                return value
            }
        }
        return nil
    }
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
