# Swift equivalent (TNAndroid -> iOS)

Ce dossier contient une version SwiftUI modulaire de l'application TNAndroid.

## Emplacement recommandé pour Xcode
- `swift/TNNewsApp/TNNewsApp/`

## Démarrage garanti (anti erreurs de scope)
- `TNNewsAppApp.swift` est volontairement minimal et lance `ContentView`.
- `ContentView.swift` est autonome (tabs de base), pour garantir un run même si le target membership Xcode n'inclut pas encore tous les fichiers.

## Activer la version complète
Ajoute ensuite au target app:
- `Core/`
- `Features/`
- `Services/`

puis remplace le `ContentView` autonome par le flux complet (`SplashView` / `RootTabView`) lorsque le target membership est propre.

## APIs Android
Les endpoints Android restent définis dans `Core/Networking/Endpoint.swift` pour l'alignement fonctionnel de la migration.

## Intégration Xcode
- Conserver un seul fichier `@main`.
- Vérifier le Target Membership de tous les dossiers.
- Supprimer/retirer du target tout `ContentView` template affichant "Hello, world!".
