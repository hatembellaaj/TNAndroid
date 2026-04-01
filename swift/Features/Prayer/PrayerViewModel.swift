import Foundation

@MainActor
final class PrayerViewModel: ObservableObject {
    @Published var prayers: [PrayerTime] = []
    @Published var isLoading = false
    @Published var error: String?

    private let repository: PrayerRepository

    init(repository: PrayerRepository) {
        self.repository = repository
    }

    func load() async {
        isLoading = true
        defer { isLoading = false }
        do {
            prayers = try await repository.fetchPrayerTimes()
            error = nil
        } catch {
            self.error = error.localizedDescription
        }
    }
}
