import SwiftUI

struct SettingsView: View {
    @EnvironmentObject private var settings: AppSettingsStore

    var body: some View {
        NavigationStack {
            Form {
                Section("Langue") {
                    Picker("Langue", selection: $settings.selectedLanguage) {
                        ForEach(AppLanguage.allCases) { lang in
                            Text(lang.title).tag(lang)
                        }
                    }
                    .pickerStyle(.segmented)
                }

                Section("Notifications") {
                    Toggle("Activer les notifications", isOn: $settings.notificationsEnabled)
                }
            }
            .navigationTitle("Paramètres")
        }
    }
}
