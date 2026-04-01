import SwiftUI

struct NewsDetailView: View {
    @EnvironmentObject private var env: AppEnvironment
    let item: NewsItem

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text(item.title)
                    .font(.title2.bold())
                Text(item.content)
                    .font(.body)

                HStack {
                    Button(env.favoritesStore.contains(newsID: item.id) ? "Retirer des favoris" : "Ajouter aux favoris") {
                        if env.favoritesStore.contains(newsID: item.id) {
                            env.favoritesStore.remove(newsID: item.id)
                        } else {
                            env.favoritesStore.add(item)
                        }
                    }
                    .buttonStyle(.borderedProminent)

                    if let link = item.shareURL, let url = URL(string: link) {
                        ShareLink(item: url)
                    }
                }
            }
            .padding()
        }
        .navigationTitle("Détail")
        .navigationBarTitleDisplayMode(.inline)
    }
}
