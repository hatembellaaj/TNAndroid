import SwiftUI
import WebKit

struct ContentView: View {
    @StateObject private var vm = BootstrapViewModel()
    @State private var showSplash = true
    @State private var showMenu = false
    @State private var selectedLanguage: AppLanguage = .fr

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
                                                            .foregroundStyle(.white.opacity(0.85))
                                                    }
                                                }
                                                .frame(width: 54, height: 54)
                                                .background(Color.white.opacity(0.08))
                                                .clipShape(RoundedRectangle(cornerRadius: 8))
                                            }

                                            Text(item.title)
                                                .font(.headline)
                                                .lineLimit(3)
                                                .foregroundStyle(.white)
                                        }
                                    }
                                }
                                .listRowBackground(Color(newsRowBackground))
                            }
                        }
                        .scrollContentBackground(.hidden)
                        .background(Color(newsScreenBackground))
                        .toolbar {
                            ToolbarItem(placement: .topBarLeading) {
                                Text(selectedLanguage.menuBadgeTitle)
                                    .font(.system(size: 14, weight: .bold))
                                    .padding(6)
                                    .background(Color.white.opacity(0.12))
                                    .clipShape(RoundedRectangle(cornerRadius: 6))
                                    .foregroundStyle(.white)
                            }

                            ToolbarItem(placement: .principal) {
                                Text(selectedLanguage.newsTitle)
                                    .font(.custom("AvenirNext-DemiBold", size: 24))
                                    .foregroundStyle(.white)
                            }

                            ToolbarItem(placement: .topBarTrailing) {
                                Button {
                                    showMenu = true
                                } label: {
                                    Image(systemName: "line.3.horizontal")
                                        .font(.system(size: 20, weight: .semibold))
                                        .foregroundStyle(.white)
                                }
                            }
                        }
                        .toolbarBackground(Color(newsNavBackground), for: .navigationBar)
                        .toolbarBackground(.visible, for: .navigationBar)
                        .toolbarColorScheme(.dark, for: .navigationBar)
                        .sheet(isPresented: $showMenu) {
                            LegacySideMenuView(
                                selectedLanguage: $selectedLanguage,
                                onClose: { showMenu = false }
                            )
                            .presentationDetents([.fraction(0.90)])
                        }
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
                    await vm.loadAll(language: selectedLanguage)
                }
                .onChange(of: selectedLanguage) { _, newLanguage in
                    Task { await vm.loadAll(language: newLanguage) }
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

private extension AppLanguage {
    var menuBadgeTitle: String {
        switch self {
        case .ar: return "Ar"
        case .fr: return "Fr"
        case .en: return "En"
        }
    }

    var newsTitle: String {
        switch self {
        case .ar: return "الأخبار"
        case .fr: return "Actualités"
        case .en: return "News"
        }
    }
}

struct LegacySideMenuView: View {
    @Binding var selectedLanguage: AppLanguage
    let onClose: () -> Void

    private let items: [(label: String, icon: String)] = [
        ("الأخبار", "newspaper"),
        ("ملفات", "folder"),
        ("الاكثر قراءة", "book"),
        ("فيديو", "play.circle"),
        ("نكتة", "face.smiling"),
        ("أوقات الصلاة", "moon.stars"),
        ("مفضلاتي", "heart"),
        ("الإعدادات", "gearshape"),
        ("من نحن", "info.circle")
    ]

    var body: some View {
        ZStack {
            Color(newsNavBackground).ignoresSafeArea()

            VStack(spacing: 0) {
                ZStack(alignment: .top) {
                    Color(androidGreen)
                    VStack(spacing: 6) {
                        Spacer().frame(height: 20)
                        Text("TUNISIE NUMERIQUE")
                            .font(.system(size: 22, weight: .heavy))
                            .foregroundStyle(.white)
                        Text("LA TUNISIE A L'ÈRE DE LA DÉMOCRATIE")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundStyle(.white.opacity(0.9))
                        Spacer().frame(height: 18)
                    }
                }
                .frame(height: 180)

                HStack(spacing: 12) {
                    ForEach(AppLanguage.allCases) { lng in
                        Button(lng.menuBadgeTitle) { selectedLanguage = lng }
                            .font(.system(size: 14, weight: .bold))
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .background(selectedLanguage == lng ? Color(androidGreen) : Color.white.opacity(0.08))
                            .foregroundStyle(.white)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                }
                .padding(.vertical, 14)

                ScrollView {
                    VStack(spacing: 18) {
                        ForEach(items, id: \.label) { item in
                            HStack {
                                Image(systemName: item.icon)
                                    .frame(width: 24)
                                    .foregroundStyle(Color(androidGreen))
                                Text(item.label)
                                    .font(.system(size: 21, weight: .semibold))
                                    .foregroundStyle(.white)
                                Spacer()
                            }
                        }
                    }
                    .padding(.horizontal, 22)
                    .padding(.top, 8)
                }

                Spacer()

                Text("سياسة الخصوصية")
                    .font(.system(size: 26, weight: .bold))
                    .foregroundStyle(Color(androidGreen))
                    .padding(.bottom, 8)
                Text("mdweb © 2022")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(.white)
                    .padding(.bottom, 20)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .overlay(alignment: .topTrailing) {
            Button(action: onClose) {
                Image(systemName: "xmark.circle.fill")
                    .font(.system(size: 30))
                    .foregroundStyle(.white.opacity(0.85))
                    .padding(16)
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
private let newsNavBackground = "#2F3444"
private let newsScreenBackground = "#2E3342"
private let newsRowBackground = "#33394A"

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

    func loadAll(language: AppLanguage) async {
        await loadNews(language: language)
        disablePrayersTemporarily()
    }

    func loadNews(language: AppLanguage) async {
        isLoadingNews = true
        newsError = nil
        guard let newsURL = Endpoint.newsInit(language).url else {
            newsError = "URL news invalide"
            isLoadingNews = false
            return
        }
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
