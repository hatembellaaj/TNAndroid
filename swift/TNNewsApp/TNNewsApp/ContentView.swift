import SwiftUI
import UIKit

struct ContentView: View {
    @StateObject private var vm = BootstrapViewModel()
    @State private var showSplash = true
    @State private var showMenu = false
    @State private var showLanguagePicker = false
    @State private var selectedLanguage: UiLanguage = .fr
    @State private var selectedDestination: MenuDestination = .news
    @State private var selectedCategoryFilter: String? = nil
    @State private var selectedTab = 0
    @State private var favoriteItems: [BootstrapViewModel.NewsRow] = TNFavoriteArticlesStorage.load()
    @State private var splashMessage = "Chargement des données..."
    @State private var splashProgress: Double = 0.1
    @State private var splashAdImageURL: URL?
    @State private var showSplashAd = false

    var body: some View {
        Group {
            if showSplash {
                AndroidStyleSplashView(
                    message: splashMessage,
                    progress: splashProgress,
                    adImageURL: splashAdImageURL,
                    showAd: showSplashAd
                )
            } else {
                TabView(selection: $selectedTab) {
                    NavigationStack {
                        Group {
                            if vm.isLoadingNews {
                                ProgressView("Chargement des articles...")
                            } else if let error = vm.newsError {
                                Text(error).foregroundStyle(.red)
                            } else {
                                HomeNewsFeedView(
                                    newsItems: vm.newsItems,
                                    selectedLanguage: selectedLanguage,
                                    categoryFilter: selectedCategoryFilter,
                                    favoriteItems: $favoriteItems
                                )
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
                                Button {
                                    showLanguagePicker = true
                                } label: {
                                    Text(selectedLanguage.menuBadgeTitle)
                                        .font(.system(size: 20, weight: .semibold))
                                        .foregroundStyle(.black)
                                }
                            }
                        }
                        .toolbarBackground(.white, for: .navigationBar)
                        .toolbarBackground(.visible, for: .navigationBar)
                        .toolbarColorScheme(.light, for: .navigationBar)
                        .sheet(isPresented: $showLanguagePicker) {
                            LanguageSelectionSheet(selectedLanguage: $selectedLanguage)
                                .presentationDetents([.fraction(0.58)])
                        }
                    }
                    .overlay(alignment: .leading) {
                        if showMenu {
                            GeometryReader { geo in
                                ZStack(alignment: .leading) {
                                    Color.black.opacity(0.22)
                                        .ignoresSafeArea()
                                        .onTapGesture { withAnimation(.easeInOut(duration: 0.2)) { showMenu = false } }

                                    LegacySideMenuView(
                                        selectedLanguage: $selectedLanguage,
                                        selectedDestination: $selectedDestination,
                                        selectedCategoryFilter: $selectedCategoryFilter,
                                        onSelectMenuItem: { destination in
                                            selectedDestination = destination
                                            withAnimation(.easeInOut(duration: 0.2)) { showMenu = false }
                                        },
                                        onClose: { withAnimation(.easeInOut(duration: 0.2)) { showMenu = false } }
                                    )
                                    .frame(maxWidth: 330)
                                    .padding(.top, geo.safeAreaInsets.top + 6)
                                    .padding(.bottom, geo.safeAreaInsets.bottom + 6)
                                    .transition(.move(edge: .leading))
                                }
                            }
                        }
                    }
                    .tabItem {
                        Image("Home")
                            .renderingMode(.template)
                        Text("Home")
                    }
                    .tag(0)

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
                    .tabItem {
                        Image("Priere")
                            .renderingMode(.template)
                        Text("Prières")
                    }
                    .tag(1)

                    NavigationStack {
                        FavoritesNewsFeedView(favoriteItems: $favoriteItems)
                    }
                    .tabItem {
                        Image("Favorite")
                            .renderingMode(.template)
                        Text("Favoris")
                    }
                    .tag(2)

                    NavigationStack {
                        Top24NewsFeedView(
                            selectedLanguage: selectedLanguage,
                            favoriteItems: $favoriteItems
                        )
                    }
                    .tabItem {
                        Image("Top24")
                            .renderingMode(.template)
                        Text("Top 24h")
                    }
                    .tag(3)
                }
                .tint(Color(androidGreen))
                .task {
                    await vm.loadAll(language: selectedLanguage, destination: selectedDestination)
                }
                .onChange(of: selectedLanguage) { _, newLanguage in
                    Task { await vm.loadAll(language: newLanguage, destination: selectedDestination) }
                }
                .onChange(of: selectedDestination) { _, newDestination in
                    if newDestination != .news {
                        selectedCategoryFilter = nil
                    }
                    Task { await vm.loadAll(language: selectedLanguage, destination: newDestination) }
                }
                .onChange(of: selectedTab) { _, newTab in
                    guard newTab == 0 else { return }
                    selectedCategoryFilter = nil
                    selectedDestination = .news
                    Task { await vm.loadAll(language: selectedLanguage, destination: .news) }
                }
                .onChange(of: favoriteItems) { _, newValue in
                    TNFavoriteArticlesStorage.save(newValue)
                }
            }
        }
        .task {
            guard showSplash else { return }
            await runSplashSequence()
            withAnimation(.easeOut(duration: 0.25)) {
                showSplash = false
            }
        }
        .environment(\.layoutDirection, selectedLanguage == .ar ? .rightToLeft : .leftToRight)
    }

    @MainActor
    private func runSplashSequence() async {
        splashProgress = 0.1
        splashMessage = "Chargement des données..."
        showSplashAd = false

        async let pubImageURL = fetchSplashPubImageURL()

        let steps: [(String, Double)] = [
            ("Chargement des données...", 0.25),
            ("Préparation du contenu...", 0.50),
            ("Finalisation...", 0.80),
            ("Prêt !", 1.0)
        ]

        for (message, progress) in steps {
            splashMessage = message
            splashProgress = progress
            try? await Task.sleep(nanoseconds: 900_000_000)
        }

        splashAdImageURL = await pubImageURL
        if splashAdImageURL != nil {
            showSplashAd = true
            try? await Task.sleep(nanoseconds: 5_000_000_000)
            showSplashAd = false
        }
    }

    private func fetchSplashPubImageURL() async -> URL? {
        guard let url = URL(string: "https://jsondata.tunisienumerique.com/pub.json") else { return nil }
        do {
            let (data, response) = try await URLSession.shared.data(from: url)
            guard let http = response as? HTTPURLResponse, (200...299).contains(http.statusCode) else { return nil }

            if let root = try JSONSerialization.jsonObject(with: data) as? [String: Any] {
                let pub = root["pub"] as? [String: Any] ?? root
                if let image = pub["image"] as? String, let imageURL = URL(string: image), !image.isEmpty {
                    return imageURL
                }
            }
        } catch {
            print("[TN-iOS][Splash] Pub fetch failed: \(error.localizedDescription)")
        }
        return nil
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

    var selectionLabel: String {
        switch self {
        case .ar: return "العربية"
        case .fr: return "Français"
        case .en: return "English"
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
    @Binding var selectedDestination: MenuDestination
    @Binding var selectedCategoryFilter: String?
    let onSelectMenuItem: (MenuDestination) -> Void
    let onClose: () -> Void

    private var primaryCategoryItem: (label: String, filter: String?) {
        switch selectedLanguage {
        case .fr: return ("A la une", "À la une")
        case .en: return ("Top stories", "Top stories")
        case .ar: return ("أهم الأخبار", "أهم الأخبار")
        }
    }

    private var categoryItems: [(label: String, filter: String?)] {
        switch selectedLanguage {
        case .fr:
            return [
                ("Monde", "Monde"),
                ("Politique", "Politique"),
                ("Economie", "Economie"),
                ("Societe", "Société"),
                ("Sport", "Sport"),
                ("Tech & net", "Tech"),
                ("Life Style", "Life Style")
            ]
        case .en:
            return [
                ("World", "World"),
                ("Politics", "Politics"),
                ("Economy", "Economy"),
                ("Society", "Society"),
                ("Sports", "Sports"),
                ("Tech & net", "Tech"),
                ("Lifestyle", "Lifestyle")
            ]
        case .ar:
            return [
                ("العالم", "العالم"),
                ("سياسة", "سياسة"),
                ("اقتصاد", "اقتصاد"),
                ("مجتمع", "مجتمع"),
                ("رياضة", "رياضة"),
                ("تكنولوجيا", "تكنولوجيا"),
                ("نمط الحياة", "نمط الحياة")
            ]
        }
    }

    private var videosLabel: String {
        switch selectedLanguage {
        case .fr: return "Videos"
        case .en: return "Videos"
        case .ar: return "فيديو"
        }
    }

    private var menuTitle: String {
        switch selectedLanguage {
        case .fr: return "TUNISIE NUMÉRIQUE"
        case .en: return "TUNISIA DIGITAL"
        case .ar: return "تونس الرقمية"
        }
    }

    private var menuSubtitle: String {
        switch selectedLanguage {
        case .fr: return "LA TUNISIE À L'ÈRE DE LA DÉMOCRATIE"
        case .en: return "TUNISIA IN THE DIGITAL ERA"
        case .ar: return "تونس في العصر الرقمي"
        }
    }

    private var aboutLabel: String {
        switch selectedLanguage {
        case .fr: return "À propos"
        case .en: return "About"
        case .ar: return "من نحن"
        }
    }

    private var settingsLabel: String {
        switch selectedLanguage {
        case .fr: return "Paramètres"
        case .en: return "Settings"
        case .ar: return "الإعدادات"
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            // Header: title + small TN logo, X close button top-right
            ZStack(alignment: .topTrailing) {
                VStack(spacing: 4) {
                    HStack(alignment: .top, spacing: 4) {
                        Text(menuTitle)
                            .font(.system(size: 22, weight: .heavy))
                            .foregroundStyle(Color(androidGreen))
                        TNCompactLogo()
                            .scaleEffect(0.55, anchor: .center)
                            .frame(width: 46, height: 28)
                            .offset(y: -10)
                    }
                    Text(menuSubtitle)
                        .font(.system(size: 11, weight: .bold))
                        .foregroundStyle(Color(androidGreen).opacity(0.85))
                }
                .frame(maxWidth: .infinity)
                .padding(.top, 56)
                .padding(.bottom, 18)

                Button(action: onClose) {
                    Image(systemName: "xmark.circle")
                        .font(.system(size: 26, weight: .regular))
                        .foregroundStyle(.black.opacity(0.75))
                }
                .padding(.top, 16)
                .padding(.trailing, 18)
            }
            .background(Color.white)

            // "À la une" alone in its own card
            VStack(spacing: 0) {
                menuCategoryButton(label: primaryCategoryItem.label, filter: primaryCategoryItem.filter)
            }
            .background(Color("#ECEEEE"))
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
            .padding(.horizontal, 18)
            .padding(.top, 18)

            // Categories card (Monde, Politique, ..., Videos)
            VStack(spacing: 0) {
                ForEach(Array(categoryItems.enumerated()), id: \.offset) { _, item in
                    menuCategoryButton(label: item.label, filter: item.filter)
                    Divider().padding(.leading, 18)
                }
                videosMenuButton()
            }
            .background(Color("#ECEEEE"))
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
            .padding(.horizontal, 18)
            .padding(.top, 16)

            // About / Settings (gray, separators)
            VStack(spacing: 0) {
                Button {
                    onSelectMenuItem(.about)
                } label: {
                    HStack {
                        Text(aboutLabel)
                            .font(.system(size: 17, weight: .medium))
                            .foregroundStyle(.gray)
                        Spacer()
                    }
                    .padding(.horizontal, 18)
                    .frame(height: 52)
                }
                .buttonStyle(.plain)

                Divider().padding(.leading, 18)

                Button {
                    onSelectMenuItem(.settings)
                } label: {
                    HStack {
                        Text(settingsLabel)
                            .font(.system(size: 17, weight: .medium))
                            .foregroundStyle(.gray)
                        Spacer()
                    }
                    .padding(.horizontal, 18)
                    .frame(height: 52)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 18)
            .padding(.top, 24)

            Spacer()
        }
        .frame(maxHeight: .infinity, alignment: .top)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
        .padding(.vertical, 10)
    }

    private func menuCategoryButton(label: String, filter: String?) -> some View {
        Button {
            selectedCategoryFilter = filter
            onSelectMenuItem(.news)
        } label: {
            HStack {
                Text(label)
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(isSelectedCategory(filter) ? Color(androidGreen) : .black)
                Spacer()
            }
            .padding(.horizontal, 18)
            .frame(height: 52)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private func videosMenuButton() -> some View {
        Button {
            onSelectMenuItem(.videos)
        } label: {
            HStack {
                Text(videosLabel)
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(selectedDestination == .videos ? Color(androidGreen) : .black)
                Spacer()
            }
            .padding(.horizontal, 18)
            .frame(height: 52)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private func isSelectedCategory(_ filter: String?) -> Bool {
        selectedDestination == .news && filter == selectedCategoryFilter
    }
}

struct TNCompactLogo: View {
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color(androidGreen))
                .frame(width: 84, height: 52)

            HStack(spacing: 5) {
                logoLetter("T")
                logoLetter("N")
            }
        }
    }

    private func logoLetter(_ letter: String) -> some View {
        Text(letter)
            .font(.system(size: 24, weight: .heavy))
            .foregroundStyle(Color(androidGreen))
            .frame(width: 30, height: 32)
            .background(
                RoundedRectangle(cornerRadius: 5, style: .continuous)
                    .fill(Color.white)
            )
            .shadow(color: .black.opacity(0.08), radius: 1, x: 0, y: 1)
    }
}

struct TNLauncherLogoBadge: View {
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 22, style: .continuous)
                .fill(Color(androidGreen))
                .frame(width: 124, height: 124)

            HStack(spacing: 8) {
                launcherLetter("T")
                launcherLetter("N")
            }
        }
    }

    private func launcherLetter(_ letter: String) -> some View {
        Text(letter)
            .font(.system(size: 46, weight: .black))
            .foregroundStyle(Color(androidGreen))
            .frame(width: 42, height: 50)
            .background(.white)
            .clipShape(RoundedRectangle(cornerRadius: 7, style: .continuous))
    }
}

struct LanguageSelectionSheet: View {
    @Binding var selectedLanguage: UiLanguage
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "globe")
                .font(.system(size: 64))
                .foregroundStyle(Color(androidGreen))
                .padding(.top, 12)
            Text("الرجاء إختيار اللغة\nPlease select a language\nVeuillez sélectionner une langue")
                .multilineTextAlignment(.center)
                .font(.system(size: 24, weight: .semibold))
                .foregroundStyle(.black.opacity(0.85))
                .padding(.horizontal, 16)

            Divider().padding(.top, 8)
            languageRow(.fr)
            Divider()
            languageRow(.en)
            Divider()
            languageRow(.ar)
            Divider()
            Spacer()
        }
        .background(Color.white)
    }

    @ViewBuilder
    private func languageRow(_ lang: UiLanguage) -> some View {
        Button {
            selectedLanguage = lang
            dismiss()
        } label: {
            HStack {
                Image(systemName: selectedLanguage == lang ? "largecircle.fill.circle" : "circle")
                    .foregroundStyle(selectedLanguage == lang ? Color(androidGreen) : .gray)
                Text(lang.selectionLabel)
                    .foregroundStyle(.black)
                    .font(.system(size: 26, weight: .medium))
                Spacer()
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 10)
        }
        .buttonStyle(.plain)
    }
}

struct HomeNewsFeedView: View {
    let newsItems: [BootstrapViewModel.NewsRow]
    let selectedLanguage: UiLanguage
    let categoryFilter: String?
    @Binding var favoriteItems: [BootstrapViewModel.NewsRow]
    @State private var currentTopStoryIndex = 0
    @State private var selectedItem: BootstrapViewModel.NewsRow?
    private var favoriteIDs: Set<String> { Set(favoriteItems.map(\.id)) }

    private var displayedNews: [BootstrapViewModel.NewsRow] {
        if isHomeMode {
            return filteredNews(for: defaultNewsCategoryLabel)
        }

        guard let filter = categoryFilter?.trimmingCharacters(in: .whitespacesAndNewlines),
              !filter.isEmpty else { return newsItems }

        return filteredNews(for: filter)
    }

    private func filteredNews(for filter: String) -> [BootstrapViewModel.NewsRow] {
        let normalizedFilter = normalizedCategoryToken(filter)
        let filterCandidates = expandedCategoryCandidates(from: normalizedFilter)
        return newsItems.filter { item in
            let tokens = item.category
                .split(whereSeparator: { [",", ";", "|", "،"].contains($0) })
                .map { normalizedCategoryToken(String($0)) }
                .filter { !$0.isEmpty }

            return tokens.contains { token in
                filterCandidates.contains { candidate in
                    token == candidate ||
                    token.contains(candidate) ||
                    candidate.contains(token)
                }
            }
        }
    }

    private func normalizedCategoryToken(_ raw: String) -> String {
        let cleaned = raw.folding(options: [.diacriticInsensitive, .caseInsensitive], locale: .current)
        return cleaned
            .replacingOccurrences(of: "[^\\p{L}\\p{N}]+", with: "", options: .regularExpression)
            .lowercased()
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private func expandedCategoryCandidates(from normalizedFilter: String) -> [String] {
        guard !normalizedFilter.isEmpty else { return [] }
        var values: Set<String> = [normalizedFilter]

        switch normalizedFilter {
        case "news", "actualites", "actualite":
            values.formUnion(["news", "actualites", "actualite", "اخبار", "الاخبار", "latest", "new"])
        case "اخبار", "الاخبار":
            values.formUnion(["news", "actualites", "actualite", "اخبار", "الاخبار"])
        case "alaune", "topstories", "اهمالاخبار":
            values.formUnion(["alaune", "topstories", "اهمالاخبار", "headline", "headlines"])
        default:
            break
        }

        return Array(values)
    }

    private var topStories: [BootstrapViewModel.NewsRow] {
        let featured = filteredNews(for: topStoriesCategoryLabel)
        return Array(featured.prefix(8))
    }

    private var topStoriesCategoryLabel: String {
        switch selectedLanguage {
        case .fr: return "À la une"
        case .en: return "Top stories"
        case .ar: return "أهم الأخبار"
        }
    }

    private var defaultNewsCategoryLabel: String {
        switch selectedLanguage {
        case .fr: return "Actualités"
        case .en: return "News"
        case .ar: return "الأخبار"
        }
    }

    private var topStoriesSectionTitle: String {
        switch selectedLanguage {
        case .fr: return "À LA UNE"
        case .en: return "TOP STORIES"
        case .ar: return "أهم الأخبار"
        }
    }

    private var defaultNewsSectionTitle: String {
        switch selectedLanguage {
        case .fr: return "ACTUALITÉS"
        case .en: return "NEWS"
        case .ar: return "الأخبار"
        }
    }

    private var emptyNewsMessage: String {
        switch selectedLanguage {
        case .fr: return "Aucun article trouvé pour cette rubrique."
        case .en: return "No articles found for this section."
        case .ar: return "لم يتم العثور على مقالات في هذا القسم."
        }
    }

    private var isHomeMode: Bool {
        guard let filter = categoryFilter?.trimmingCharacters(in: .whitespacesAndNewlines) else {
            return true
        }
        return filter.isEmpty
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 14) {
                if isHomeMode {
                    NewsSectionHeader(title: topStoriesSectionTitle)

                    TabView(selection: $currentTopStoryIndex) {
                        ForEach(Array(topStories.enumerated()), id: \.element.id) { index, item in
                            VStack(spacing: 8) {
                                TopHeadlineCard(item: item)
                                    .contentShape(Rectangle())
                                    .onTapGesture { selectedItem = item }
                                    .zIndex(0)

                                TNNewsCardActionsRow(
                                    isFavorite: favoriteIDs.contains(item.id),
                                    onToggleFavorite: { toggleFavorite(item) },
                                    onShare: { share(item) }
                                )
                                .padding(.horizontal, 12)
                                .zIndex(1)
                            }
                            .padding(.horizontal, 4)
                            .tag(index)
                        }
                    }
                    .frame(height: 315)
                    .tabViewStyle(.page(indexDisplayMode: .never))

                    if topStories.count > 1 {
                        HStack(spacing: 7) {
                            ForEach(0..<topStories.count, id: \.self) { index in
                                Circle()
                                    .fill(index == currentTopStoryIndex ? Color(androidGreen) : Color.gray.opacity(0.35))
                                    .frame(width: 9, height: 9)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.top, -2)
                    }
                }

                NewsSectionHeader(title: isHomeMode ? defaultNewsSectionTitle : (categoryFilter ?? defaultNewsSectionTitle).uppercased())
                    .padding(.top, 8)

                if displayedNews.isEmpty {
                    Text(emptyNewsMessage)
                        .font(.system(size: 15, weight: .medium))
                        .foregroundStyle(.secondary)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 10)
                } else {
                    VStack(spacing: 12) {
                        ForEach(displayedNews) { item in
                            VStack(spacing: 8) {
                                NewsFeedRowCard(item: item)
                                    .contentShape(Rectangle())
                                    .onTapGesture { selectedItem = item }
                                    .zIndex(0)

                                TNNewsCardActionsRow(
                                    isFavorite: favoriteIDs.contains(item.id),
                                    onToggleFavorite: { toggleFavorite(item) },
                                    onShare: { share(item) }
                                )
                                .padding(.horizontal, 12)
                                .zIndex(1)
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
        }
        .background(Color(newsScreenBackground))
        .navigationDestination(item: $selectedItem) { item in
            NewsHTMLDetailView(item: item)
        }
    }

    private func toggleFavorite(_ item: BootstrapViewModel.NewsRow) {
        if let idx = favoriteItems.firstIndex(where: { $0.id == item.id }) {
            favoriteItems.remove(at: idx)
        } else {
            favoriteItems.insert(item, at: 0)
        }
    }

    private func share(_ item: BootstrapViewModel.NewsRow) {
        let text = [item.title, item.shareURL].filter { !$0.isEmpty }.joined(separator: "\n")
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let root = scene.windows.first?.rootViewController else { return }
        let vc = UIActivityViewController(activityItems: [text], applicationActivities: nil)
        root.present(vc, animated: true)
    }
}

struct FavoritesNewsFeedView: View {
    @Binding var favoriteItems: [BootstrapViewModel.NewsRow]
    @State private var selectedItem: BootstrapViewModel.NewsRow?

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 14) {
                NewsSectionHeader(title: "FAVORIS")
                    .padding(.top, 8)

                if favoriteItems.isEmpty {
                    Text("Aucun article en favoris pour le moment.")
                        .font(.system(size: 15, weight: .medium))
                        .foregroundStyle(.secondary)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 10)
                } else {
                    VStack(spacing: 12) {
                        ForEach(favoriteItems) { item in
                            VStack(spacing: 8) {
                                NewsFeedRowCard(item: item)
                                    .contentShape(Rectangle())
                                    .onTapGesture { selectedItem = item }
                                    .zIndex(0)

                                TNNewsCardActionsRow(
                                    isFavorite: true,
                                    onToggleFavorite: { removeFavorite(item.id) },
                                    onShare: { share(item) }
                                )
                                .padding(.horizontal, 12)
                                .zIndex(1)
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
        }
        .background(Color(newsScreenBackground))
        .navigationDestination(item: $selectedItem) { item in
            NewsHTMLDetailView(item: item)
        }
    }

    private func removeFavorite(_ id: String) {
        favoriteItems.removeAll { $0.id == id }
    }

    private func share(_ item: BootstrapViewModel.NewsRow) {
        let text = [item.title, item.shareURL].filter { !$0.isEmpty }.joined(separator: "\n")
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let root = scene.windows.first?.rootViewController else { return }
        let vc = UIActivityViewController(activityItems: [text], applicationActivities: nil)
        root.present(vc, animated: true)
    }
}

struct Top24NewsFeedView: View {
    let selectedLanguage: UiLanguage
    @Binding var favoriteItems: [BootstrapViewModel.NewsRow]

    @State private var items: [BootstrapViewModel.NewsRow] = []
    @State private var isLoading = false
    @State private var errorMessage: String?
    @State private var selectedItem: BootstrapViewModel.NewsRow?

    private var favoriteIDs: Set<String> { Set(favoriteItems.map(\.id)) }

    private var sectionTitle: String {
        switch selectedLanguage {
        case .fr: return "TOP 24H"
        case .en: return "TOP 24H"
        case .ar: return "الأكثر قراءة"
        }
    }

    private var emptyMessage: String {
        switch selectedLanguage {
        case .fr: return "Aucun article disponible pour le moment."
        case .en: return "No articles available right now."
        case .ar: return "لا توجد مقالات متوفرة حالياً."
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 14) {
                NewsSectionHeader(title: sectionTitle)
                    .padding(.top, 8)

                if isLoading {
                    ProgressView()
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 24)
                } else if let errorMessage {
                    Text(errorMessage)
                        .foregroundStyle(.red)
                        .font(.system(size: 14, weight: .medium))
                        .padding(.horizontal, 8)
                        .padding(.vertical, 10)
                } else if items.isEmpty {
                    Text(emptyMessage)
                        .font(.system(size: 15, weight: .medium))
                        .foregroundStyle(.secondary)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 10)
                } else {
                    VStack(spacing: 12) {
                        ForEach(items) { item in
                            VStack(spacing: 8) {
                                NewsFeedRowCard(item: item)
                                    .contentShape(Rectangle())
                                    .onTapGesture { selectedItem = item }
                                    .zIndex(0)

                                TNNewsCardActionsRow(
                                    isFavorite: favoriteIDs.contains(item.id),
                                    onToggleFavorite: { toggleFavorite(item) },
                                    onShare: { share(item) }
                                )
                                .padding(.horizontal, 12)
                                .zIndex(1)
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
        }
        .background(Color(newsScreenBackground))
        .navigationDestination(item: $selectedItem) { item in
            NewsHTMLDetailView(item: item)
        }
        .task { await load() }
        .onChange(of: selectedLanguage) { _, _ in
            Task { await load() }
        }
    }

    @MainActor
    private func load() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        guard let endpoint = selectedLanguage.endpointURL(for: .mostRead),
              let url = URL(string: endpoint) else {
            errorMessage = "Endpoint Top 24h indisponible."
            items = []
            return
        }

        print("[TN-iOS][Top24] Loading from: \(url.absoluteString)")

        do {
            let (data, response) = try await URLSession.shared.data(from: url)
            if let http = response as? HTTPURLResponse {
                print("[TN-iOS][Top24] HTTP status: \(http.statusCode)")
            }
            let payload = try JSONSerialization.jsonObject(with: data)
            let json = payload as? [String: Any]
            let rootArray = payload as? [[String: Any]]
            let arrays: [[String: Any]] =
                (json?["data"] as? [[String: Any]]) ??
                (json?["results"] as? [[String: Any]]) ??
                (json?["news"] as? [[String: Any]]) ??
                (json?["popular"] as? [[String: Any]]) ??
                rootArray ?? []

            items = arrays.enumerated().map { idx, row in
                let id = (row["News_ID"] as? String) ??
                    (row["id"] as? String) ??
                    (row["id_news"] as? String) ??
                    String(idx)

                let rawTitle = (row["News_Titre"] as? String) ??
                    (row["title"] as? String) ??
                    (row["titre"] as? String) ??
                    "Sans titre"

                let rawSummary = (row["News_Description"] as? String) ??
                    (row["description"] as? String) ??
                    (row["resume"] as? String) ?? ""

                let rawCategory = (row["News_list_category"] as? String) ??
                    (row["News_Categorie"] as? String) ??
                    (row["category"] as? String) ?? ""

                let rawContent = (row["News_Contenu"] as? String) ??
                    (row["content"] as? String) ?? rawSummary

                let date = (row["News_Format_Date"] as? String) ??
                    (row["News_Date"] as? String) ??
                    (row["date"] as? String) ?? ""

                let shareURL = (row["News_Url_Partage"] as? String) ??
                    (row["shareURL"] as? String) ?? ""

                let imageURL = (row["News_Url_Image"] as? String) ??
                    (row["imageURL"] as? String) ?? ""

                return BootstrapViewModel.NewsRow(
                    id: id,
                    title: rawTitle,
                    category: rawCategory,
                    summary: rawSummary,
                    contentHTML: rawContent,
                    date: date,
                    shareURL: shareURL,
                    imageURL: imageURL
                )
            }
            print("[TN-iOS][Top24] Loaded \(items.count) articles")
        } catch {
            errorMessage = "Erreur chargement Top 24h: \(error.localizedDescription)"
            print("[TN-iOS][Top24] Error: \(error)")
            items = []
        }
    }

    private func toggleFavorite(_ item: BootstrapViewModel.NewsRow) {
        if let idx = favoriteItems.firstIndex(where: { $0.id == item.id }) {
            favoriteItems.remove(at: idx)
        } else {
            favoriteItems.insert(item, at: 0)
        }
    }

    private func share(_ item: BootstrapViewModel.NewsRow) {
        let text = [item.title, item.shareURL].filter { !$0.isEmpty }.joined(separator: "\n")
        guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let root = scene.windows.first?.rootViewController else { return }
        let vc = UIActivityViewController(activityItems: [text], applicationActivities: nil)
        root.present(vc, animated: true)
    }
}

private enum TNFavoriteArticlesStorage {
    private static let key = "tn_ios_favorite_articles_v1"

    static func load() -> [BootstrapViewModel.NewsRow] {
        guard let data = UserDefaults.standard.data(forKey: key),
              let decoded = try? JSONDecoder().decode([BootstrapViewModel.NewsRow].self, from: data) else {
            return []
        }
        return decoded
    }

    static func save(_ items: [BootstrapViewModel.NewsRow]) {
        guard let data = try? JSONEncoder().encode(items) else { return }
        UserDefaults.standard.set(data, forKey: key)
    }
}

private struct TNNewsCardActionsRow: View {
    let isFavorite: Bool
    let onToggleFavorite: () -> Void
    let onShare: () -> Void

    var body: some View {
        HStack {
            Spacer()

            Button(action: onToggleFavorite) {
                Image("Favorite")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20, height: 24)
                    .foregroundStyle(isFavorite ? Color(androidGreen) : Color(white: 0.745))
            }
            .buttonStyle(.plain)
            .contentShape(Rectangle())
            .padding(4)

            Button(action: onShare) {
                Image("Share")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 22, height: 22)
                    .foregroundStyle(Color(white: 0.745))
            }
            .buttonStyle(.plain)
            .contentShape(Rectangle())
            .padding(4)
            .padding(.leading, 14)
        }
        .allowsHitTesting(true)
    }
}

private struct NewsSectionHeader: View {
    let title: String

    var body: some View {
        HStack(spacing: 8) {
            Text(title)
                .font(.system(size: 20, weight: .heavy))
                .foregroundStyle(.black)
            RoundedRectangle(cornerRadius: 3)
                .fill(Color(androidGreen))
                .frame(width: 44, height: 7)
            Spacer()
        }
    }
}

private struct TopHeadlineCard: View {
    let item: BootstrapViewModel.NewsRow

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            NewsThumbnail(urlString: item.imageURL)
                .frame(height: 160)
                .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))

            Text(item.title)
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(.black)
                .lineLimit(2)

            Text(item.summary.isEmpty ? item.date : item.summary)
                .font(.system(size: 14))
                .foregroundStyle(.gray)
                .lineLimit(2)

            if !item.date.isEmpty {
                Text(item.date)
                    .font(.system(size: 13))
                    .foregroundStyle(.secondary)
            }
        }
        .padding(10)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

private struct NewsFeedRowCard: View {
    let item: BootstrapViewModel.NewsRow

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            NewsThumbnail(urlString: item.imageURL)
                .frame(height: 165)
                .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))

            Text(item.title)
                .font(.system(size: 19, weight: .semibold))
                .foregroundStyle(.black)
                .lineLimit(2)

            if !item.summary.isEmpty {
                Text(item.summary)
                    .font(.system(size: 14))
                    .foregroundStyle(.secondary)
                    .lineLimit(2)
            }
        }
        .padding(10)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

private struct NewsThumbnail: View {
    let urlString: String

    var body: some View {
        Group {
            if let imageURL = URL(string: urlString), !urlString.isEmpty {
                AsyncImage(url: imageURL) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                    default:
                        placeholder
                    }
                }
            } else {
                placeholder
            }
        }
        .frame(maxWidth: .infinity)
        .background(Color.gray.opacity(0.2))
    }

    private var placeholder: some View {
        Image(systemName: "newspaper")
            .resizable()
            .scaledToFit()
            .padding(24)
            .foregroundStyle(.white.opacity(0.85))
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.gray.opacity(0.25))
    }
}

struct AndroidStyleSplashView: View {
    let message: String
    let progress: Double
    let adImageURL: URL?
    let showAd: Bool

    var body: some View {
        ZStack {
            Color.white.ignoresSafeArea()

            Circle()
                .fill(Color(androidGreenSecondary).opacity(0.18))
                .frame(width: 320, height: 320)
                .offset(x: -120, y: -260)

            VStack(spacing: 16) {
                if showAd, let adImageURL {
                    AsyncImage(url: adImageURL) { phase in
                        switch phase {
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFit()
                        case .failure(_):
                            Color.black.opacity(0.08)
                                .overlay(Text("Publicité indisponible").foregroundStyle(.secondary))
                        case .empty:
                            ProgressView()
                        @unknown default:
                            ProgressView()
                        }
                    }
                    .frame(maxWidth: .infinity, maxHeight: 380)
                    .padding(.horizontal, 18)
                } else {
                    TNLauncherLogoBadge()

                    Text(message)
                        .font(.footnote)
                        .foregroundStyle(.secondary)

                    ProgressView(value: progress, total: 1.0)
                        .tint(Color(androidGreen))
                        .frame(width: 220)
                }
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
    struct NewsRow: Identifiable, Codable, Hashable {
        let id: String
        let title: String
        let category: String
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
            let rootArray = payload as? [[String: Any]]
            let arrays: [[String: Any]] =
                (json?["data"] as? [[String: Any]]) ??
                (json?["results"] as? [[String: Any]]) ??
                (json?["news"] as? [[String: Any]]) ??
                rootArray ?? []

            let mapped = arrays.enumerated().map { idx, row in
                let id = pickString(row, keys: ["News_ID", "id", "id_news", "idNews"]) ??
                    String(describing: row["id"] ?? idx)

                let rawTitle = pickString(row, keys: [
                    "News_Titre", "title", "titre", "titleNews", "post_title"
                ]) ?? "Sans titre"
                let rawSummary = pickString(row, keys: [
                    "News_Description", "description", "resume", "summary"
                ]) ?? ""
                let rawCategory = pickString(row, keys: [
                    "News_list_category", "News_Categorie", "categorie", "category", "rubrique", "category_name"
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
                    category: normalizeDisplayText(rawCategory),
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
                category: "",
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
        decodeUnicodeEscapes(text)
            .replacingOccurrences(of: "<[^>]+>", with: " ", options: .regularExpression)
            .replacingOccurrences(of: "&quot;", with: "\"")
            .replacingOccurrences(of: "&amp;", with: "&")
            .replacingOccurrences(of: "&#039;", with: "'")
            .replacingOccurrences(of: "&nbsp;", with: " ")
            .replacingOccurrences(of: "\\s+", with: " ", options: .regularExpression)
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private func decodeUnicodeEscapes(_ text: String) -> String {
        var output = ""
        var index = text.startIndex

        while index < text.endIndex {
            if text[index] == "\\",
               text.index(index, offsetBy: 1, limitedBy: text.endIndex) != nil {
                let uIndex = text.index(after: index)
                if uIndex < text.endIndex, text[uIndex] == "u" {
                    let start = text.index(uIndex, offsetBy: 1)
                    let end = text.index(start, offsetBy: 4, limitedBy: text.endIndex) ?? text.endIndex
                    if end <= text.endIndex, text.distance(from: start, to: end) == 4 {
                        let hex = String(text[start..<end])
                        if let value = UInt32(hex, radix: 16), let scalar = UnicodeScalar(value) {
                            output.unicodeScalars.append(scalar)
                            index = end
                            continue
                        }
                    }
                }
            }

            output.append(text[index])
            index = text.index(after: index)
        }

        return output
    }
}

struct NewsHTMLDetailView: View {
    @Environment(\.dismiss) private var dismiss
    let item: BootstrapViewModel.NewsRow

    private var normalizedImageURL: URL? {
        normalizedURL(from: item.imageURL)
    }

    private var normalizedShareURL: URL? {
        normalizedURL(from: item.shareURL)
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: { dismiss() }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundStyle(Color(androidGreen))
                        .frame(width: 44, height: 44)
                }

                Spacer()
                TNCompactLogo()
                Spacer()

                Text("FR")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(.secondary)
                    .frame(width: 44, height: 44)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(Color.white)

            Divider()

            ScrollView {
                VStack(alignment: .leading, spacing: 12) {
                    if let imageURL = normalizedImageURL {
                        AsyncImage(url: imageURL) { phase in
                            switch phase {
                            case .empty:
                                ZStack {
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color.gray.opacity(0.12))
                                    ProgressView()
                                }
                                .frame(height: 220)
                            case .success(let image):
                                image
                                    .resizable()
                                    .scaledToFill()
                                    .frame(height: 220)
                                    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                            case .failure(let error):
                                ZStack {
                                    RoundedRectangle(cornerRadius: 16)
                                        .fill(Color.gray.opacity(0.12))
                                    Image(systemName: "photo")
                                        .foregroundStyle(.gray)
                                }
                                .frame(height: 220)
                                .onAppear {
                                    print("[TN-iOS][DetailHTML] image load failed id=\(item.id) raw=\(item.imageURL) normalized=\(imageURL.absoluteString) error=\(error.localizedDescription)")
                                }
                            @unknown default:
                                EmptyView()
                            }
                        }
                    }

                    Text(item.title)
                        .font(.title3.bold())

                    if !item.date.isEmpty {
                        Text(item.date)
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }

                    if let shareURL = normalizedShareURL {
                        ShareLink("Partager", item: shareURL)
                    }

                    Divider()

                    HTMLContentView(html: item.contentHTML)
                }
                .padding(.horizontal)
                .padding(.vertical, 8)
            }
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
        .onAppear {
            print("[TN-iOS][DetailHTML] OPEN id=\(item.id) title=\(item.title)")
            print("[TN-iOS][DetailHTML] image raw=\(item.imageURL) normalized=\(normalizedImageURL?.absoluteString ?? "<invalid>")")
            print("[TN-iOS][DetailHTML] share raw=\(item.shareURL) normalized=\(normalizedShareURL?.absoluteString ?? "<invalid>")")
            print("[TN-iOS][DetailHTML] html length=\(item.contentHTML.count)")
        }
    }

    private func normalizedURL(from raw: String) -> URL? {
        let trimmed = raw.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return nil }
        if let direct = URL(string: trimmed), direct.scheme != nil {
            return direct
        }
        return URL(string: "https://\(trimmed)")
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
