import SwiftUI

struct PrayerView: View {
    @EnvironmentObject private var env: AppEnvironment
    @StateObject private var holder = Holder()

    var body: some View {
        NavigationStack {
            Group {
                if holder.vm.isLoading {
                    ProgressView()
                } else if let error = holder.vm.error {
                    Text(error)
                } else {
                    List(holder.vm.prayers) { prayer in
                        HStack {
                            Text(prayer.name)
                            Spacer()
                            Text(prayer.time).bold()
                        }
                    }
                }
            }
            .navigationTitle("Horaires de prière")
            .task {
                holder.setupIfNeeded(repository: env.prayerRepository)
                await holder.vm.load()
            }
        }
    }

    final class Holder: ObservableObject {
        @Published var vm = PrayerViewModel(repository: RemotePrayerRepository(client: URLSessionHTTPClient()))

        func setupIfNeeded(repository: PrayerRepository) {
            vm = PrayerViewModel(repository: repository)
        }
    }
}
