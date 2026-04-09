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
- Si ATS bloque les prières (`-1022`), l'app affiche une liste fallback locale pour garder l'écran utilisable.

## ATS (prières en HTTP)
Le flux prières Android utilise une URL `http://`.
Sur iOS, ATS bloque ce chargement par défaut.

Ajoute ces clés dans le `Info.plist` du target iOS:

- `NSAppTransportSecurity` (Dictionary)
  - `NSAllowsArbitraryLoads` (Boolean) = `YES`

Ou copie le snippet depuis:
- `swift/TNNewsApp/TNNewsApp/Info.plist.ats-snippet.xml`

### Vérification rapide
Après lancement, vérifie dans la console Xcode:
- `[TN-iOS] ATS config detected - NSAllowsArbitraryLoads=true`

Si `false`, l'exception n'est pas appliquée au bon `Info.plist` target (vérifie `Build Settings` -> `Info.plist File`).

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
