import SwiftUI

struct ContentView: View {
    @StateObject private var vm = BootstrapViewModel()
    @State private var showSplash = true
    @State private var showMenu = false
    @State private var selectedLanguage: UiLanguage = .fr
    @State private var selectedDestination: MenuDestination = .news

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
                                                .foregroundStyle(.black)
                                        }
                                    }
                                    .listRowBackground(Color.white)
                                    .listRowSeparatorTint(Color.black.opacity(0.08))
                                }
                            }
                        }
                        .scrollContentBackground(.hidden)
                        .background(Color("#F6F7F7"))
                        .toolbar {
                            ToolbarItem(placement: .topBarLeading) {
                                Button {
                                    withAnimation(.easeInOut(duration: 0.2)) { showMenu = true }
                                } label: {
                                    Image(systemName: "line.3.horizontal")
                                        .font(.system(size: 22, weight: .bold))
                                        .foregroundStyle(Color(androidGreen))
                                }
                            }

                            ToolbarItem(placement: .principal) {
                                TNCompactLogo()
                            }

                            ToolbarItem(placement: .topBarTrailing) {
                                Text(selectedLanguage.menuBadgeTitle)
                                    .font(.system(size: 20, weight: .semibold))
                                    .foregroundStyle(.black)
                            }
                        }
                        .toolbarBackground(.white, for: .navigationBar)
                        .toolbarBackground(.visible, for: .navigationBar)
                        .toolbarColorScheme(.light, for: .navigationBar)
                    }
                    .overlay(alignment: .leading) {
                        if showMenu {
                            ZStack(alignment: .leading) {
                                Color.black.opacity(0.22)
                                    .ignoresSafeArea()
                                    .onTapGesture { withAnimation(.easeInOut(duration: 0.2)) { showMenu = false } }

                                LegacySideMenuView(
                                    selectedLanguage: $selectedLanguage,
                                    onSelectMenuItem: { destination in
                                        selectedDestination = destination
                                        withAnimation(.easeInOut(duration: 0.2)) { showMenu = false }
                                    },
                                    onClose: { withAnimation(.easeInOut(duration: 0.2)) { showMenu = false } }
                                )
                                .frame(maxWidth: 330)
                                .transition(.move(edge: .leading))
                            }
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
                    await vm.loadAll(language: selectedLanguage, destination: selectedDestination)
                }
                .onChange(of: selectedLanguage) { _, newLanguage in
                    Task { await vm.loadAll(language: newLanguage, destination: selectedDestination) }
                }
                .onChange(of: selectedDestination) { _, newDestination in
                    Task { await vm.loadAll(language: selectedLanguage, destination: newDestination) }
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

enum UiLanguage: String, CaseIterable, Identifiable {
    case fr
    case en
    case ar

    var id: String { rawValue }

    func endpointURL(for destination: MenuDestination) -> String? {
        switch self {
        case .fr:
            switch destination {
            case .news: return "https://preprod.tunisienumerique.com/results.json"
            case .dossiers: return "https://jsondata.tunisienumerique.com/dossiers.json"
            case .mostRead: return "https://www.tunisienumerique.com/jsondata/popular.json"
            case .videos: return "https://preprod.tunisienumerique.com/jsondata/videotunisienumerique"
            case .jokes: return "https://humour.tunisienumerique.com/hummor.json"
            case .prayerTimes, .favorites, .settings, .about: return nil
            }
        case .ar:
            switch destination {
            case .news: return "https://arabe.tunisienumerique.com/results.json"
            case .dossiers: return "https://arabe.tunisienumerique.com/dossiers.json"
            case .mostRead: return "https://arabe.tunisienumerique.com/jsondata/popular.json"
            case .videos: return "https://preprod.tunisienumerique.com/jsondata/videotunisienumerique"
            case .jokes: return "https://humour.tunisienumerique.com/hummor.json"
            case .prayerTimes, .favorites, .settings, .about: return nil
            }
        case .en:
            switch destination {
            case .news: return "https://news-tunisia.tunisienumerique.com/results.json"
            case .dossiers: return "https://news-tunisia.tunisienumerique.com/jsondata/dossiers.json"
            case .mostRead: return "https://news-tunisia.tunisienumerique.com/jsondata/popular.json"
            case .videos: return "https://preprod.tunisienumerique.com/jsondata/videotunisienumerique"
            case .jokes: return "https://humour.tunisienumerique.com/hummor.json"
            case .prayerTimes, .favorites, .settings, .about: return nil
            }
        }
    }
}

enum MenuDestination: CaseIterable {
    case news
    case dossiers
    case mostRead
    case videos
    case jokes
    case prayerTimes
    case favorites
    case settings
    case about
}

private extension UiLanguage {
    var menuBadgeTitle: String {
        switch self {
        case .ar: return "Ar"
        case .fr: return "Fr"
        case .en: return "En"
        }
    }

    func title(for destination: MenuDestination) -> String {
        switch (self, destination) {
        case (.fr, .news): return "Actualités"
        case (.fr, .dossiers): return "Dossiers"
        case (.fr, .mostRead): return "Les plus lus"
        case (.fr, .videos): return "Vidéos"
        case (.fr, .jokes): return "Blagues"
        case (.fr, .prayerTimes): return "Horaires de prière"
        case (.fr, .favorites): return "Mes favoris"
        case (.fr, .settings): return "Paramètres"
        case (.fr, .about): return "Qui sommes-nous"
        case (.en, .news): return "News"
        case (.en, .dossiers): return "Files"
        case (.en, .mostRead): return "Most read"
        case (.en, .videos): return "Videos"
        case (.en, .jokes): return "Jokes"
        case (.en, .prayerTimes): return "Prayer times"
        case (.en, .favorites): return "Favorites"
        case (.en, .settings): return "Settings"
        case (.en, .about): return "About us"
        case (.ar, .news): return "الأخبار"
        case (.ar, .dossiers): return "ملفات"
        case (.ar, .mostRead): return "الاكثر قراءة"
        case (.ar, .videos): return "فيديو"
        case (.ar, .jokes): return "نكتة"
        case (.ar, .prayerTimes): return "أوقات الصلاة"
        case (.ar, .favorites): return "مفضلاتي"
        case (.ar, .settings): return "الإعدادات"
        case (.ar, .about): return "من نحن"
        }
    }

    var privacyLabel: String {
        switch self {
        case .ar: return "سياسة الخصوصية"
        case .fr: return "Politique de confidentialité"
        case .en: return "Privacy policy"
        }
    }
}

struct LegacySideMenuView: View {
    @Binding var selectedLanguage: UiLanguage
    let onSelectMenuItem: (MenuDestination) -> Void
    let onClose: () -> Void

    private var categoryItems: [(destination: MenuDestination, label: String)] {
        [
            (.news, "A la une"),
            (.dossiers, "Monde"),
            (.mostRead, "Politique"),
            (.videos, "Economie"),
            (.jokes, "Autos"),
            (.prayerTimes, "Sport"),
            (.favorites, "Tech & net"),
            (.settings, "Société"),
            (.about, "Recette")
        ]
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 6) {
                        Text("T")
                            .font(.system(size: 24, weight: .heavy))
                            .foregroundStyle(.white)
                            .frame(width: 42, height: 42)
                            .background(Color(androidGreen))
                        Text("N")
                            .font(.system(size: 24, weight: .heavy))
                            .foregroundStyle(.white)
                            .frame(width: 42, height: 42)
                            .background(Color(androidGreen))
                    }
                    Text("TUNISIE NUMÉRIQUE")
                        .font(.system(size: 19, weight: .heavy))
                        .foregroundStyle(Color(androidGreen))
                    Text("LA TUNISIE À L'ÈRE DE LA DÉMOCRATIE")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(Color(androidGreen).opacity(0.85))
                }
                Spacer()
                Button(action: onClose) {
                    Image(systemName: "xmark.circle")
                        .font(.system(size: 28, weight: .regular))
                        .foregroundStyle(.black.opacity(0.75))
                }
            }
            .padding(20)
            .background(Color.white)

            VStack(spacing: 12) {
                ForEach(categoryItems, id: \.label) { item in
                    Button {
                        onSelectMenuItem(item.destination)
                    } label: {
                        HStack {
                            Text(item.label)
                                .font(.system(size: 20, weight: .semibold))
                                .foregroundStyle(item.destination == .news ? Color(androidGreen) : .black)
                            Spacer()
                        }
                        .padding(.horizontal, 18)
                        .frame(height: 56)
                        .background(Color("#ECEEEE"))
                        .overlay(
                            RoundedRectangle(cornerRadius: 16)
                                .stroke(item.destination == .news ? Color(androidGreen) : Color.clear, lineWidth: 2)
                        )
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 18)
            .padding(.vertical, 12)

            Divider().padding(.top, 4)

            Button {
                onSelectMenuItem(.about)
            } label: {
                HStack {
                    Text("A propos")
                        .font(.system(size: 20, weight: .medium))
                        .foregroundStyle(.gray)
                    Spacer()
                }
                .padding(.horizontal, 18)
                .frame(height: 62)
            }
            .buttonStyle(.plain)

            Divider()
            Button {
                onSelectMenuItem(.settings)
            } label: {
                HStack {
                    Text("Paramètres")
                        .font(.system(size: 20, weight: .medium))
                        .foregroundStyle(.gray)
                    Spacer()
                }
                .padding(.horizontal, 18)
                .frame(height: 62)
            }
            .buttonStyle(.plain)
            Spacer()
        }
        .frame(maxHeight: .infinity, alignment: .top)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
        .padding(.vertical, 10)
    }
}

struct TNCompactLogo: View {
    var body: some View {
        HStack(spacing: 5) {
            Text("T")
                .font(.system(size: 22, weight: .heavy))
                .foregroundStyle(.white)
                .frame(width: 36, height: 36)
                .background(Color(androidGreen))
            Text("N")
                .font(.system(size: 22, weight: .heavy))
                .foregroundStyle(.white)
                .frame(width: 36, height: 36)
                .background(Color(androidGreen))
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
private let newsNavBackground = "#FFFFFF"
private let newsScreenBackground = "#F6F7F7"
private let newsRowBackground = "#FFFFFF"

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

    func loadAll(language: UiLanguage, destination: MenuDestination) async {
        await loadNews(language: language, destination: destination)
        disablePrayersTemporarily()
    }

    func loadNews(language: UiLanguage, destination: MenuDestination) async {
        isLoadingNews = true
        newsError = nil
        guard let endpoint = language.endpointURL(for: destination),
              let newsURL = URL(string: endpoint) else {
            newsItems = []
            newsError = "Section '\(language.title(for: destination))' en cours d'intégration."
            isLoadingNews = false
            return
        }
        print("[TN-iOS] Loading news from: \(newsURL.absoluteString)")

        do {
            let (data, response) = try await requestDataWithFallbackIfNeeded(url: newsURL, destination: destination)
            if let http = response as? HTTPURLResponse {
                print("[TN-iOS] News HTTP status: \(http.statusCode)")
            }

            let payload = try JSONSerialization.jsonObject(with: data)

            if destination == .videos {
                let rows = parseVideoRows(payload)
                newsItems = rows
                newsError = rows.isEmpty ? "Aucune vidéo disponible." : nil
                isLoadingNews = false
                print("[TN-iOS] Videos loaded count: \(rows.count)")
                return
            }

            let json = payload as? [String: Any]
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

    private func requestDataWithFallbackIfNeeded(url: URL, destination: MenuDestination) async throws -> (Data, URLResponse) {
        let primary = try await URLSession.shared.data(from: url)

        // Android "video" endpoint may require ".json" suffix in some environments.
        if destination == .videos, !url.absoluteString.hasSuffix(".json") {
            let primaryText = String(data: primary.0, encoding: .utf8)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
            let looksLikeJSON = primaryText.hasPrefix("{") || primaryText.hasPrefix("[")
            if primaryText.isEmpty || primaryText == "{}" || !looksLikeJSON {
                if let fallbackURL = URL(string: url.absoluteString + ".json") {
                    print("[TN-iOS] Videos fallback URL: \(fallbackURL.absoluteString)")
                    return try await URLSession.shared.data(from: fallbackURL)
                }
            }
        }

        return primary
    }

    private func parseVideoRows(_ payload: Any) -> [NewsRow] {
        let youtubeBase = "https://www.youtube.com/watch?v="
        let array: [[String: Any]]

        if let dict = payload as? [String: Any],
           let videos = dict["video"] as? [[String: Any]] {
            array = videos
        } else if let arr = payload as? [[String: Any]] {
            array = arr
        } else {
            return []
        }

        return array.enumerated().map { idx, row in
            let videoID = (row["id"] as? String) ?? String(idx)
            let title = normalizeDisplayText(
                (row["title"] as? String) ??
                (row["titre"] as? String) ??
                "Sans titre"
            )
            let date = normalizeDisplayText(
                (row["date"] as? String) ??
                (row["published"] as? String) ??
                ""
            )
            let imageURL = (row["image"] as? String) ?? ""
            let shareURL = youtubeBase + videoID

            return NewsRow(
                id: videoID,
                title: title,
                summary: date,
                contentHTML: title,
                date: date,
                shareURL: shareURL,
                imageURL: imageURL
            )
        }
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

struct HTMLContentView: View {
    let html: String

    var body: some View {
        Group {
            if let attributed = html.asAttributedString {
                Text(attributed)
                    .font(.body)
            } else {
                Text(html.strippingHTMLTags)
                    .font(.body)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal)
        .padding(.bottom, 12)
    }
}

private extension String {
    var asAttributedString: AttributedString? {
        guard let data = data(using: .utf8),
              let nsAttributed = try? NSAttributedString(
                data: data,
                options: [
                    .documentType: NSAttributedString.DocumentType.html,
                    .characterEncoding: String.Encoding.utf8.rawValue
                ],
                documentAttributes: nil
              ) else {
            return nil
        }
        return AttributedString(nsAttributed)
    }

    var strippingHTMLTags: String {
        replacingOccurrences(of: "<[^>]+>", with: " ", options: .regularExpression)
            .replacingOccurrences(of: "&nbsp;", with: " ")
            .replacingOccurrences(of: "&amp;", with: "&")
            .replacingOccurrences(of: "&quot;", with: "\"")
            .replacingOccurrences(of: "&#039;", with: "'")
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }
}

#Preview {
    ContentView()
}
