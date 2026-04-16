import Foundation

enum APIError: Error, LocalizedError {
    case invalidURL
    case transport(Error)
    case server(Int)
    case decoding(Error)
    case empty

    var errorDescription: String? {
        switch self {
        case .invalidURL: return "URL invalide"
        case .transport(let err): return "Erreur réseau: \(err.localizedDescription)"
        case .server(let code): return "Erreur serveur (\(code))"
        case .decoding(let err): return "Erreur de décodage: \(err.localizedDescription)"
        case .empty: return "Réponse vide"
        }
    }
}
