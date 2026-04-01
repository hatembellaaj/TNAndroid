import Foundation

protocol HTTPClient {
    func fetch<T: Decodable>(_ type: T.Type, endpoint: Endpoint) async throws -> T
}

struct URLSessionHTTPClient: HTTPClient {
    func fetch<T: Decodable>(_ type: T.Type, endpoint: Endpoint) async throws -> T {
        guard let url = endpoint.url else { throw APIError.invalidURL }
        let (data, response): (Data, URLResponse)
        do {
            (data, response) = try await URLSession.shared.data(from: url)
        } catch {
            throw APIError.transport(error)
        }

        guard let http = response as? HTTPURLResponse else { throw APIError.empty }
        guard (200...299).contains(http.statusCode) else { throw APIError.server(http.statusCode) }

        do {
            return try JSONDecoder().decode(T.self, from: data)
        } catch {
            throw APIError.decoding(error)
        }
    }
}
