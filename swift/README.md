# Swift equivalent (TNAndroid -> iOS)

Ce dossier contient une version SwiftUI modulaire de l'application TNAndroid.

## Emplacement recommandé pour Xcode
Pour éviter les erreurs de scope/type membership (ex: `Cannot find 'AppSettingsStore' in scope`), les sources Swift sont placées sous le chemin suivant, compatible avec un projet iOS template:

- `swift/TNNewsApp/TNNewsApp/`

Contenu principal:
- `Core/` : modèles, réseau, settings, stockage.
- `Features/` : écrans + view models par domaine fonctionnel.
- `Services/` : notifications, synchronisation arrière-plan.
- `TNNewsAppApp.swift` : point d'entrée SwiftUI.
- `TNNewsAppApp.swift` contient aussi `AppSettingsStore` et `AppEnvironment` pour garantir leur disponibilité dans le target principal.
- `ContentView.swift` : entry view (redirigé vers `SplashView`).

## Fonctionnalités couvertes
- Splash + bootstrap
- Home / Actualités
- Détail article
- Favoris persistés localement (`UserDefaults` JSON)
- Prières (chargement endpoint)
- Top24
- Paramètres (langue, notifications)
- Routage deep link notifications

## Intégration Xcode (anti Hello World)
- Conserver un seul fichier `@main` (`TNNewsAppApp.swift`).
- Supprimer/retirer du target tout `ContentView` template affichant "Hello, world!".
- Vérifier le Target Membership de `Core`, `Features`, `Services` dans le target app.
