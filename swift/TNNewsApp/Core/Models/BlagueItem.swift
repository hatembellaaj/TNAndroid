import Foundation

struct BlagueItem: Identifiable, Codable, Hashable {
    let id: String
    let title: String
    let content: String
}
