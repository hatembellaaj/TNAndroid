import SwiftUI
import WebKit

struct ContentView: View {
    @StateObject private var vm = BootstrapViewModel()

    var body: some View {
        TabView {
            NavigationStack {
                Group {
                    if vm.isLoadingNews {
                        ProgressView("Chargement des articles...")
                    } else if let error = vm.newsError {
                        Text(error).foregroundStyle(.red)
                    } else {
                        List(vm.newsItems) { item in
                            NavigationLink {
                                NewsHTMLDetailView(item: item)
                            } label: {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(item.title).font(.headline)
                                    if !item.summary.isEmpty {
                                        Text(item.summary).font(.subheadline).foregroundStyle(.secondary)
                                    }
                                }
                            }
                        }
                    }
                }
                .navigationTitle("Actualités")
            }
            .tabItem { Label("Home", systemImage: "house") }

            NavigationStack {
                Group {
                    if vm.isLoadingPrayers {
                        ProgressView("Chargement des prières...")
                    } else if let error = vm.prayerError {
                        Text(error).foregroundStyle(.red)
                    } else if vm.prayers.isEmpty {
                        Text("Flux prières ignoré temporairement (endpoint HTTP).")
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                            .padding()
                    } else {
                        List(vm.prayers) { prayer in
                            HStack {
                                Text(prayer.name)
                                Spacer()
                                Text(prayer.time).bold()
                            }
                        }
                    }
                }
                .navigationTitle("Prières")
            }
            .tabItem { Label("Prières", systemImage: "clock") }

            NavigationStack {
                Text("Favoris (intégration complète après target membership)")
                    .padding()
                    .navigationTitle("Favoris")
            }
            .tabItem { Label("Favoris", systemImage: "bookmark") }

            NavigationStack {
                Text("Paramètres")
                    .navigationTitle("Paramètres")
            }
            .tabItem { Label("Paramètres", systemImage: "gearshape") }
        }
        .task {
            await vm.loadAll()
        }
    }
}

@MainActor
final class BootstrapViewModel: ObservableObject {
    struct NewsRow: Identifiable {
        let id: String
        let title: String
        let summary: String
        let contentHTML: String
        let date: String
        let shareURL: String
    }

    struct PrayerRow: Identifiable {
        let id = UUID()
        let name: String
        let time: String
    }

    @Published var newsItems: [NewsRow] = []
    @Published var prayers: [PrayerRow] = []

    @Published var isLoadingNews = false
    @Published var isLoadingPrayers = false

    @Published var newsError: String?
    @Published var prayerError: String?

    private let newsURL = URL(string: "https://preprod.tunisienumerique.com/results.json")!
    func loadAll() async {
        await loadNews()
        disablePrayersTemporarily()
    }

    func loadNews() async {
        isLoadingNews = true
        newsError = nil
        print("[TN-iOS] Loading news from: \(newsURL.absoluteString)")

        do {
            let (data, response) = try await URLSession.shared.data(from: newsURL)
            if let http = response as? HTTPURLResponse {
                print("[TN-iOS] News HTTP status: \(http.statusCode)")
            }

            let json = try JSONSerialization.jsonObject(with: data) as? [String: Any]
            let arrays: [[String: Any]] =
                (json?["data"] as? [[String: Any]]) ??
                (json?["results"] as? [[String: Any]]) ??
                (json?["news"] as? [[String: Any]]) ?? []

            let mapped = arrays.enumerated().map { idx, row in
                let id = pickString(row, keys: ["News_ID", "id", "id_news", "idNews"]) ??
                    String(describing: row["id"] ?? idx)

                let rawTitle = pickString(row, keys: [
                    "News_Titre", "title", "titre", "titleNews", "post_title"
                ]) ?? "Sans titre"
                let rawSummary = pickString(row, keys: [
                    "News_Description", "description", "resume", "summary"
                ]) ?? ""
                let rawContent = pickString(row, keys: [
                    "News_Contenu", "content", "contenu", "News_commentaire_android"
                ]) ?? rawSummary
                let date = pickString(row, keys: ["News_Format_Date", "News_Date", "date"]) ?? ""
                let shareURL = pickString(row, keys: ["News_Url_Partage", "shareURL", "shareUrlNews"]) ?? ""

                let title = normalizeDisplayText(rawTitle)
                let summary = normalizeDisplayText(rawSummary)

                return NewsRow(
                    id: id,
                    title: title,
                    summary: summary,
                    contentHTML: rawContent,
                    date: normalizeDisplayText(date),
                    shareURL: shareURL
                )
            }

            newsItems = mapped
            print("[TN-iOS] News loaded count: \(mapped.count)")
            if mapped.isEmpty {
                print("[TN-iOS] News payload parsed but empty")
            }
        } catch {
            newsError = "Erreur chargement news: \(error.localizedDescription)"
            print("[TN-iOS] News load error: \(error)")
        }

        isLoadingNews = false
    }

    func disablePrayersTemporarily() {
        isLoadingPrayers = false
        prayerError = nil
        prayers = []
        print("[TN-iOS] Prayer endpoint ignored temporarily (HTTP-only source).")
    }

    private func pickString(_ row: [String: Any], keys: [String]) -> String? {
        for key in keys {
            if let value = row[key] as? String, !value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                return value
            }
        }
        return nil
    }

    private func normalizeDisplayText(_ text: String) -> String {
        text
            .replacingOccurrences(of: "&quot;", with: "\"")
            .replacingOccurrences(of: "&amp;", with: "&")
            .replacingOccurrences(of: "&#039;", with: "'")
            .replacingOccurrences(of: "&nbsp;", with: " ")
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }
}

struct NewsHTMLDetailView: View {
    let item: BootstrapViewModel.NewsRow

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(item.title)
                .font(.title3.bold())
                .padding(.horizontal)
                .padding(.top, 8)

            if !item.date.isEmpty {
                Text(item.date)
                    .font(.footnote)
                    .foregroundStyle(.secondary)
                    .padding(.horizontal)
            }

            if let url = URL(string: item.shareURL), !item.shareURL.isEmpty {
                ShareLink("Partager", item: url)
                    .padding(.horizontal)
            }

            Divider()
            HTMLContentView(html: item.contentHTML)
        }
        .navigationTitle("Détail")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct HTMLContentView: UIViewRepresentable {
    let html: String

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView(frame: .zero)
        webView.isOpaque = false
        webView.backgroundColor = .clear
        return webView
    }

    func updateUIView(_ webView: WKWebView, context: Context) {
        let wrappedHTML = """
        <html>
          <head>
            <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
            <style>
              body { font-family: -apple-system; font-size: 18px; color: #111; line-height: 1.5; margin: 0; padding: 0; }
              img { max-width: 100%; height: auto; }
              iframe { max-width: 100%; }
            </style>
          </head>
          <body>\(html)</body>
        </html>
        """
        webView.loadHTMLString(wrappedHTML, baseURL: URL(string: "https://www.tunisienumerique.com"))
    }
}

#Preview {
    ContentView()
}
