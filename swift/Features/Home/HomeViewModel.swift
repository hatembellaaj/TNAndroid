import Foundation

@MainActor
final class HomeViewModel: ObservableObject {
    @Published var items: [NewsItem] = []
    @Published var isLoading = false
    @Published var error: String?

    private let repository: ContentRepository

    init(repository: ContentRepository) {
        self.repository = repository
    }

    func load(language: AppLanguage) async {
        isLoading = true
        defer { isLoading = false }
        do {
            items = try await repository.fetchNews(language: language)
            error = nil
        } catch {
            self.error = error.localizedDescription
        }
    }
}
