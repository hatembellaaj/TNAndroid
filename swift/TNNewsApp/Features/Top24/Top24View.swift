import SwiftUI

struct Top24View: View {
    @EnvironmentObject private var settings: AppSettingsStore
    @EnvironmentObject private var env: AppEnvironment
    @StateObject private var holder = Holder()

    var body: some View {
        NavigationStack {
            List(holder.vm.items) { item in
                NavigationLink(value: item) {
                    Text(item.title)
                }
            }
            .navigationDestination(for: NewsItem.self) { item in
                NewsDetailView(item: item)
            }
            .navigationTitle("Top24")
            .task {
                holder.setupIfNeeded(repository: env.contentRepository)
                await holder.vm.load(language: settings.selectedLanguage)
            }
        }
    }

    final class Holder: ObservableObject {
        @Published var vm = Top24ViewModel(repository: RemoteContentRepository(client: URLSessionHTTPClient()))

        func setupIfNeeded(repository: ContentRepository) {
            vm = Top24ViewModel(repository: repository)
        }
    }
}
