package com.mdweb.tunnumerique.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.ui.adapters.NewsListAdapter;
import com.mdweb.tunnumerique.ui.adapters.NavigationMenuAdapter;
import com.mdweb.tunnumerique.ui.adapters.DossierSliderAdapter;
import com.mdweb.tunnumerique.ui.fragments.AproposFragment;
import com.mdweb.tunnumerique.ui.fragments.ParametreFragment;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;

import java.util.ArrayList;
import java.util.List;

public class HomeTnActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    // Vues du header
    private ImageView menuIcon;
    private ImageView logoTn;
    private TextView languageButton;

    // Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView menuRecyclerView;
    private NavigationMenuAdapter menuAdapter;
    private ImageView closeDrawer;
    private View drawerOverlay;

    // Vues existantes
    private RecyclerView newsRecyclerView;
    private ProgressBar progressBar;

    // Container pour les fragments
    private View fragmentContainer;

    // ViewPager2 pour "À LA UNE"
    private ViewPager2 dossierViewPager;
    // Dots indicator
    private LinearLayout dotsIndicator;
    private ImageView[] dots;

    private DossierSliderAdapter dossierAdapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private int currentDossierPosition = 0;

    private NewsListAdapter newsAdapter;
    private RequestQueue requestQueue;
    private List<News> allNewsList;
    private List<News> dossiersList;
    private List<NavigationMenuAdapter.MenuItem> menuItemsList;
    private BottomNavigationView bottomNavigationView;

    // État de l'affichage (RecyclerView ou Fragment)
    private boolean isShowingFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hometn_activity);

        // Initialisation des vues
        initViews();

        // Configurer le header
        setupHeader();

        // Configurer le drawer
        setupDrawer();

        // Initialiser Volley
        requestQueue = Volley.newRequestQueue(this);

        // Configuration du RecyclerView
        setupRecyclerView();

        // Charger d'abord les articles locaux
        loadLocalArticles();

        // Charger les dossiers pour "À LA UNE"
        loadDossiers();

        // Configurer le Bottom Navigation
        setupBottomNavigation();
    }

    /**
     * Initialise toutes les vues
     */
    private void initViews() {
        // Header
        menuIcon = findViewById(R.id.menu_icon);
        logoTn = findViewById(R.id.headerLogo);
        languageButton = findViewById(R.id.language_button);

        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        drawerOverlay = findViewById(R.id.drawerOverlay);

        // Existant
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
      //  progressBar = findViewById(R.id.progressBar);

        // Fragment container
        fragmentContainer = findViewById(R.id.fragment_container);

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // ViewPager2 pour "À LA UNE"
        dossierViewPager = findViewById(R.id.dossierViewPager);
    }

    /**
     * Configure le Bottom Navigation
     */
    private void setupBottomNavigation() {
        // Définir Home comme sélectionné par défaut
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        // ✅ Icône verte pour selected, gris sinon
        ColorStateList iconColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},   // sélectionné
                        new int[]{}                                  // par défaut
                },
                new int[]{
                        Color.parseColor("#88BD2E"),  // vert pour icône sélectionnée
                        Color.parseColor("#AAAAAA")   // gris sinon
                }
        );

        // ✅ Texte blanc pour selected, gris sinon
        ColorStateList textColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},   // sélectionné
                        new int[]{}                                  // par défaut
                },
                new int[]{
                        Color.WHITE,                  // blanc pour texte sélectionné
                        Color.parseColor("#AAAAAA")   // gris sinon
                }
        );

        bottomNavigationView.setItemIconTintList(iconColors);
        bottomNavigationView.setItemTextColor(textColors);
        // Gérer les clics sur les items du menu
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Retour au RecyclerView si on était sur un fragment
                if (isShowingFragment) {
                    showRecyclerView();
                }
                return true;

            } else if (itemId == R.id.nav_horaires) {
                // Rediriger vers MainActivity avec le fragment Horaires de Prière
                Intent intent = new Intent(HomeTnActivity.this, MainActivity.class);
                intent.putExtra("FRAGMENT_TO_LOAD", "horaires");
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_enregistres) {
                // Rediriger vers MainActivity avec le fragment Favoris
                Intent intent = new Intent(HomeTnActivity.this, FavorisActivity.class);
                startActivity(intent);
                return true;


            } else if (itemId == R.id.nav_top24) {
                // Rediriger vers MainActivity avec le fragment Plus Lus (Top24)
                Intent intent = new Intent(HomeTnActivity.this, PlusLusActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    /**
     * Configure le drawer avec le menu dynamique
     */
    private void setupDrawer() {

        // 🔥 FIX OVERLAY
        drawerOverlay.setClickable(false);
        drawerOverlay.setFocusable(false);

        // 🔥 FIX NAVIGATIONVIEW (IMPORTANT)
        navigationView.setClickable(false);
        navigationView.setFocusable(false);
        navigationView.setFocusableInTouchMode(false);

        View headerView = navigationView.getHeaderView(0);

        closeDrawer = headerView.findViewById(R.id.closeDrawer);
        menuRecyclerView = headerView.findViewById(R.id.menuRecyclerView);

        closeDrawer.setOnClickListener(v ->
                drawerLayout.closeDrawer(GravityCompat.START)
        );

        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔥 IMPORTANT
        menuRecyclerView.setClickable(true);
        menuRecyclerView.setFocusable(true);
        menuRecyclerView.setNestedScrollingEnabled(false);

        menuItemsList = new ArrayList<>();

        menuAdapter = new NavigationMenuAdapter(menuItemsList, (item, position) -> {

            Log.e("TEST_CLIC", "════ LAMBDA APPELÉ ════");
            Log.e("TEST_CLIC", "Item: " + item.getTitle());
            Log.e("TEST_CLIC", "Position: " + position);

            onMenuItemClick(item, position);
        });

        menuRecyclerView.setAdapter(menuAdapter);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                drawerOverlay.setVisibility(View.VISIBLE);
                drawerOverlay.setAlpha(slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawerOverlay.setVisibility(View.VISIBLE);
                drawerOverlay.setAlpha(1f);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                drawerOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        drawerOverlay.setOnClickListener(v ->
                drawerLayout.closeDrawer(GravityCompat.START)
        );
    }



    /**
     * Configure le header avec les écouteurs de clics
     */
    private void setupHeader() {
        menuIcon.setOnClickListener(v -> openMenu());
        languageButton.setOnClickListener(v -> changeLanguage());
        updateLanguageButton();
    }

    /**
     * Ouvre le menu de navigation
     */
    private void openMenu() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Gestion des clics sur les items du menu
     */
    private void onMenuItemClick(NavigationMenuAdapter.MenuItem item, int position) {
        Log.d(TAG, "🎯 CLIC MENU | Title=[" + item.getTitle() + "] Id=[" + item.getId() + "]");

        drawerLayout.closeDrawer(GravityCompat.START);

        // ── "À LA UNE" → reste sur HomeTnActivity, affiche tout ─────────
        if (item.getId().equals("alaune")) {
            showRecyclerView();
            displayNews(allNewsList);
            menuAdapter.setSelectedPosition(0);
            return;
        }

        // ── "A propos" ───────────────────────────────────────────────────
        if (item.getId().equals("about")) {
            openAproposFragment();
            return;
        }

        // ── "Paramètres" ─────────────────────────────────────────────────
        if (item.getId().equals("settings")) {
            openParametreFragment();
            return;
        }

        // ── CATÉGORIES → ouvrir CategoryNewsActivity ─────────────────────
        Intent intent = new Intent(HomeTnActivity.this, CategoryNewsActivity.class);
        intent.putExtra(CategoryNewsActivity.EXTRA_CATEGORY_NAME, item.getTitle());
        intent.putExtra(CategoryNewsActivity.EXTRA_CATEGORY_ID,   item.getId());
        startActivity(intent);
    }
//    /**
//     * Gestion des clics sur les items du menu
//     */
//    private void onMenuItemClick(NavigationMenuAdapter.MenuItem item, int position) {
//        Log.e("TEST_CLIC", "════ onMenuItemClick APPELÉ ════");
//
//        Log.d(TAG, "════════════════════════════════════════════");
//        Log.d(TAG, "🎯 CLIC MENU REÇU");
//        Log.d(TAG, "  Title: [" + item.getTitle() + "]");
//        Log.d(TAG, "  ID: [" + item.getId() + "]");
//        Log.d(TAG, "  Position: " + position);
//        Log.d(TAG, "════════════════════════════════════════════");
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//
//        // ✅ GESTION "À LA UNE"
//        if (item.getId().equals("alaune")) {
//            Log.d(TAG, "✅ Affichage À LA UNE - " + allNewsList.size() + " articles");
//            showRecyclerView();
//            displayNews(allNewsList);
//            menuAdapter.setSelectedPosition(0);
//            return;
//        }
//
//        // "A propos"
//        if (item.getId().equals("about")) {
//            Log.d(TAG, "✅ Ouverture fragment A propos");
//            openAproposFragment();
//            return;
//        }
//
//        // "Paramètres"
//        if (item.getId().equals("settings")) {
//            Log.d(TAG, "✅ Ouverture fragment Paramètres");
//            openParametreFragment();
//            return;
//        }
//
//        // ✅ CATÉGORIES - Filtrer les articles
//        if (allNewsList != null && !allNewsList.isEmpty()) {
//            Log.d(TAG, "🔍 Lancement du filtrage...");
//            List<News> filteredNews = filterNewsByCategory(item.getTitle());
//
//            if (filteredNews.isEmpty()) {
//                Log.w(TAG, "⚠️ Aucun article trouvé pour: " + item.getTitle());
//                Toast.makeText(this, "Aucun article pour " + item.getTitle(), Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d(TAG, "✅ " + filteredNews.size() + " articles trouvés - Affichage");
//                menuAdapter.setSelectedPosition(position);
//                showRecyclerView();
//                displayNews(filteredNews);
//            }
//        } else {
//            Log.e(TAG, "❌ allNewsList est vide ou null !");
//        }
//    }
//

    /**
     * Filtre les articles par catégorie
     */
    /**
     * ✅ FILTRE AMÉLIORÉ - Compare en lowercase et teste plusieurs variantes
     */
    /**
     * ✅ FILTRE AMÉLIORÉ - Compare en lowercase et teste plusieurs variantes
     */
    private List<News> filterNewsByCategory(String categoryName) {
        List<News> filtered = new ArrayList<>();

        Log.d(TAG, "════════════════════════════════════════════");
        Log.d(TAG, "🔍 FILTRAGE DÉTAILLÉ");
        Log.d(TAG, "  Catégorie recherchée: [" + categoryName + "]");
        Log.d(TAG, "  Total articles: " + allNewsList.size());
        Log.d(TAG, "════════════════════════════════════════════");

        // Normaliser le nom de catégorie
        String normalizedCategory = categoryName.trim().toLowerCase();

        // Variantes à tester
        String[] variants = {
                normalizedCategory,
                normalizedCategory.replaceAll("\\s+", ""),      // Sans espaces
                normalizedCategory.replaceAll("[\\s&_-]", "")   // Sans espaces, &, _, -
        };

        Log.d(TAG, "  Variantes testées:");
        for (String v : variants) {
            Log.d(TAG, "    - [" + v + "]");
        }

        // Afficher quelques exemples de typeNews
        Log.d(TAG, "  Exemples de typeNews:");
        for (int i = 0; i < Math.min(5, allNewsList.size()); i++) {
            Log.d(TAG, "    [" + i + "] TypeNews: [" + allNewsList.get(i).getTypeNews() + "]");
        }

        // Filtrer
        int matchCount = 0;
        for (News news : allNewsList) {
            String newsType = news.getTypeNews();

            if (newsType == null || newsType.isEmpty()) {
                continue;
            }

            String normalizedNewsType = newsType.trim().toLowerCase();

            // Tester toutes les variantes
            boolean matched = false;
            for (String variant : variants) {
                // Test 1: Égalité exacte
                if (normalizedNewsType.equals(variant)) {
                    filtered.add(news);
                    matched = true;
                    if (matchCount < 3) {
                        Log.d(TAG, "  ✅ Match [" + newsType + "] == [" + variant + "]");
                    }
                    break;
                }

                // Test 2: Contient
                if (normalizedNewsType.contains(variant)) {
                    filtered.add(news);
                    matched = true;
                    if (matchCount < 3) {
                        Log.d(TAG, "  ✅ Match [" + newsType + "] contains [" + variant + "]");
                    }
                    break;
                }
            }

            if (matched) matchCount++;
        }

        Log.d(TAG, "════════════════════════════════════════════");
        Log.d(TAG, "  📊 RÉSULTAT: " + filtered.size() + " articles filtrés");
        Log.d(TAG, "════════════════════════════════════════════");

        return filtered;
    }


















    private void openAproposFragment() {
        Fragment fragment = new AproposFragment();
        showFragment(fragment);
    }

    private void openParametreFragment() {
        Fragment fragment = new ParametreFragment();
        showFragment(fragment);
    }

    /**
     * Affiche un fragment et cache le RecyclerView
     */
    private void showFragment(Fragment fragment) {
        newsRecyclerView.setVisibility(View.GONE);
      //  progressBar.setVisibility(View.GONE);
        if (dossierViewPager != null) {
            dossierViewPager.setVisibility(View.GONE);
        }

        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        isShowingFragment = true;
    }

    /**
     * Affiche le RecyclerView et cache le fragment
     */
    private void showRecyclerView() {
        newsRecyclerView.setVisibility(View.VISIBLE);
        if (dossierViewPager != null) {
            dossierViewPager.setVisibility(View.VISIBLE);
        }

      //  startAutoScroll();

        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.GONE);
        }

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(currentFragment);
            transaction.commit();
        }

        isShowingFragment = false;
    }

    private void changeLanguage() {
        Intent intent = new Intent(HomeTnActivity.this, LangueActivity.class);
        startActivity(intent);
    }

    private void updateLanguageButton() {
        String currentLang = SessionManager.getInstance().getCurrentLang(this);

        if (currentLang.equals(Constant.AR)) {
            languageButton.setText("AR");
        } else if (currentLang.equals(Constant.EN)) {
            languageButton.setText("EN");
        } else {
            languageButton.setText("FR");
        }
    }

    /**
     * Configure le RecyclerView
     */
    private void setupRecyclerView() {
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Charge tous les articles depuis le fichier JSON local SELON LA LANGUE
     */
    private void loadLocalArticles() {
        allNewsList = new ArrayList<>();
        String currentLng = SessionManager.getInstance().getCurrentLang(this);

        try {
            if (currentLng.equals(Constant.AR)) {
                allNewsList.addAll(new DataParser().getListFetchNews(
                        new Utils(this).getStringFromFile(Communication.FILE_NEWS_INIT_AR)
                ));
                Log.d(TAG, "📰 Articles chargés en ARABE: " + allNewsList.size());
            } else if (currentLng.equals(Constant.EN)) {
                allNewsList.addAll(new DataParser().getListFetchNews(
                        new Utils(this).getStringFromFile(Communication.FILE_NEWS_INIT_EN)
                ));
                Log.d(TAG, "📰 Articles chargés en ANGLAIS: " + allNewsList.size());
            } else {
                allNewsList.addAll(new DataParser().getListFetchNews(
                        new Utils(this).getStringFromFile(Communication.FILE_NEWS_INIT)
                ));
                Log.d(TAG, "📰 Articles chargés en FRANÇAIS: " + allNewsList.size());
            }

            // Afficher tous les articles dans NEWS
            displayNews(allNewsList);

            // Charger les catégories pour le menu
            loadCategoriesForMenu();

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur chargement articles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge les catégories pour le menu drawer
     */
    private void loadCategoriesForMenu() {
        Log.e("MENU_DEBUG", "════════════════════════════════");
        Log.e("MENU_DEBUG", "🔥 loadCategoriesForMenu APPELÉE");
        Log.e("MENU_DEBUG", "════════════════════════════════");
        String currentLang = SessionManager.getInstance().getCurrentLang(this);

        try {
            List<com.mdweb.tunnumerique.data.model.Categories> categoriesListTemp;

            if (currentLang.equals(Constant.AR)) {
                categoriesListTemp = new DataParser().getListCategories(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES_AR), 0);
            } else if (currentLang.equals(Constant.EN)) {
                categoriesListTemp = new DataParser().getListCategories(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES_EN), 0);
            } else {
                categoriesListTemp = new DataParser().getListCategories(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES), 1);
            }

            updateDrawerMenu(categoriesListTemp);

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur catégories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateDrawerMenu(List<com.mdweb.tunnumerique.data.model.Categories> categories) {
        Log.e("MENU_DEBUG", "════════════════════════════════");
        Log.e("MENU_DEBUG", "🔥 updateDrawerMenu APPELÉE");
        Log.e("MENU_DEBUG", "  Catégories reçues: " + categories.size());
        Log.e("MENU_DEBUG", "════════════════════════════════");
        menuItemsList.clear();

        // ✅ AJOUTER "À LA UNE" EN PREMIER COMME HEADER
        menuItemsList.add(new NavigationMenuAdapter.MenuItem("A la une", "alaune", true));

        // ✅ AJOUTER LES CATÉGORIES
        for (com.mdweb.tunnumerique.data.model.Categories category : categories) {
            menuItemsList.add(new NavigationMenuAdapter.MenuItem(
                    category.getTitleCategories(),
                    category.getTitleCategories()
            ));
        }

        // ✅ AJOUTER A PROPOS ET PARAMÈTRES
        menuItemsList.add(new NavigationMenuAdapter.MenuItem("A propos", "about"));
        menuItemsList.add(new NavigationMenuAdapter.MenuItem("Paramètres", "settings"));

        menuAdapter.notifyDataSetChanged();
        menuAdapter.setSelectedPosition(0);
    }

    /**
     * Charge les dossiers pour "À LA UNE" SELON LA LANGUE
     */
    private void loadDossiers() {
        dossiersList = new ArrayList<>();
        String currentLng = SessionManager.getInstance().getCurrentLang(this);

        try {
            if (currentLng.equals(Constant.AR)) {
                dossiersList.addAll(new DataParser().getListDossier(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_DOSSIER_AR)
                ));
                Log.d(TAG, "📂 Dossiers chargés en ARABE: " + dossiersList.size());
            } else if (currentLng.equals(Constant.EN)) {
                dossiersList.addAll(new DataParser().getListDossier(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_DOSSIER_EN)
                ));
                Log.d(TAG, "📂 Dossiers chargés en ANGLAIS: " + dossiersList.size());
            } else {
                dossiersList.addAll(new DataParser().getListDossier(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_DOSSIER)
                ));
                Log.d(TAG, "📂 Dossiers chargés en FRANÇAIS: " + dossiersList.size());
            }

            setupDossierViewPager();

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur chargement dossiers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configure le ViewPager2 pour "À LA UNE" avec callbacks Save et Share
     */
    private void setupDossierViewPager() {
        if (dossiersList == null || dossiersList.isEmpty()) {
            if (dossierViewPager != null) {
                dossierViewPager.setVisibility(View.GONE);
            }
            return;
        }

        dossierViewPager.setVisibility(View.VISIBLE);

        dossierAdapter = new DossierSliderAdapter(this, dossiersList);
        dossierViewPager.setAdapter(dossierAdapter);

        // ✅ Callback pour ouvrir le détail
        dossierAdapter.setOnDossierClickListener(dossier -> {
            openDossierDetail(dossier);
        });

        // ✅ CALLBACKS POUR SAVE ET SHARE
        dossierAdapter.setOnDossierActionListener(new DossierSliderAdapter.OnDossierActionListener() {
            @Override
            public void onDossierSave(News dossier) {
                saveDossierToFavorites(dossier);
            }

            @Override
            public void onDossierShare(News dossier) {
                shareDossier(dossier);
            }
        });

        // Callback pour le changement de page
        dossierViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);

                        Log.d(TAG, "📄 Page changée vers position: " + position);

                        // Mettre à jour les dots via l'adapter
                        if (dossierAdapter != null) {
                            dossierAdapter.updateDots(position);
                        }
                    }
                });

        Log.d(TAG, "✅ ViewPager configuré avec " + dossiersList.size() + " dossiers");
    }

    // ===========================
    // ✅ MÉTHODE SAVE - AJOUTER/RETIRER DES FAVORIS
    // ===========================
    private void saveDossierToFavorites(News dossier) {
        Log.d(TAG, "💾 Action sauvegarde du dossier: " + dossier.getTitleNews());

        try {
            FavorisDataBase db = FavorisDataBase.getInstance(this);
            String currentLang = SessionManager.getInstance().getCurrentLang(this);

            // ✅ Vérifier si déjà dans les favoris
            News existingNews = db.getNews(dossier.getIdNews(), dossier.getArtOrPubOrVid());

            if (existingNews != null) {
                // ✅ RETIRER des favoris
                db.deleteNews(dossier.getIdNews(), dossier.getArtOrPubOrVid());
                Toast.makeText(this, "Retiré des favoris ✓", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "✅ Dossier retiré des favoris - ID: " + dossier.getIdNews());
            } else {
                // ✅ AJOUTER aux favoris
                // S'assurer que la langue est définie
                if (dossier.getNewsLng() == null || dossier.getNewsLng().isEmpty()) {
                    dossier.setNewsLng(currentLang);
                }

                db.addNews(dossier);
                Toast.makeText(this, "Ajouté aux favoris ✓", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "✅ Dossier ajouté aux favoris - ID: " + dossier.getIdNews() + ", Langue: " + currentLang);
            }

            // ✅ Rafraîchir l'adapter pour mettre à jour l'icône
            if (dossierAdapter != null) {
                dossierAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur sauvegarde: " + e.getMessage());
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // ===========================
    // ✅ MÉTHODE SHARE - PARTAGER LE DOSSIER
    // ===========================
    private void shareDossier(News dossier) {
        Log.d(TAG, "🔗 Action partage du dossier: " + dossier.getTitleNews());

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            String shareMessage = dossier.getTitleNews();

            // Ajouter l'URL si disponible
            if (dossier.getShareUrlNews() != null && !dossier.getShareUrlNews().isEmpty()) {
                shareMessage += "\n\n" + dossier.getShareUrlNews();
            } else if (dossier.getShareUrlNews() != null && !dossier.getShareUrlNews().isEmpty()) {
                shareMessage += "\n\n" + dossier.getShareUrlNews();
            }

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "À LA UNE");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(shareIntent, "Partager via"));

            Log.d(TAG, "✅ Partage lancé");

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur partage: " + e.getMessage());
            Toast.makeText(this, "Erreur lors du partage", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }




    private void saveNewsToFavorites(News dossier) {
        Log.d(TAG, "💾 Action sauvegarde du dossier: " + dossier.getTitleNews());

        try {
            FavorisDataBase db = FavorisDataBase.getInstance(this);
            String currentLang = SessionManager.getInstance().getCurrentLang(this);

            // ✅ Vérifier si déjà dans les favoris
            News existingNews = db.getNews(dossier.getIdNews(), dossier.getArtOrPubOrVid());

            if (existingNews != null) {
                // ✅ RETIRER des favoris
                db.deleteNews(dossier.getIdNews(), dossier.getArtOrPubOrVid());
                Toast.makeText(this, "Retiré des favoris ✓", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "✅ Dossier retiré des favoris - ID: " + dossier.getIdNews());
            } else {
                // ✅ AJOUTER aux favoris
                // S'assurer que la langue est définie
                if (dossier.getNewsLng() == null || dossier.getNewsLng().isEmpty()) {
                    dossier.setNewsLng(currentLang);
                }

                db.addNews(dossier);
                Toast.makeText(this, "Ajouté aux favoris ✓", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "✅ Dossier ajouté aux favoris - ID: " + dossier.getIdNews() + ", Langue: " + currentLang);
            }

            // ✅ Rafraîchir l'adapter pour mettre à jour l'icône
            if (dossierAdapter != null) {
                dossierAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur sauvegarde: " + e.getMessage());
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // ===========================
    // ✅ MÉTHODE SHARE - PARTAGER LE DOSSIER
    // ===========================
    private void shareNews(News dossier) {
        Log.d(TAG, "🔗 Action partage du dossier: " + dossier.getTitleNews());

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            String shareMessage = dossier.getTitleNews();

            // Ajouter l'URL si disponible
            if (dossier.getShareUrlNews() != null && !dossier.getShareUrlNews().isEmpty()) {
                shareMessage += "\n\n" + dossier.getShareUrlNews();
            } else if (dossier.getShareUrlNews() != null && !dossier.getShareUrlNews().isEmpty()) {
                shareMessage += "\n\n" + dossier.getShareUrlNews();
            }

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "À LA UNE");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(shareIntent, "Partager via"));

            Log.d(TAG, "✅ Partage lancé");

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur partage: " + e.getMessage());
            Toast.makeText(this, "Erreur lors du partage", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Démarre le défilement automatique du ViewPager
     */
    private void startAutoScroll() {
        if (dossiersList == null || dossiersList.isEmpty()) return;

        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (dossiersList != null && !dossiersList.isEmpty() && dossierViewPager != null) {
                    currentDossierPosition = (currentDossierPosition + 1) % dossiersList.size();
                    dossierViewPager.setCurrentItem(currentDossierPosition, true);
                    autoScrollHandler.postDelayed(this, 3000);
                }
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
    }

    /**
     * Arrête le défilement automatique
     */
    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    /**
     * Affiche la liste des articles NEWS
     */
    private void displayNews(List<News> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            Toast.makeText(this, "Aucun article disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "📺 Affichage de " + newsList.size() + " articles NEWS");

        newsAdapter = new NewsListAdapter(this, newsList);

        // ✅ OUVERTURE DU DÉTAIL
        newsAdapter.setOnNewsClickListener(news -> openArticleDetail(news));

        // ✅ SAVE & SHARE
        newsAdapter.setOnNewsActionListener(new NewsListAdapter.OnNewsActionListener() {
            @Override
            public void onNewsSave(News news) {
                saveNewsToFavorites(news);
            }

            @Override
            public void onNewsShare(News news) {
                shareNews(news);
            }
        });

        newsRecyclerView.setAdapter(newsAdapter);
    }





    private void openArticleDetail(News news) {
        Intent intent = new Intent(HomeTnActivity.this, ArticleDetailActivity.class);
        intent.putExtra("news_object", news);
        startActivity(intent);
    }

    private void openDossierDetail(News dossier) {
        Intent intent = new Intent(HomeTnActivity.this, ArticleDetailActivity.class);
        intent.putExtra("news_object", dossier);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (isShowingFragment) {
            showRecyclerView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguageButton();

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        if (!isShowingFragment) {
            // startAutoScroll();
        }

        String currentLang = SessionManager.getInstance().getCurrentLang(this);
        String previousLang = languageButton.getText().toString().toLowerCase();

        if ((currentLang.equals(Constant.AR) && !previousLang.equals("ar")) ||
                (currentLang.equals(Constant.EN) && !previousLang.equals("en")) ||
                (currentLang.equals(Constant.FR) && !previousLang.equals("fr"))) {

            Log.d(TAG, "🔄 Langue changée - Rechargement");
            loadLocalArticles();
            loadDossiers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopAutoScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopAutoScroll();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}