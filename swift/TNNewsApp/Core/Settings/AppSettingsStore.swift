import Foundation

@MainActor
final class AppSettingsStore: ObservableObject {
    @Published var selectedLanguage: AppLanguage {
        didSet { UserDefaults.standard.set(selectedLanguage.rawValue, forKey: Keys.language) }
    }

    @Published var notificationsEnabled: Bool {
        didSet { UserDefaults.standard.set(notificationsEnabled, forKey: Keys.notifications) }
    }

    private enum Keys {
        static let language = "tn.lang"
        static let notifications = "tn.notifications"
    }

    init() {
        let raw = UserDefaults.standard.string(forKey: Keys.language) ?? AppLanguage.fr.rawValue
        self.selectedLanguage = AppLanguage(rawValue: raw) ?? .fr
        self.notificationsEnabled = UserDefaults.standard.object(forKey: Keys.notifications) as? Bool ?? true
    }
}
