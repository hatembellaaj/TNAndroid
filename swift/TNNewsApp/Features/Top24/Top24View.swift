import SwiftUI

struct Top24View: View {
    @EnvironmentObject private var settings: AppSettingsStore
    @EnvironmentObject private var env: AppEnvironment
    @StateObject private var holder = Holder()
    @State private var favoriteIDs: Set<String> = []

    var body: some View {
        NavigationStack {
            List(holder.vm.items) { item in
                HStack(alignment: .top, spacing: 12) {
                    NavigationLink(value: item) {
                        Text(item.title)
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

                        ShareLink(item: shareText(for: item)) {
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
            .navigationTitle("Top24")
            .task {
                holder.setupIfNeeded(repository: env.contentRepository)
                await holder.vm.load(language: settings.selectedLanguage)
                refreshFavorites()
            }
            .onAppear(perform: refreshFavorites)
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

    final class Holder: ObservableObject {
        @Published var vm = Top24ViewModel(repository: RemoteContentRepository(client: URLSessionHTTPClient()))

        func setupIfNeeded(repository: ContentRepository) {
            vm = Top24ViewModel(repository: repository)
        }
    }
}
