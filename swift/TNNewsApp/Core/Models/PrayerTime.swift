import Foundation

struct PrayerTime: Identifiable, Codable, Hashable {
    let id: UUID = UUID()
    let name: String
    let time: String
}

struct PrayerDay: Codable {
    let date: String
    let entries: [PrayerTime]
}
