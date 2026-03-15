# Spécification complète du projet `TNAndroid`

## 1) Présentation

`TNAndroid` est une application Android native (Java) orientée contenu média/news pour **Tunisie Numérique**. Le projet agrège des flux éditoriaux (actualités, dossiers, vidéos, blagues), propose une section de **prières**, gère les **favoris hors-ligne** et intègre des services tiers de **notifications push**, **analytics** et **publicité**.

Cette spécification est construite à partir d’un audit statique du dépôt.

---

## 2) Stack technique

- **Langage principal**: Java (Android).
- **Build system**: Gradle (Android Plugin 7.4.2).
- **SDK Android**:
  - `compileSdkVersion 31`
  - `targetSdkVersion 31`
  - `minSdkVersion 19`
- **Application ID**: `com.mdweb.tunnumerique`.
- **Version applicative**:
  - `versionCode 93`
  - `versionName 3.6`
- **MultiDex** activé.
- **Internationalisation** active pour `fr`, `en`, `ar`.

### Dépendances majeures

- UI/AndroidX: `appcompat`, `material`, `recyclerview`, `viewpager`, `webkit`.
- Réseau: `volley`.
- JSON: `gson`.
- Image: `glide`, `universal-image-loader`.
- Notifications: `OneSignal` + services Huawei Push.
- Analytics/Crash: Firebase Analytics + Crashlytics, Huawei Analytics.
- Worker: WorkManager (`DataWorker`).

---

## 3) Portée fonctionnelle

### 3.1 Fonctionnalités principales

1. **Accueil éditorial**
   - Flux d’actualités, contenus de type article/vidéo/publicité.
   - Dossiers et contenus populaires.

2. **Navigation thématique / catégories**
   - Parcours par catégories.
   - Filtrage/recherche d’articles.

3. **Détails de contenu**
   - Écrans dédiés article, vidéo, blague.
   - Partage de contenu.

4. **Favoris**
   - Sauvegarde locale en SQLite.
   - Distinction contenus articles / vidéos.

5. **Prières (Horaire Salat)**
   - Affichage des horaires.
   - Gestion pays/gouvernorat via données JSON distantes.

6. **Multilingue**
   - Français / Anglais / Arabe.
   - Endpoints différenciés selon langue pour certains flux.

7. **Notifications**
   - Push via OneSignal (principal) + Huawei HMS.
   - Extension de notification personnalisée.

8. **Publicité / campagnes**
   - Gestion de contenu publicitaire JSON.
   - Présence d’intégration Huawei Ads Lite.

9. **Guide / onboarding & réglages**
   - Écran de guide.
   - Paramètres utilisateur (notifications, langue, etc.).

### 3.2 Entrée utilisateur

- L’activité de démarrage est `SplashScreenActivity` (intent `MAIN/LAUNCHER`).
- Le splash enchaîne sur le chargement de données, choix de langue (si nécessaire), puis navigation vers le flux principal.

---

## 4) Architecture logicielle

Le code est organisé en couches pragmatiques:

- `ui/activitys`: activités Android (navigation et orchestration écran).
- `ui/fragments`: composants d’écrans fonctionnels (home, actualité, favoris, prières, etc.).
- `ui/adapters`: adapters RecyclerView/ViewPager pour affichage de listes/carrousels.
- `data/model`: modèles métiers (News, Categories, HorairePriere, etc.).
- `data/parsers`: parseurs de payloads JSON.
- `data/sqlite`: persistance locale des favoris.
- `services`: services Android (sync, push HMS, extension notif OneSignal).
- `tools/*`: utilitaires transverses (session, réseau, cache image, styles, helper views).

### 4.1 Composants applicatifs clés

- **Classe Application** (`tools.application.Application`)
  - Initialisation globale (cache image, OneSignal, analytics Huawei).
  - Gestion d’événements analytics custom.

- **SplashScreenActivity**
  - Initialisation de session.
  - Planification du `DataWorker` périodique (15 min, réseau requis).
  - Déclenchement des flux initiaux/pub.

- **MainActivity**
  - Point de navigation principal des fragments et sections.

- **ServiceSynchronisation / DataWorker**
  - Mécanismes de synchronisation/rafraîchissement périodique.

---

## 5) Spécification des données

### 5.1 Modèles observés

- `News`, `ListNews`, `Article`, `Blague`
- `Categories`, `HomeSection`, `HomeItem`, `SectionHome`
- `HorairePriere`, `HoraireSalat`, `DateSalat`, `Country`, `Gouvernorat`
- `SliderItem`, `SlideItem`, etc.

### 5.2 Persistance locale

Base SQLite `favorisDatabase`, table `news`.

Colonnes principales:
- Identifiants (`news_id`), type contenu (`news_type_news`, `news_type`), titre, description, contenu.
- Métadonnées media (image/audio/url détail/share).
- Langue (`news_lng`).
- Drapeau paywall (`news_is_paywall`, défaut 0).

Migration observée:
- upgrade `< v7`: ajout colonne `news_is_paywall`.

### 5.3 Préférences (SharedPreferences)

`SessionManager` centralise des clés de configuration utilisateur:
- langue courante,
- état notifications,
- survey/horodatages,
- métriques de lancement et autres drapeaux applicatifs.

---

## 6) Spécification des flux réseau

### 6.1 Endpoints structurants

Depuis `Communication`:

- News init:
  - FR: `https://preprod.tunisienumerique.com/results.json`
  - AR: `https://arabe.tunisienumerique.com/results.json`
  - EN: `https://news-tunisia.tunisienumerique.com/results.json`
- Refresh / pagination:
  - `https://jsondata.tunisienumerique.com/jsonfetch.php?type=1&id_article=`
  - `https://jsondata.tunisienumerique.com/jsonfetch.php?type=2&id_article=`
- Dossiers:
  - FR/AR/EN endpoints dédiés
- Populaires (`plus lus`), catégories, vidéos
- Prières/pays supportés
- Blagues
- Publicités JSON
- Upload image utilisateur
- Survey JSON

### 6.2 Comportement

- Les fichiers JSON sont aussi nommés localement (`init.json`, `popular.json`, etc.) pour cache/stockage.
- Sélection d’endpoint selon langue via `SessionManager` + constantes (`fr`, `en`, `ar`).
- Stack réseau appuyée sur `Volley` + helpers maison (`JsonRequestHelper`, `LocalFilesManager`).

---

## 7) Navigation, écrans et UX

### 7.1 Activités déclarées (manifest)

- `SplashScreenActivity` (launcher)
- `MainActivity`
- `DetailsArticleActivity`, `DetailsVideoActivity`, `DetailsBlagueActivity`
- `FilterArticleActivity`
- `CategoryNewsActivity`, `PlusLusActivity`
- `FavorisActivity`
- `GuideActivity`, `LangueActivity`, `EnqueteActivity`
- `NotificationActivity`
- autres activités historiques (`HomeTnActivity`, `ArticleDetailActivity`, `DetailArticleFragment` en activité)

### 7.2 Fragments principaux

- `HomeFragment`
- `ActualiteFragment`
- `FavoriFragment`, `ArticleFavoriFragment`, `VideoFavoriFragment`
- `HorairePriereFragment`, `HoraireFragment`
- `ParametreFragment`, `AproposFragment`
- `BlagueFragment`, `BlaguesDetailsFragment`, `ArticleDetailFragment`

### 7.3 Navigation bas écran

Menu bas observé:
- Home
- Horaires de prière
- Enregistrés
- Top24

---

## 8) Services, jobs et notifications

- **OneSignal**
  - Initialisation dans `Application`.
  - `NotificationOpenedHandler` custom.
  - Extension distante `NotificationExtenderAndroid` déclarée dans le manifest.

- **Huawei HMS**
  - `YourHmsMessageService` pour événements push Huawei.
  - Dépendances analytics et ads Huawei présentes.

- **WorkManager**
  - `DataWorker` périodique toutes les 15 minutes.
  - Contrainte réseau connecté.

- **ServiceSynchronisation**
  - Service job bindé (`BIND_JOB_SERVICE`).

---

## 9) Permissions et sécurité

Permissions déclarées (extraits):
- Internet, état réseau/wifi.
- Lecture/écriture stockage externe.
- Vibration.
- Permission legacy `MOUNT_UNMOUNT_FILESYSTEMS` (protégée, ignorée via tools).

Points notables:
- `usesCleartextTraffic="true"` activé.
- Présence d’URLs HTTP (non TLS) dans les endpoints prières/support pays.
- `allowBackup="false"`.

---

## 10) Ressources et internationalisation

- Ressources de chaînes:
  - `values/strings.xml` (EN majoritairement)
  - `values-fr/strings.xml`
  - `values-ar/strings.xml`
- Layouts dédiés arabe: `res/layout-ar/*`.
- Police arabe embarquée + assets media (gif, audio, json de seed pour home/slider).

---

## 11) Build, release et exploitation

### 11.1 Configuration build

- Plugins: Android app, OneSignal Gradle plugin, Google services, Firebase Crashlytics.
- Build types: `release` + `Build` (personnalisé), tous deux minifiés.
- Splits bundle:
  - langue: désactivé
  - densité/ABI: activés

### 11.2 Artefacts

- APK release présent dans le dépôt: `app/release/app-release.apk`.

### 11.3 Fichiers de services cloud

- `google-services.json` (Firebase/Google)
- `agconnect-services.json` (Huawei)

---

## 12) Qualité et tests

- Tests unitaires et instrumentés minimaux présents (`ExampleUnitTest`, `UtilsTest`).
- Pas de suite exhaustive observable dans le dépôt.

---

## 13) Contraintes & dette technique identifiables

1. **Mix AndroidX + anciennes libs support** (risque de compatibilité et duplication).
2. **Endpoints hétérogènes** (preprod/prod/http/https).
3. **Permissions legacy stockage** pouvant être problématiques sur versions Android récentes.
4. **Présence de secrets potentiels** (clés/API dans ressources) à auditer.
5. **Activités/fragments au naming ambigu** (`DetailArticleFragment` déclaré en activité).

---

## 14) Spécification opérationnelle (résumé exécutable)

Le système doit:

1. Démarrer sur splash, initialiser session/cache/push/analytics.
2. Charger les contenus éditoriaux selon langue.
3. Permettre navigation home/catégories/recherche/détails.
4. Afficher vidéos et blagues dédiées.
5. Gérer favoris localement (ajout, retrait, listing).
6. Afficher horaires de prière (pays/gouvernorat).
7. Recevoir et traiter notifications OneSignal/Huawei.
8. Exécuter un rafraîchissement périodique des données en arrière-plan.
9. Fonctionner en FR/EN/AR avec ressources localisées.

---

## 15) Recommandations de spécification future (si besoin produit)

Pour une version “spécification produit/technique contractuelle”, compléter:

- Matrice détaillée des écrans (wireframe + règles métier).
- Contrat JSON formel de chaque endpoint (schéma, versionning).
- Règles précises de cache/offline et politique d’expiration.
- Politique RGPD / consentement tracking / confidentialité.
- Matrice de compatibilité appareils/OS.
- SLO performance (temps splash, temps chargement, taux crash cible).
