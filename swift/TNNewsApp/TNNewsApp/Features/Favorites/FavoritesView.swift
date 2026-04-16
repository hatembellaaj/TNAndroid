import SwiftUI

struct FavoritesView: View {
    @EnvironmentObject private var env: AppEnvironment
    @StateObject private var vmHolder = Holder()

    var body: some View {
        NavigationStack {
            List {
                ForEach(vmHolder.vm.items) { item in
                    HStack(alignment: .top, spacing: 12) {
                        NavigationLink(value: item) {
                            Text(item.title)
                        }
                        .buttonStyle(.plain)

                        Spacer(minLength: 8)

                        HStack(spacing: 14) {
                            Button(action: { vmHolder.vm.remove(item.id) }) {
                                Image(systemName: "bookmark.fill")
                                    .font(.title3)
                                    .foregroundStyle(.green)
                            }
                            .buttonStyle(.plain)

                            ShareLink(item: [item.title, item.shareURL].compactMap { $0 }.joined(separator: "\n")) {
                                Image(systemName: "square.and.arrow.up")
                                    .font(.title3)
                                    .foregroundStyle(.gray)
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                .onDelete { indexSet in
                    for idx in indexSet {
                        vmHolder.vm.remove(vmHolder.vm.items[idx].id)
                    }
                }
            }
            .navigationDestination(for: NewsItem.self) { item in
                NewsDetailView(item: item)
            }
            .navigationTitle("Favoris")
            .onAppear {
                vmHolder.setupIfNeeded(store: env.favoritesStore)
                vmHolder.vm.load()
            }
        }
    }

    final class Holder: ObservableObject {
        @Published var vm = FavoritesViewModel(store: UserDefaultsFavoritesStore())

        func setupIfNeeded(store: FavoritesStore) {
            vm = FavoritesViewModel(store: store)
        }
    }
}
