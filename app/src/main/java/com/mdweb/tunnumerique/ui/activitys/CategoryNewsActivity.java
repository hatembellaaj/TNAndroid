package com.mdweb.tunnumerique.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.adapters.CategoryNewsAdapter;
import com.mdweb.tunnumerique.ui.adapters.NavigationMenuAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryNewsActivity extends AppCompatActivity {

    private static final String TAG = "CategoryNewsActivity";

    public static final String EXTRA_CATEGORY_NAME = "CATEGORY_NAME";
    public static final String EXTRA_CATEGORY_ID   = "CATEGORY_ID";

    // ── Header ──
    private ImageView menuIcon;
    private TextView toolbarTitle;
    private TextView languageButton;

    // ── Drawer ──
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View drawerOverlay;
    private RecyclerView menuRecyclerView;
    private NavigationMenuAdapter menuAdapter;
    private ImageView closeDrawer;
    private List<NavigationMenuAdapter.MenuItem> menuItemsList;

    // ── Bottom Navigation ──
    private BottomNavigationView bottomNavigationView;

    // ── Contenu ──
    private RecyclerView newsRecyclerView;
    private CategoryNewsAdapter newsAdapter;
    private List<News> allNewsList = new ArrayList<>();

    // Catégorie courante
    private String currentCategoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_news);

        initViews();
        setupHeader();
        setupDrawer();
        setupBottomNavigation();

        // Intent extras
        String categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        if (categoryName == null) categoryName = "";
        currentCategoryName = categoryName;

        toolbarTitle.setText(capitalize(categoryName));
        loadAndDisplay(categoryName);
    }

    // ════════════════════════════════════════════════════════
    // Init vues
    // ════════════════════════════════════════════════════════

    private void initViews() {
        menuIcon             = findViewById(R.id.menu_icon);
        toolbarTitle         = findViewById(R.id.categoryToolbarTitle);
        languageButton       = findViewById(R.id.language_button);
        drawerLayout         = findViewById(R.id.drawerLayout);
        navigationView       = findViewById(R.id.navigationView);
        drawerOverlay        = findViewById(R.id.drawerOverlay);
        newsRecyclerView     = findViewById(R.id.categoryNewsRecyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // ════════════════════════════════════════════════════════
    // Header
    // ════════════════════════════════════════════════════════

    private void setupHeader() {
        menuIcon.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        updateLanguageButton();
        languageButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryNewsActivity.this, LangueActivity.class);
            startActivity(intent);
        });
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

    // ════════════════════════════════════════════════════════
    // Bottom Navigation — même logique que HomeTnActivity
    // ════════════════════════════════════════════════════════

    private void setupBottomNavigation() {
        // Aucun item sélectionné par défaut (on est sur une catégorie, pas sur Home)
        bottomNavigationView.setSelectedItemId(0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Retour à HomeTnActivity
                Intent intent = new Intent(CategoryNewsActivity.this, HomeTnActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_horaires) {
                Intent intent = new Intent(CategoryNewsActivity.this, MainActivity.class);
                intent.putExtra("FRAGMENT_TO_LOAD", "horaires");
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_enregistres) {
                Intent intent = new Intent(CategoryNewsActivity.this, MainActivity.class);
                intent.putExtra("FRAGMENT_TO_LOAD", "favoris");
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_top24) {
                Intent intent = new Intent(CategoryNewsActivity.this, MainActivity.class);
                intent.putExtra("FRAGMENT_TO_LOAD", "top24");
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    // ════════════════════════════════════════════════════════
    // Drawer
    // ════════════════════════════════════════════════════════

    private void setupDrawer() {
        drawerOverlay.setClickable(false);
        drawerOverlay.setFocusable(false);

        navigationView.setClickable(false);
        navigationView.setFocusable(false);
        navigationView.setFocusableInTouchMode(false);

        View headerView      = navigationView.getHeaderView(0);
        closeDrawer          = headerView.findViewById(R.id.closeDrawer);
        menuRecyclerView     = headerView.findViewById(R.id.menuRecyclerView);

        closeDrawer.setOnClickListener(v ->
                drawerLayout.closeDrawer(GravityCompat.START)
        );

        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerView.setClickable(true);
        menuRecyclerView.setFocusable(true);
        menuRecyclerView.setNestedScrollingEnabled(false);

        menuItemsList = new ArrayList<>();

        menuAdapter = new NavigationMenuAdapter(menuItemsList, (item, position) ->
                onMenuItemClick(item, position)
        );

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

        loadCategoriesForMenu();
    }

    private void loadCategoriesForMenu() {
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
            Log.e(TAG, "Erreur catégories menu : " + e.getMessage());
        }
    }

    private void updateDrawerMenu(List<com.mdweb.tunnumerique.data.model.Categories> categories) {
        menuItemsList.clear();

        menuItemsList.add(new NavigationMenuAdapter.MenuItem("A la une", "alaune", true));

        for (com.mdweb.tunnumerique.data.model.Categories category : categories) {
            menuItemsList.add(new NavigationMenuAdapter.MenuItem(
                    category.getTitleCategories(),
                    category.getTitleCategories()
            ));
        }

        menuItemsList.add(new NavigationMenuAdapter.MenuItem("A propos", "about"));
        menuItemsList.add(new NavigationMenuAdapter.MenuItem("Paramètres", "settings"));

        menuAdapter.notifyDataSetChanged();

        // Surligner la catégorie courante dans le menu
        for (int i = 0; i < menuItemsList.size(); i++) {
            if (menuItemsList.get(i).getTitle().equalsIgnoreCase(currentCategoryName)) {
                menuAdapter.setSelectedPosition(i);
                break;
            }
        }
    }

    private void onMenuItemClick(NavigationMenuAdapter.MenuItem item, int position) {
        drawerLayout.closeDrawer(GravityCompat.START);

        if (item.getId().equals("alaune")) {
            // Retour à HomeTnActivity
            Intent intent = new Intent(CategoryNewsActivity.this, HomeTnActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return;
        }

        if (item.getId().equals("about")) {
            Intent intent = new Intent(CategoryNewsActivity.this, HomeTnActivity.class);
            intent.putExtra("OPEN_FRAGMENT", "about");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        if (item.getId().equals("settings")) {
            Intent intent = new Intent(CategoryNewsActivity.this, HomeTnActivity.class);
            intent.putExtra("OPEN_FRAGMENT", "settings");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        // Autre catégorie → recharger sur place
        currentCategoryName = item.getTitle();
        toolbarTitle.setText(capitalize(currentCategoryName));
        menuAdapter.setSelectedPosition(position);
        loadAndDisplay(currentCategoryName);
    }

    // ════════════════════════════════════════════════════════
    // Chargement des articles
    // ════════════════════════════════════════════════════════

    private void loadAndDisplay(String categoryName) {
        allNewsList.clear();
        String currentLng = SessionManager.getInstance().getCurrentLang(this);

        try {
            String json;
            if (currentLng.equals(Constant.AR)) {
                json = new Utils(this).getStringFromFile(Communication.FILE_NEWS_INIT_AR);
            } else if (currentLng.equals(Constant.EN)) {
                json = new Utils(this).getStringFromFile(Communication.FILE_NEWS_INIT_EN);
            } else {
                json = new Utils(this).getStringFromFile(Communication.FILE_NEWS_INIT);
            }

            allNewsList.addAll(new DataParser().getListFetchNews(json));

        } catch (Exception e) {
            Log.e(TAG, "Erreur chargement : " + e.getMessage());
        }

        List<News> filtered = filterByCategory(categoryName);

        if (filtered.isEmpty()) {
            Toast.makeText(this, "Aucun article pour " + categoryName, Toast.LENGTH_SHORT).show();
        }

        displayNews(filtered);
    }

    // ════════════════════════════════════════════════════════
    // Filtre
    // ════════════════════════════════════════════════════════

    private List<News> filterByCategory(String categoryName) {
        List<News> filtered = new ArrayList<>();
        if (categoryName == null || categoryName.isEmpty()) return allNewsList;

        String normalized = categoryName.trim().toLowerCase();
        String[] variants = {
                normalized,
                normalized.replaceAll("\\s+", ""),
                normalized.replaceAll("[\\s&_-]", "")
        };

        for (News news : allNewsList) {
            String type = news.getTypeNews();
            if (type == null || type.isEmpty()) continue;

            String normalizedType = type.trim().toLowerCase();
            for (String v : variants) {
                if (normalizedType.equals(v) || normalizedType.contains(v)) {
                    filtered.add(news);
                    break;
                }
            }
        }

        return filtered;
    }

    // ════════════════════════════════════════════════════════
    // Affichage
    // ════════════════════════════════════════════════════════

    private void displayNews(List<News> list) {
        newsAdapter = new CategoryNewsAdapter(this, list);

        newsAdapter.setOnNewsClickListener(news -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra("news_object", news);
            startActivity(intent);
        });

        newsAdapter.setOnNewsActionListener(new CategoryNewsAdapter.OnNewsActionListener() {
            @Override
            public void onNewsSave(News news) {
                saveToFavorites(news);
            }

            @Override
            public void onNewsShare(News news) {
                shareNews(news);
            }
        });

        newsRecyclerView.setAdapter(newsAdapter);
    }

    // ════════════════════════════════════════════════════════
    // Favoris
    // ════════════════════════════════════════════════════════

    private void saveToFavorites(News news) {
        try {
            FavorisDataBase db = FavorisDataBase.getInstance(this);
            String currentLang = SessionManager.getInstance().getCurrentLang(this);

            News existing = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());
            if (existing != null) {
                db.deleteNews(news.getIdNews(), news.getArtOrPubOrVid());
                Toast.makeText(this, "Retiré des favoris ✓", Toast.LENGTH_SHORT).show();
            } else {
                if (news.getNewsLng() == null || news.getNewsLng().isEmpty()) {
                    news.setNewsLng(currentLang);
                }
                db.addNews(news);
                Toast.makeText(this, "Ajouté aux favoris ✓", Toast.LENGTH_SHORT).show();
            }

            if (newsAdapter != null) newsAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e(TAG, "Erreur favoris : " + e.getMessage());
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    // ════════════════════════════════════════════════════════
    // Partage
    // ════════════════════════════════════════════════════════

    private void shareNews(News news) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            String msg = news.getTitleNews();
            if (news.getShareUrlNews() != null && !news.getShareUrlNews().isEmpty()) {
                msg += "\n\n" + news.getShareUrlNews();
            }

            intent.putExtra(Intent.EXTRA_SUBJECT, news.getTitleNews());
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(Intent.createChooser(intent, "Partager via"));

        } catch (Exception e) {
            Log.e(TAG, "Erreur partage : " + e.getMessage());
            Toast.makeText(this, "Erreur lors du partage", Toast.LENGTH_SHORT).show();
        }
    }

    // ════════════════════════════════════════════════════════
    // Back pressed
    // ════════════════════════════════════════════════════════

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ════════════════════════════════════════════════════════
    // onResume
    // ════════════════════════════════════════════════════════

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguageButton();

        // Aucun item du bottom nav sélectionné sur cette page
        bottomNavigationView.setSelectedItemId(0);
    }

    // ════════════════════════════════════════════════════════
    // Helper
    // ════════════════════════════════════════════════════════

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}