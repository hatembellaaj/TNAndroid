# Analyse complète TNAndroid pour générer une application Swift équivalente

## 1. Résumé exécutif

`TNAndroid` est une application média/news multi-langue (FR/EN/AR) qui combine :
- un flux éditorial principal (actualités, vidéos, blagues, dossiers, plus lus),
- un module horaires de prières,
- des favoris persistés localement,
- des notifications push,
- des campagnes publicitaires,
- et un chargement/cache JSON local orienté “offline-first léger”.

L’équivalent iOS recommandé est une application **Swift + SwiftUI** avec une architecture **modulaire** (Domain/Data/Presentation), un noyau réseau `URLSession`, une persistance locale `Core Data` (ou SQLite GRDB), et des services système iOS (UNUserNotificationCenter, BGTaskScheduler, etc.).

---

## 2. Cartographie fonctionnelle Android → iOS

## 2.1 Parcours utilisateur

1. **Splash** (`SplashScreenActivity`) :
   - initialise les préférences de première ouverture,
   - planifie la synchronisation périodique (`WorkManager` + `DataWorker` toutes les 15 min, réseau requis),
   - déclenche le préchargement data/publicité,
   - force le passage par le choix de langue au premier lancement.

2. **Main shell** (`MainActivity`) :
   - héberge la navigation principale,
   - ouvre différents fragments : home, actualités, blagues, favoris, horaires, paramètres, à propos,
   - gère aussi l’ouverture depuis une URL de notification.

3. **Détails contenus** :
   - activité dédiée article/vidéo/blague.

4. **Favoris** :
   - lecture/écriture locale SQLite (`FavorisDataBase`).

## 2.2 Équivalence iOS recommandée

- `SplashScreenActivity` → `Launch/Splash Coordinator` (SwiftUI `@main` + écran de boot logique, pas de blocage visuel long).
- `MainActivity + Fragments` → `TabView + NavigationStack` (ou Coordinator UIKit si besoin legacy).
- `SharedPreferences(SessionManager)` → `UserDefaults` encapsulé dans `AppSettingsStore`.
- `SQLiteOpenHelper(FavorisDataBase)` → `Core Data` (ou GRDB pour mapping SQL proche de l’existant).
- `WorkManager(DataWorker)` → `BGAppRefreshTask` / `BGProcessingTask` + refresh opportuniste foreground.
- `OneSignal/HMS` → APNs natif + fournisseur push unique (OneSignal iOS si continuité produit).

---

## 3. Inventaire technique du projet Android

## 3.1 Configuration build

- Langage : Java Android.
- SDK : min 19, target 31, compile 31.
- Version app : `versionCode 93`, `versionName 3.6`.
- MultiDex activé.
- Langues packagées : en/fr/ar.

## 3.2 Dépendances structurantes

- Réseau : Volley.
- Parsing JSON : Gson.
- Images : Glide + Universal Image Loader.
- Notifications : OneSignal + HMS Push.
- Analytics/Crash : Firebase + Huawei Analytics.
- Jobs : WorkManager.

## 3.3 Manifest / permissions importantes

- INTERNET, ACCESS_NETWORK_STATE, WIFI, VIBRATE.
- Permissions stockage (`READ/WRITE_EXTERNAL_STORAGE`) héritées Android legacy.
- `usesCleartextTraffic=true` (à traiter avec vigilance sécurité en iOS).
- Entrée app : `SplashScreenActivity` (`MAIN/LAUNCHER`).

---

## 4. Analyse des domaines fonctionnels et contrat iOS

## 4.1 News/Contenu éditorial

### Android observé
- Endpoints multi-langues dans `Communication` (`results.json`, refresh, scroll, dossiers, catégories, plus lus, vidéo, blagues).
- Fichiers JSON mis en cache localement (`init.json`, `popular.json`, etc.).
- Parsing via `DataParser` et modèles (`News`, `ListNews`, `SectionHome`, etc.).

### Contrat Swift proposé
- `ContentAPIClient` (async/await) + `Endpoint` typé.
- `Codable` pour DTO (`NewsDTO`, `CategoryDTO`, etc.).
- `Repository` avec stratégie cache:
  - mémoire (NSCache),
  - disque (fichiers JSON versionnés),
  - base locale (Core Data) pour favoris + snapshots utiles.
- Politique de fraîcheur (`stale-while-revalidate`): afficher cache puis refresh silencieux.

## 4.2 Favoris

### Android observé
- DB SQLite `favorisDatabase`, table `news`, version 7.
- colonnes riches (titre, description, image, audio, langue, paywall).

### Contrat Swift proposé
- Entité `FavoriteNews` (Core Data) alignée sur le schéma Android.
- Index unique logique : `(newsId, typeNews)`.
- Service `FavoritesStore`:
  - `add(news)`
  - `remove(newsId:type)`
  - `isFavorite(newsId:type)`
  - `list(language)`

## 4.3 Prières

### Android observé
- Endpoints dédiés prières/pays/gouvernorats.
- Préférences : `countryId`, `gouvernorateId` dans SessionManager.

### Contrat Swift proposé
- Module `PrayerTimes` séparé du flux news.
- Store local pour sélection région.
- Écran SwiftUI avec picker pays/région + calcul prochain horaire.

## 4.4 Notifications

### Android observé
- OneSignal + extension d’ouverture/payload.
- HMS pour devices Huawei.
- ouverture potentielle d’URL deep link depuis notif.

### Contrat Swift proposé
- APNs + OneSignal iOS SDK (si conservation fournisseur).
- `NotificationRouter` iOS qui mappe payload → destination (`article`, `video`, `externalURL`).
- permission prompts progressifs (soft ask puis system prompt).

## 4.5 Synchronisation background

### Android observé
- `DataWorker` appelle plusieurs endpoints et émet un broadcast refresh UI.

### Contrat Swift proposé
- `SyncOrchestrator` :
  - refresh foreground au lancement/app active,
  - tâches BG opportunistes (iOS impose fréquence non garantie),
  - instrumentation du dernier refresh réussi.

---

## 5. Architecture cible Swift (recommandation de référence)

## 5.1 Organisation modules

- `AppCore` : DI, configuration, logs, feature flags.
- `Networking` : client HTTP, endpoints, retry, reachability.
- `Data` : repositories + persistance.
- `Domain` : use-cases métiers.
- `Features/*` : Home, NewsDetail, Videos, Blagues, Favorites, PrayerTimes, Settings.
- `DesignSystem` : composants UI réutilisables.

## 5.2 Patterns

- MVVM + UseCases.
- Navigation par coordinators légers (ou Router SwiftUI).
- Concurrency moderne (`async/await`, `Task`, `@MainActor`).

## 5.3 Injection de dépendances

- Protocole-first (`NewsRepositoryProtocol`, `FavoritesStoreProtocol`).
- Container simple maison ou Factory.

---

## 6. Mapping UI Android → SwiftUI

- `Activity + Fragment` imbriqués → `TabView` + sous-`NavigationStack` par onglet.
- `RecyclerView Adapter` → `List` / `LazyVStack` + `ForEach`.
- `ViewPager/Slider` → `TabView(style: .page)`.
- Drawer latéral existant →
  - soit menu profil/settings intégré par onglet,
  - soit side menu custom (à limiter pour cohérence iOS).

---

## 7. Mapping des données et modèles

## 7.1 DTO réseau

Créer des structs Swift `Codable` proches des JSON existants :
- `NewsDTO`, `CategoryDTO`, `DossierDTO`, `VideoDTO`, `BlagueDTO`, `PrayerDTO`, `CountryDTO`, `SurveyDTO`, `AdDTO`.

## 7.2 Modèles domaine

- `NewsItem` (id, type, title, summary, content, image, media, language, paywall, shareURL).
- `HomeSection` (id, title, items).
- `PrayerSchedule`.

## 7.3 Persistance

- Core Data entities : `CDNewsFavorite`, `CDSettingsSnapshot` (si besoin), `CDCachedSection` (optionnel).

---

## 8. Sécurité, conformité et risques migration

1. **HTTP non chiffré** observé sur certains endpoints (prières) :
   - imposer HTTPS ou ajouter exception ATS minimale temporaire.
2. **URLs hardcodées** :
   - basculer vers `xcconfig` + environnement (`dev/preprod/prod`).
3. **SDKs doublons analytics/pub** :
   - décider un set minimal iOS pour réduire poids et dette.
4. **Parité comportementale background** :
   - iOS ne garantit pas un refresh périodique strict (adapter le produit).

---

## 9. Plan de migration par lots (pragmatique)

## Lot 0 — Fondation (1-2 semaines)
- Setup projet iOS, architecture, design system minimal, networking de base.

## Lot 1 — Lecture contenu (2-3 semaines)
- Home + listing actualités + détail article + multi-langue.

## Lot 2 — Modules secondaires (2 semaines)
- Vidéos, blagues, plus lus, catégories.

## Lot 3 — Favoris (1 semaine)
- Persistance Core Data + synchro UI.

## Lot 4 — Prières (1-2 semaines)
- Pays/gouvernorat + affichage horaires.

## Lot 5 — Notifications/analytics/pub (1-2 semaines)
- APNs/OneSignal, deep links, instrumentation.

## Lot 6 — Stabilisation (1-2 semaines)
- QA, offline, perf, crash-free, localisation RTL arabe.

---

## 10. Backlog technique iOS initial (prêt à implémenter)

- [ ] Définir `Endpoint` enum avec toutes routes Android existantes.
- [ ] Implémenter `HTTPClient` + stratégie retry/réseau indisponible.
- [ ] Créer DTO `Codable` + mapper DTO→Domain.
- [ ] Créer `HomeRepository`, `NewsRepository`, `FavoritesRepository`, `PrayerRepository`.
- [ ] Écrire tests unitaires parsing JSON (fixtures).
- [ ] Écrire tests d’intégration repositories (cache + réseau).
- [ ] Brancher `TabView` (Home, Prières, Favoris, Top24, Paramètres).
- [ ] Ajouter deep links notifications vers détail article/vidéo/webview.

---

## 11. Critères d’acceptation de parité (Android vs iOS)

1. Même structure de navigation de premier niveau.
2. Même endpoints et filtres langue.
3. Même comportement de favoris (ajout/retrait/persistance).
4. Ouverture notification vers bon écran/URL.
5. Disponibilité offline minimale (derniers contenus/favoris).
6. Support FR/EN/AR + interface arabe lisible (RTL).

---

## 12. Recommandations finales

- **Ne pas** porter le code Java 1:1 : refaire un design natif iOS moderne.
- Stabiliser d’abord le **contrat data** (endpoints + schémas JSON) avant l’UI.
- Prioriser une parité fonctionnelle MVP, puis optimiser pub/analytics.
- Ajouter un document de mapping payload notifications (clé par clé) dès le début pour éviter les écarts prod.

