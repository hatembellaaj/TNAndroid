import Foundation

@MainActor
final class Top24ViewModel: ObservableObject {
    @Published var items: [NewsItem] = []
    @Published var isLoading = false

    private let repository: ContentRepository

    init(repository: ContentRepository) {
        self.repository = repository
    }

    func load(language: AppLanguage) async {
        isLoading = true
        defer { isLoading = false }
        items = (try? await repository.fetchTop24(language: language)) ?? []
    }
}
