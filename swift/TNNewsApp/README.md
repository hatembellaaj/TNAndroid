# Swift equivalent (TNAndroid -> iOS)

Ce dossier contient une version SwiftUI modulaire de l'application TNAndroid.

## Architecture
- `Core/` : modèles, réseau, settings, stockage.
- `Features/` : écrans + view models par domaine fonctionnel.
- `Services/` : notifications, synchronisation arrière-plan.
- `TNNewsApp.swift` : point d'entrée SwiftUI.

## Fonctionnalités couvertes
- Splash + bootstrap
- Home / Actualités
- Détail article
- Favoris persistés localement (`UserDefaults` JSON)
- Prières (chargement endpoint)
- Top24
- Paramètres (langue, notifications)
- Routage deep link notifications

## Notes
- Le code est prêt à être importé dans un projet Xcode iOS 16+.
- Pour une production, remplacer `UserDefaults` par Core Data/SQLite et brancher APNs/OneSignal réel.
