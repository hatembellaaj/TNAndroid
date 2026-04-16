import SwiftUI
import UIKit

struct HomeView: View {
    @EnvironmentObject private var settings: AppSettingsStore
    @EnvironmentObject private var env: AppEnvironment
    @StateObject private var vmHolder = Holder()
    @State private var favoriteIDs: Set<String> = []

    var body: some View {
        NavigationStack {
            Group {
                if vmHolder.vm.isLoading {
                    ProgressView()
                } else if let err = vmHolder.vm.error {
                    Text(err)
                } else {
                    List(vmHolder.vm.items) { item in
                        HStack(alignment: .top, spacing: 12) {
                            NavigationLink(value: item) {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(item.title).font(.headline)
                                    Text(item.summary).font(.subheadline).foregroundStyle(.secondary)
                                }
                            }
                            .buttonStyle(.plain)

                            Spacer(minLength: 8)

                            HStack(spacing: 14) {
                                Button(action: { toggleFavorite(item) }) {
                                    Image(systemName: favoriteIDs.contains(item.id) ? "bookmark.fill" : "bookmark")
                                        .font(.title3)
                                }
                                .buttonStyle(.plain)
                                .foregroundStyle(favoriteIDs.contains(item.id) ? .green : .gray)

                                Button(action: { share(item) }) {
                                    Image(systemName: "square.and.arrow.up")
                                        .font(.title3)
                                        .foregroundStyle(.gray)
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }
                    .navigationDestination(for: NewsItem.self) { item in
                        NewsDetailView(item: item)
                    }
                }
            }
            .navigationTitle("Actualités")
            .task {
                vmHolder.setupIfNeeded(repository: env.contentRepository)
                await vmHolder.vm.load(language: settings.selectedLanguage)
                refreshFavorites()
            }
        }
    }

    private func refreshFavorites() {
        favoriteIDs = Set(env.favoritesStore.all().map(\.id))
    }

    private func toggleFavorite(_ item: NewsItem) {
        if env.favoritesStore.contains(newsID: item.id) {
            env.favoritesStore.remove(newsID: item.id)
        } else {
            env.favoritesStore.add(item)
        }
        refreshFavorites()
    }

    private func shareText(for item: NewsItem) -> String {
        [item.title, item.shareURL].compactMap { $0 }.joined(separator: "\n")
    }

    private func share(_ item: NewsItem) {
        let text = shareText(for: item)
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let root = scene.windows.first?.rootViewController else { return }
        let vc = UIActivityViewController(activityItems: [text], applicationActivities: nil)
        root.present(vc, animated: true)
    }

    final class Holder: ObservableObject {
        @Published var vm = HomeViewModel(repository: RemoteContentRepository(client: URLSessionHTTPClient()))

        func setupIfNeeded(repository: ContentRepository) {
            if vm.items.isEmpty {
                vm = HomeViewModel(repository: repository)
            }
        }
    }
}
