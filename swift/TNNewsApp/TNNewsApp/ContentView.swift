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
                                                .foregroundStyle(.white)
                                        }
                                    }
                                    .listRowBackground(Color(newsRowBackground))
                                    .listRowSeparatorTint(.white.opacity(0.16))
                                }
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
                                Text(selectedLanguage.title(for: selectedDestination))
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
                                onSelectMenuItem: { destination in
                                    selectedDestination = destination
                                    showMenu = false
                                },
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

    private var items: [(destination: MenuDestination, label: String, icon: String)] {
        [
            (.news, selectedLanguage.title(for: .news), "newspaper"),
            (.dossiers, selectedLanguage.title(for: .dossiers), "folder"),
            (.mostRead, selectedLanguage.title(for: .mostRead), "book"),
            (.videos, selectedLanguage.title(for: .videos), "play.circle"),
            (.jokes, selectedLanguage.title(for: .jokes), "face.smiling"),
            (.prayerTimes, selectedLanguage.title(for: .prayerTimes), "moon.stars"),
            (.favorites, selectedLanguage.title(for: .favorites), "heart"),
            (.settings, selectedLanguage.title(for: .settings), "gearshape"),
            (.about, selectedLanguage.title(for: .about), "info.circle")
        ]
    }

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
                    ForEach(UiLanguage.allCases) { lng in
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
                        ForEach(items, id: \.destination) { item in
                            Button {
                                onSelectMenuItem(item.destination)
                            } label: {
                                HStack {
                                    Image(systemName: item.icon)
                                        .frame(width: 24)
                                        .foregroundStyle(Color(androidGreen))
                                    Text(item.label)
                                        .font(.system(size: 21, weight: .semibold))
                                        .foregroundStyle(.white)
                                    Spacer()
                                }
                                .contentShape(Rectangle())
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.horizontal, 22)
                    .padding(.top, 8)
                }

                Spacer()

                Text(selectedLanguage.privacyLabel)
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
            let (data, response) = try await URLSession.shared.data(from: newsURL)
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
