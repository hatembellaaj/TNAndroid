import Foundation

enum NotificationDestination {
    case article(id: String)
    case externalURL(URL)
    case home
}

final class NotificationRouter {
    func destination(from userInfo: [AnyHashable: Any]) -> NotificationDestination {
        if let articleID = userInfo["article_id"] as? String {
            return .article(id: articleID)
        }
        if let urlString = userInfo["openURL"] as? String,
           let url = URL(string: urlString) {
            return .externalURL(url)
        }
        return .home
    }
}
