import SwiftUI

struct NewsListView: View {
    let title: String
    let items: [NewsItem]

    var body: some View {
        List(items) { item in
            NavigationLink(value: item) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(item.title).font(.headline)
                    Text(item.summary).font(.subheadline).foregroundStyle(.secondary)
                }
            }
        }
        .navigationTitle(title)
        .navigationDestination(for: NewsItem.self) { item in
            NewsDetailView(item: item)
        }
    }
}
