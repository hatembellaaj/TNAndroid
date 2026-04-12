# Swift equivalent (TNAndroid -> iOS)

Ce dossier contient une version SwiftUI modulaire de l'application TNAndroid.

## Emplacement recommandé pour Xcode
- `swift/TNNewsApp/TNNewsApp/`

## Démarrage garanti + chargement réel
- `TNNewsAppApp.swift` lance `ContentView`.
- `ContentView.swift` charge réellement les données:
  - News: `https://preprod.tunisienumerique.com/results.json`
  - Prières: flux temporairement désactivé (endpoint actuel en HTTP)
- Logs visibles dans la console Xcode avec préfixe `[TN-iOS]` (URL, status HTTP, count, erreurs).
- L'onglet prières affiche un message informatif tant que l'API n'est pas migrée en HTTPS.

## API prières (temporairement ignorée)
Le flux prières Android est encore en `http://`.
Décision actuelle: **ne pas charger cette API pour le moment** et conserver ATS strict côté iOS.
Dès que l'endpoint passe en HTTPS, réactiver l'appel dans la couche repository / view model.

## Activer ensuite la version modulaire complète
Ajoute ensuite au target app:
- `Core/`
- `Features/`
- `Services/`

puis rebascule vers le flux complet (`SplashView`/`RootTabView`) quand le target membership est propre.

## APIs Android
Les endpoints Android restent définis dans `Core/Networking/Endpoint.swift` pour l'alignement de migration.

## Icône iOS depuis Android (sans fichier binaire dans Git)
Pour éviter les PR bloquées par les fichiers binaires, l'asset catalog iOS est versionné **sans PNG**.

Sur macOS, génère les icônes au style launcher TN (carré vert arrondi + lettres TN):

```bash
bash swift/TNNewsApp/scripts/generate_app_icon_from_android.sh
```

Le script génère des PNG non transparents au style TN, puis remplit:
- `swift/TNNewsApp/TNNewsApp/Assets.xcassets/AppIcon.appiconset/`

Ensuite dans Xcode, vérifie:
- Target `TNNewsApp` -> `General` -> `App Icons and Launch Screen` -> `App Icon = AppIcon`

## Intégration Xcode
- Conserver un seul fichier `@main`.
- Vérifier le Target Membership de tous les dossiers.
- Supprimer/retirer du target tout `ContentView` template affichant "Hello, world!".
