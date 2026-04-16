import SwiftUI

struct NewsListView: View {
    @EnvironmentObject private var env: AppEnvironment
    let title: String
    let items: [NewsItem]
    @State private var favoriteIDs: Set<String> = []

    var body: some View {
        List(items) { item in
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

                    ShareLink(item: shareText(for: item)) {
                        Image(systemName: "square.and.arrow.up")
                            .font(.title3)
                            .foregroundStyle(.gray)
                    }
                    .buttonStyle(.plain)
                }
            }
        }
        .navigationTitle(title)
        .navigationDestination(for: NewsItem.self) { item in
            NewsDetailView(item: item)
        }
        .onAppear(perform: refreshFavorites)
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
}
