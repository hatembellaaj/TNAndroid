import SwiftUI

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
                            VStack(alignment: .leading, spacing: 4) {
                                Text(item.title).font(.headline)
                                if !item.summary.isEmpty {
                                    Text(item.summary).font(.subheadline).foregroundStyle(.secondary)
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
    private let prayersURL = URL(string: "http://196.203.63.50/Isslamyat/web/json/priere.json")!

    func loadAll() async {
        await loadNews()
        await loadPrayers()
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
                let id = (row["id"] as? String) ??
                    (row["id_news"] as? String) ??
                    String(describing: row["id"] ?? idx)
                let title = (row["title"] as? String) ??
                    (row["titre"] as? String) ??
                    (row["titleNews"] as? String) ?? "Sans titre"
                let summary = (row["description"] as? String) ??
                    (row["resume"] as? String) ??
                    (row["summary"] as? String) ?? ""

                return NewsRow(id: id, title: title, summary: summary)
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

    func loadPrayers() async {
        isLoadingPrayers = true
        prayerError = nil
        print("[TN-iOS] Loading prayers from: \(prayersURL.absoluteString)")

        do {
            let (data, response) = try await URLSession.shared.data(from: prayersURL)
            if let http = response as? HTTPURLResponse {
                print("[TN-iOS] Prayers HTTP status: \(http.statusCode)")
            }

            let json = try JSONSerialization.jsonObject(with: data)
            let rows = flattenPrayerRows(json)
            prayers = rows
            print("[TN-iOS] Prayers loaded count: \(rows.count)")
        } catch {
            prayerError = "Erreur chargement prières: \(error.localizedDescription)"
            print("[TN-iOS] Prayers load error: \(error)")
        }

        isLoadingPrayers = false
    }

    private func flattenPrayerRows(_ payload: Any) -> [PrayerRow] {
        var out: [PrayerRow] = []

        if let dict = payload as? [String: Any] {
            // Common keys from prayer payloads
            let candidateKeys = ["data", "times", "prayer", "prayers", "horaire", "horaires"]
            for key in candidateKeys {
                if let arr = dict[key] as? [[String: Any]] {
                    out.append(contentsOf: parsePrayerArray(arr))
                }
            }

            // fallback: parse key/value hh:mm directly
            for (k, v) in dict {
                if let time = v as? String, time.contains(":") {
                    out.append(PrayerRow(name: k, time: time))
                }
            }
        } else if let arr = payload as? [[String: Any]] {
            out.append(contentsOf: parsePrayerArray(arr))
        }

        // Remove duplicates by (name,time)
        var seen = Set<String>()
        return out.filter {
            let key = "\($0.name)|\($0.time)"
            if seen.contains(key) { return false }
            seen.insert(key)
            return true
        }
    }

    private func parsePrayerArray(_ arr: [[String: Any]]) -> [PrayerRow] {
        arr.compactMap { row in
            let name = (row["name"] as? String) ??
                (row["prayer"] as? String) ??
                (row["nom"] as? String)
            let time = (row["time"] as? String) ??
                (row["heure"] as? String) ??
                (row["value"] as? String)

            guard let n = name, let t = time else { return nil }
            return PrayerRow(name: n, time: t)
        }
    }
}

#Preview {
    ContentView()
}
