# Swift equivalent (TNAndroid -> iOS)

Ce dossier contient une version SwiftUI modulaire de l'application TNAndroid.

## Emplacement recommandé pour Xcode
- `swift/TNNewsApp/TNNewsApp/`

## Contenu principal
- `Core/` : modèles, réseau, settings, stockage.
- `Features/` : écrans + view models par domaine fonctionnel.
- `Services/` : notifications, synchronisation arrière-plan.
- `TNNewsAppApp.swift` : point d'entrée SwiftUI (injecte `AppSettingsStore` + `AppEnvironment`).
- `ContentView.swift` : racine UI (`RootTabView`).

## APIs alignées Android
Les endpoints utilisés dans `Endpoint.swift` reprennent les URLs Android (results, refresh/scroll, dossiers, plus lus, catégories, vidéos, blagues, prières, pays, survey).

## Intégration Xcode
- Conserver un seul fichier `@main` (`TNNewsAppApp.swift`).
- Vérifier le Target Membership de `Core`, `Features`, `Services` dans le target app.
- Supprimer/retirer du target tout `ContentView` template affichant "Hello, world!".
