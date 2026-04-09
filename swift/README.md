# Swift equivalent (TNAndroid -> iOS)

Ce dossier contient une version SwiftUI modulaire de l'application TNAndroid.

## Emplacement recommandé pour Xcode
- `swift/TNNewsApp/TNNewsApp/`

## Démarrage garanti + chargement réel
- `TNNewsAppApp.swift` lance `ContentView`.
- `ContentView.swift` charge réellement les données:
  - News: `https://preprod.tunisienumerique.com/results.json`
  - Prières: `http://196.203.63.50/Isslamyat/web/json/priere.json`
- Logs visibles dans la console Xcode avec préfixe `[TN-iOS]` (URL, status HTTP, count, erreurs).

## Activer ensuite la version modulaire complète
Ajoute ensuite au target app:
- `Core/`
- `Features/`
- `Services/`

puis rebascule vers le flux complet (`SplashView`/`RootTabView`) quand le target membership est propre.

## APIs Android
Les endpoints Android restent définis dans `Core/Networking/Endpoint.swift` pour l'alignement de migration.

## Intégration Xcode
- Conserver un seul fichier `@main`.
- Vérifier le Target Membership de tous les dossiers.
- Supprimer/retirer du target tout `ContentView` template affichant "Hello, world!".
