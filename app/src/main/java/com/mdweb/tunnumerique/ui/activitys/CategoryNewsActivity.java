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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CategoryNewsActivity extends AppCompatActivity {

    private static final String TAG = "CategoryNewsActivity";

    public static final String EXTRA_CATEGORY_NAME = "CATEGORY_NAME";
    public static final String EXTRA_CATEGORY_ID   = "CATEGORY_ID";
    public static final String EXTRA_IS_ALAUNE     = "IS_ALAUNE";

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
    private final Set<String> availableNormalizedCategories = new LinkedHashSet<>();

    // Catégorie courante
    private String currentCategoryName = "";
    private String currentCategoryId = "";
    private boolean isAlaUnePage = false;

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
        String categoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);

        if (categoryName == null) categoryName = "";
        if (categoryId == null) categoryId = "";
        currentCategoryName = categoryName;
        currentCategoryId = categoryId;

        toolbarTitle.setText(capitalize(categoryName));
        loadAndDisplay(categoryName, categoryId);
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
            String categoryId = category.getTitleUrlCategories();
            if (categoryId == null || categoryId.trim().isEmpty()) {
                categoryId = category.getTitleCategories();
            }
            menuItemsList.add(new NavigationMenuAdapter.MenuItem(
                    category.getTitleCategories(),
                    categoryId
            ));
        }

        menuItemsList.add(new NavigationMenuAdapter.MenuItem("A propos", "about"));
        menuItemsList.add(new NavigationMenuAdapter.MenuItem("Paramètres", "settings"));

        menuAdapter.notifyDataSetChanged();

        if (isAlaUnePage) {
            menuAdapter.setSelectedPosition(0);
            return;
        }

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
            Intent intent = new Intent(CategoryNewsActivity.this, AlaUneActivity.class);
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
        currentCategoryId = item.getId();
        toolbarTitle.setText(capitalize(currentCategoryName));
        menuAdapter.setSelectedPosition(position);
        loadAndDisplay(currentCategoryName, currentCategoryId);
    }

    // ════════════════════════════════════════════════════════
    // Chargement des articles
    // ════════════════════════════════════════════════════════

    private void loadAndDisplay(String categoryName, String categoryId) {
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

        buildAvailableCategoryIndex();
        List<News> filtered = filterByCategory(categoryName, categoryId);

        if (filtered.isEmpty()) {
            Toast.makeText(this, "Aucun article pour " + categoryName, Toast.LENGTH_SHORT).show();
        }

        displayNews(filtered);
    }

    // ════════════════════════════════════════════════════════
    // Filtre
    // ════════════════════════════════════════════════════════

    private List<News> filterByCategory(String categoryName, String categoryId) {
        List<News> filtered = new ArrayList<>();
        if ((categoryName == null || categoryName.isEmpty())
                && (categoryId == null || categoryId.isEmpty())) {
            return allNewsList;
        }

        List<String> normalizedCandidates = buildCategoryCandidates(categoryName, categoryId);
        Log.d(TAG, "Filter candidates = " + normalizedCandidates);
        Log.d(TAG, "Available categories = " + availableNormalizedCategories);

        for (News news : allNewsList) {
            String type = news.getTypeNews();
            if (type == null || type.isEmpty()) continue;

            String[] tokens = type.split("[,;|،]");
            for (String token : tokens) {
                String normalizedToken = normalizeCategoryToken(token);
                if (matchesAnyCandidate(normalizedCandidates, normalizedToken)) {
                    filtered.add(news);
                    break;
                }
            }
        }

        return filtered;
    }

    private String normalizeCategoryToken(String value) {
        if (value == null) return "";
        String normalized = decodeUnicodeEscapes(value);
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
        return normalized.replaceAll("[^\\p{L}\\p{N}]+", "");
    }

    private boolean categoriesMatch(String normalizedCategory, String normalizedToken) {
        if (normalizedCategory == null || normalizedToken == null) return false;
        if (normalizedCategory.isEmpty() || normalizedToken.isEmpty()) return false;

        if (normalizedToken.equals(normalizedCategory)) {
            return true;
        }

        if (normalizedCategory.length() < 3 || normalizedToken.length() < 3) {
            return false;
        }

        return normalizedToken.contains(normalizedCategory)
                || normalizedCategory.contains(normalizedToken);
    }

    private boolean matchesAnyCandidate(List<String> normalizedCandidates, String normalizedToken) {
        if (normalizedToken == null || normalizedToken.isEmpty()) return false;
        for (String candidate : normalizedCandidates) {
            if (categoriesMatch(candidate, normalizedToken)) {
                return true;
            }
        }
        return false;
    }

    private List<String> buildCategoryCandidates(String categoryName, String categoryId) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();

        addCandidate(candidates, categoryName);
        addCandidate(candidates, categoryId);

        if (categoryId != null) {
            String[] idParts = categoryId.split("[-_/]");
            for (String part : idParts) {
                addCandidate(candidates, part);
            }
        }

        // Correspondances FR <-> AR les plus courantes.
        if (containsCandidate(candidates, "actualites")) {
            addCandidate(candidates, "اخبار");
            addCandidate(candidates, "الاخبار");
        } else if (containsCandidate(candidates, "اخبار") || containsCandidate(candidates, "الاخبار")) {
            addCandidate(candidates, "actualites");
            addCandidate(candidates, "actualite");
        }

        return new ArrayList<>(candidates);
    }

    private boolean containsCandidate(Set<String> candidates, String value) {
        return candidates.contains(normalizeCategoryToken(value));
    }

    private void addCandidate(Set<String> candidates, String rawValue) {
        String normalized = normalizeCategoryToken(rawValue);
        if (!normalized.isEmpty()) {
            candidates.add(normalized);
        }
    }

    private void buildAvailableCategoryIndex() {
        availableNormalizedCategories.clear();
        for (News news : allNewsList) {
            String type = news.getTypeNews();
            if (type == null || type.isEmpty()) continue;

            String[] tokens = type.split("[,;|،]");
            for (String token : tokens) {
                String normalized = normalizeCategoryToken(token);
                if (!normalized.isEmpty()) {
                    availableNormalizedCategories.add(normalized);
                }
            }
        }
    }

    private String decodeUnicodeEscapes(String value) {
        StringBuilder result = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\\' && i + 5 < value.length() && value.charAt(i + 1) == 'u') {
                String hex = value.substring(i + 2, i + 6);
                try {
                    result.append((char) Integer.parseInt(hex, 16));
                    i += 5;
                    continue;
                } catch (NumberFormatException ignored) {
                    // Garde la chaîne d'origine si la séquence n'est pas valide.
                }
            }
            result.append(current);
        }

        return result.toString();
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
