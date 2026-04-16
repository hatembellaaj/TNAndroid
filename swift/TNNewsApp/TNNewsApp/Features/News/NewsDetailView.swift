import SwiftUI

struct NewsDetailView: View {
    @EnvironmentObject private var env: AppEnvironment
    let item: NewsItem

    private var detailImageURL: URL? {
        normalizeURL(item.imageURL)
    }

    private var detailShareURL: URL? {
        normalizeURL(item.shareURL)
    }

    private var parsedHTMLContent: AttributedString {
        parseHTML(item.content)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                if let imageURL = detailImageURL {
                    AsyncImage(url: imageURL) { phase in
                        switch phase {
                        case .empty:
                            ZStack {
                                RoundedRectangle(cornerRadius: 16)
                                    .fill(Color.gray.opacity(0.1))
                                ProgressView()
                            }
                            .frame(height: 220)
                        case .success(let image):
                            image
                                .resizable()
                                .scaledToFill()
                                .frame(height: 220)
                                .clipShape(RoundedRectangle(cornerRadius: 16))
                        case .failure(let error):
                            ZStack {
                                RoundedRectangle(cornerRadius: 16)
                                    .fill(Color.gray.opacity(0.1))
                                Image(systemName: "photo")
                                    .foregroundColor(.gray)
                            }
                            .frame(height: 220)
                            .onAppear {
                                print("[TN-iOS][Detail] Image load failed for id=\(item.id) url=\(imageURL.absoluteString) error=\(error.localizedDescription)")
                            }
                        @unknown default:
                            EmptyView()
                        }
                    }
                }

                if !item.type.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                    Text(item.type.uppercased())
                        .font(.caption.bold())
                        .foregroundStyle(Color.green)
                }

                Text(item.title)
                    .font(.title2.bold())

                if let date = item.date, !date.isEmpty {
                    Text(date)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }

                HStack(spacing: 16) {
                    Button(env.favoritesStore.contains(newsID: item.id) ? "Retirer des favoris" : "Ajouter aux favoris") {
                        if env.favoritesStore.contains(newsID: item.id) {
                            env.favoritesStore.remove(newsID: item.id)
                        } else {
                            env.favoritesStore.add(item)
                        }
                    }
                    .buttonStyle(.borderedProminent)

                    if let shareURL = detailShareURL {
                        ShareLink("Partager", item: shareURL)
                    } else {
                        Text("Partager indisponible")
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }
                }

                Divider()

                Text(parsedHTMLContent)
                    .font(.body)
                    .textSelection(.enabled)
            }
            .padding()
        }
        .navigationTitle("Détail")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            logDetailDiagnostics()
        }
    }

    private func normalizeURL(_ raw: String?) -> URL? {
        guard let raw = raw?.trimmingCharacters(in: .whitespacesAndNewlines), !raw.isEmpty else { return nil }
        if let direct = URL(string: raw), direct.scheme != nil { return direct }
        return URL(string: "https://\(raw)")
    }

    private func parseHTML(_ html: String) -> AttributedString {
        guard !html.isEmpty else { return AttributedString("Contenu indisponible") }
        guard let data = html.data(using: .utf8) else { return AttributedString(html) }

        do {
            let nsAttributed = try NSAttributedString(
                data: data,
                options: [
                    .documentType: NSAttributedString.DocumentType.html,
                    .characterEncoding: String.Encoding.utf8.rawValue
                ],
                documentAttributes: nil
            )
            return AttributedString(nsAttributed)
        } catch {
            print("[TN-iOS][Detail] HTML parse failed for id=\(item.id): \(error.localizedDescription)")
            return AttributedString(html)
        }
    }

    private func logDetailDiagnostics() {
        let imageRaw = item.imageURL ?? "<nil>"
        let shareRaw = item.shareURL ?? "<nil>"
        let normalizedImage = detailImageURL?.absoluteString ?? "<invalid>"
        let normalizedShare = detailShareURL?.absoluteString ?? "<invalid>"
        let containsHTML = item.content.contains("<") && item.content.contains(">")

        print("[TN-iOS][Detail] OPEN id=\(item.id) title=\(item.title)")
        print("[TN-iOS][Detail] type=\(item.type) date=\(item.date ?? "<nil>") paywall=\(item.isPaywall)")
        print("[TN-iOS][Detail] image raw=\(imageRaw) normalized=\(normalizedImage)")
        print("[TN-iOS][Detail] share raw=\(shareRaw) normalized=\(normalizedShare)")
        print("[TN-iOS][Detail] content length=\(item.content.count) looksHTML=\(containsHTML)")
    }
}
