import SwiftUI

struct HomeView: View {
    @EnvironmentObject private var settings: AppSettingsStore
    @EnvironmentObject private var env: AppEnvironment
    @StateObject private var vmHolder = Holder()

    var body: some View {
        NavigationStack {
            Group {
                if vmHolder.vm.isLoading {
                    ProgressView()
                } else if let err = vmHolder.vm.error {
                    Text(err)
                } else {
                    List(vmHolder.vm.items) { item in
                        NavigationLink(value: item) {
                            VStack(alignment: .leading) {
                                Text(item.title).font(.headline)
                                Text(item.summary).font(.subheadline).foregroundStyle(.secondary)
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
            }
        }
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
