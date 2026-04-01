import Foundation

struct VideoItem: Identifiable, Codable, Hashable {
    let id: String
    let title: String
    let youtubeURL: String
    let thumbnailURL: String?
}
