import Foundation

enum Endpoint {
    case newsInit(AppLanguage)
    case refresh(idArticle: String)
    case scrollDown(idArticle: String)
    case dossiers(AppLanguage)
    case plusLus(AppLanguage)
    case categories(AppLanguage)
    case videos
    case blagues
    case prayers
    case supportedCountries
    case survey

    var urlString: String {
        switch self {
        case .newsInit(.fr): return "https://preprod.tunisienumerique.com/results.json"
        case .newsInit(.ar): return "https://arabe.tunisienumerique.com/results.json"
        case .newsInit(.en): return "https://news-tunisia.tunisienumerique.com/results.json"

        case .refresh(let idArticle):
            return "https://jsondata.tunisienumerique.com/jsonfetch.php?type=1&id_article=\(idArticle)"

        case .scrollDown(let idArticle):
            return "https://jsondata.tunisienumerique.com/jsonfetch.php?type=2&id_article=\(idArticle)"

        case .dossiers(.fr): return "https://jsondata.tunisienumerique.com/dossiers.json"
        case .dossiers(.ar): return "https://arabe.tunisienumerique.com/dossiers.json"
        case .dossiers(.en): return "https://news-tunisia.tunisienumerique.com/jsondata/dossiers.json"

        case .plusLus(.fr): return "https://www.tunisienumerique.com/jsondata/popular.json"
        case .plusLus(.ar): return "https://arabe.tunisienumerique.com/jsondata/popular.json"
        case .plusLus(.en): return "https://news-tunisia.tunisienumerique.com/jsondata/popular.json"

        case .categories(.fr): return "https://preprod.tunisienumerique.com/jsondata/categories.json"
        case .categories(.ar): return "https://news-tunisia.tunisienumerique.com/jsondata/categories.json"
        case .categories(.en): return "https://news-tunisia.tunisienumerique.com/jsondata/categories.json"

        case .videos: return "https://preprod.tunisienumerique.com/jsondata/videotunisienumerique"
        case .blagues: return "https://humour.tunisienumerique.com/hummor.json"
        case .prayers: return "http://196.203.63.50/Isslamyat/web/json/priere.json"
        case .supportedCountries: return "http://mdweb-int.com/appTN/prieretn/country.json"
        case .survey: return "https://mdweb-int.com/appTN/npsTN.json"
        }
    }

    var url: URL? { URL(string: urlString) }
}
