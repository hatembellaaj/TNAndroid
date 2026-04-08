import Foundation

final class SyncOrchestrator {
    private let contentRepository: ContentRepository

    init(contentRepository: ContentRepository) {
        self.contentRepository = contentRepository
    }

    func initialRefresh(language: AppLanguage) async {
        _ = try? await contentRepository.fetchNews(language: language)
        _ = try? await contentRepository.fetchTop24(language: language)
    }
}
