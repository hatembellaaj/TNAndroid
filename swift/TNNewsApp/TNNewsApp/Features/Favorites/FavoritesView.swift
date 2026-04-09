import SwiftUI

struct FavoritesView: View {
    @EnvironmentObject private var env: AppEnvironment
    @StateObject private var vmHolder = Holder()

    var body: some View {
        NavigationStack {
            List {
                ForEach(vmHolder.vm.items) { item in
                    NavigationLink(value: item) {
                        Text(item.title)
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
