import SwiftUI
import WebKit

struct ContentView: View {
    @StateObject private var vm = BootstrapViewModel()
    @State private var showSplash = true

    var body: some View {
        Group {
            if showSplash {
                AndroidStyleSplashView()
            } else {
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
                                        HStack(spacing: 10) {
                                            if let imageURL = URL(string: item.imageURL), !item.imageURL.isEmpty {
                                                AsyncImage(url: imageURL) { phase in
                                                    switch phase {
                                                    case .success(let image):
                                                        image
                                                            .resizable()
                                                            .scaledToFill()
                                                    default:
                                                        Image(systemName: "newspaper")
                                                            .resizable()
                                                            .scaledToFit()
                                                            .padding(10)
                                                            .foregroundStyle(.secondary)
                                                    }
                                                }
                                                .frame(width: 54, height: 54)
                                                .background(Color(.secondarySystemBackground))
                                                .clipShape(RoundedRectangle(cornerRadius: 8))
                                            }

                                            Text(item.title)
                                                .font(.headline)
                                                .lineLimit(3)
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
                .tint(Color(androidGreen))
                .task {
                    await vm.loadAll()
                }
            }
        }
        .task {
            guard showSplash else { return }
            try? await Task.sleep(nanoseconds: 1_200_000_000)
            withAnimation(.easeOut(duration: 0.25)) {
                showSplash = false
            }
        }
    }
}

struct AndroidStyleSplashView: View {
    var body: some View {
        ZStack {
            Color.white.ignoresSafeArea()

            Circle()
                .fill(Color(androidGreenSecondary).opacity(0.18))
                .frame(width: 320, height: 320)
                .offset(x: -120, y: -260)

            VStack(spacing: 16) {
                TNBrandWordmarkView()

                Text("Chargement de données...")
                    .font(.footnote)
                    .foregroundStyle(.secondary)

                ProgressView()
                    .tint(Color(androidGreen))
                    .frame(width: 160)
            }
        }
    }
}

struct TNBrandWordmarkView: View {
    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: "newspaper.fill")
                .font(.system(size: 24, weight: .bold))
                .foregroundStyle(Color(androidGreen))

            VStack(alignment: .leading, spacing: 1) {
                Text("TUNISIE")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundStyle(Color(androidGreen))
                Text("NUMÉRIQUE")
                    .font(.system(size: 14, weight: .heavy))
                    .foregroundStyle(Color(androidGreenSecondary))
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(.white.opacity(0.9))
        .clipShape(RoundedRectangle(cornerRadius: 10))
    }
}

private let androidGreen = "#36C750"
private let androidGreenSecondary = "#83B01A"

private extension Color {
    init(_ hex: String) {
        let value = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: value).scanHexInt64(&int)
        let r, g, b: UInt64
        switch value.count {
        case 6:
            (r, g, b) = (int >> 16, int >> 8 & 0xFF, int & 0xFF)
        default:
            (r, g, b) = (54, 199, 80)
        }
        self.init(.sRGB, red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255, opacity: 1)
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
        let imageURL: String
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
                let imageURL = pickString(row, keys: ["News_Url_Image", "imageURL", "imageUrlNews"]) ?? ""

                let title = normalizeDisplayText(rawTitle)
                let summary = normalizeDisplayText(rawSummary)

                return NewsRow(
                    id: id,
                    title: title,
                    summary: summary,
                    contentHTML: rawContent,
                    date: normalizeDisplayText(date),
                    shareURL: shareURL,
                    imageURL: imageURL
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
