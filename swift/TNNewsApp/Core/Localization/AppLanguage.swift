import Foundation

enum AppLanguage: String, CaseIterable, Codable, Identifiable {
    case fr
    case en
    case ar

    var id: String { rawValue }

    var title: String {
        switch self {
        case .fr: return "Français"
        case .en: return "English"
        case .ar: return "العربية"
        }
    }
}
