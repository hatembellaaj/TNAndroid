package com.mdweb.tunnumerique.ui.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.mdweb.tunnumerique.ui.adapters.PlusLusAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlusLusActivity extends AppCompatActivity {

    private static final String TAG = "PlusLusActivity";

    // ── Vues Header ──────────────────────────────────────────────────
    private ImageView             menuIcon;
    private TextView              languageButton;

    // ── Drawer ───────────────────────────────────────────────────────
    private DrawerLayout          drawerLayout;
    private NavigationView        navigationView;
    private View                  drawerOverlay;

    // ── Contenu ──────────────────────────────────────────────────────
    private RecyclerView          recyclerViewPlusLus;
    private ProgressBar           progressBar;
    private TextView              tvEmpty;
    private TextView              categoryToolbarTitle;
    private BottomNavigationView  bottomNavigationView;

    // ── Données ──────────────────────────────────────────────────────
    private PlusLusAdapter        adapter;
    private List<News>            newsList;

    // ════════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_lus);

        initViews();
        setupHeader();
        setupDrawer();
        setupBottomNavigation();

        // Charger les articles Plus Lus selon la langue
        loadPlusLus();
    }

    // ── Initialisation des vues ──────────────────────────────────────
    private void initViews() {
        // Header
        menuIcon             = findViewById(R.id.menu_icon);
        languageButton       = findViewById(R.id.language_button);
        categoryToolbarTitle = findViewById(R.id.categoryToolbarTitle);

        // Drawer
        drawerLayout   = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        drawerOverlay  = findViewById(R.id.drawerOverlay);

        // Contenu
        recyclerViewPlusLus  = findViewById(R.id.recyclerViewPlusLus);
        progressBar          = findViewById(R.id.progressBar);
        tvEmpty              = findViewById(R.id.tvEmpty);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup RecyclerView
        recyclerViewPlusLus.setLayoutManager(new LinearLayoutManager(this));
    }

    // ── Header ───────────────────────────────────────────────────────
    private void setupHeader() {
        menuIcon.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        languageButton.setOnClickListener(v -> {
            Intent intent = new Intent(PlusLusActivity.this, LangueActivity.class);
            startActivity(intent);
        });

        updateLanguageButton();
        updateTitle();
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

    private void updateTitle() {
        String currentLang = SessionManager.getInstance().getCurrentLang(this);

        if (currentLang.equals(Constant.AR)) {
            categoryToolbarTitle.setText("الأكثر قراءة");
        } else if (currentLang.equals(Constant.EN)) {
            categoryToolbarTitle.setText("Most Read");
        } else {
            categoryToolbarTitle.setText("Top 24");
        }
    }

    // ── Chargement WS Plus Lus selon langue ─────────────────────────
    private void loadPlusLus() {
        newsList = new ArrayList<>();
        String currentLng = SessionManager.getInstance().getCurrentLang(this);

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        recyclerViewPlusLus.setVisibility(View.GONE);

        try {
            if (currentLng.equals(Constant.AR)) {
                newsList.addAll(new DataParser().getListNews(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_PLUS_LUS_AR)
                ));
                Log.d(TAG, "📰 Plus Lus chargés en ARABE: " + newsList.size());

            } else if (currentLng.equals(Constant.EN)) {
                newsList.addAll(new DataParser().getListNews(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_PLUS_LUS_EN)
                ));
                Log.d(TAG, "📰 Plus Lus chargés en ANGLAIS: " + newsList.size());

            } else {
                newsList.addAll(new DataParser().getListNews(
                        new Utils(this).getStringFromFile(Communication.FILE_NAME_PLUS_LUS)
                ));
                Log.d(TAG, "📰 Plus Lus chargés en FRANÇAIS: " + newsList.size());
            }

            displayPlusLus(newsList);

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur chargement Plus Lus: " + e.getMessage());
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    // ── Affichage des articles ────────────────────────────────────────
    private void displayPlusLus(List<News> list) {
        progressBar.setVisibility(View.GONE);

        if (list == null || list.isEmpty()) {
            Log.w(TAG, "⚠️ Aucun article Plus Lus disponible");
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerViewPlusLus.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "📺 Affichage de " + list.size() + " articles Plus Lus");

        tvEmpty.setVisibility(View.GONE);
        recyclerViewPlusLus.setVisibility(View.VISIBLE);

        adapter = new PlusLusAdapter(this, list);

        // ── Clic article → détail ────────────────────────────────────
        adapter.setOnArticleClickListener((news, position) -> {
            Intent intent = new Intent(PlusLusActivity.this, ArticleDetailActivity.class);
            intent.putExtra("news_object", news);
            startActivity(intent);
        });

        // ── Save & Share ─────────────────────────────────────────────
        adapter.setOnArticleActionListener(new PlusLusAdapter.OnArticleActionListener() {
            @Override
            public void onArticleSave(News news) {
                saveToFavorites(news);
            }

            @Override
            public void onArticleShare(News news) {
                shareArticle(news);
            }
        });

        recyclerViewPlusLus.setAdapter(adapter);
    }

    // ── Sauvegarde favoris ────────────────────────────────────────────
    private void saveToFavorites(News news) {
        Log.d(TAG, "💾 Sauvegarde: " + news.getTitleNews());

        try {
            FavorisDataBase db = FavorisDataBase.getInstance(this);
            String currentLang = SessionManager.getInstance().getCurrentLang(this);

            News existing = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());

            if (existing != null) {
                db.deleteNews(news.getIdNews(), news.getArtOrPubOrVid());
                Toast.makeText(this, "Retiré des favoris ✓", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "✅ Retiré des favoris - ID: " + news.getIdNews());
            } else {
                if (news.getNewsLng() == null || news.getNewsLng().isEmpty()) {
                    news.setNewsLng(currentLang);
                }
                db.addNews(news);
                Toast.makeText(this, "Ajouté aux favoris ✓", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "✅ Ajouté aux favoris - ID: " + news.getIdNews());
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur sauvegarde: " + e.getMessage());
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // ── Partage article ───────────────────────────────────────────────
    private void shareArticle(News news) {
        Log.d(TAG, "🔗 Partage: " + news.getTitleNews());

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            String shareMessage = news.getTitleNews();

            if (news.getShareUrlNews() != null && !news.getShareUrlNews().isEmpty()) {
                shareMessage += "\n\n" + news.getShareUrlNews();
            }

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Les Plus Lus");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(shareIntent, "Partager via"));
            Log.d(TAG, "✅ Partage lancé");

        } catch (Exception e) {
            Log.e(TAG, "❌ Erreur partage: " + e.getMessage());
            Toast.makeText(this, "Erreur lors du partage", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // ── Drawer ───────────────────────────────────────────────────────
    private void setupDrawer() {
        // FIX overlay & NavigationView (identique à HomeTnActivity)
        drawerOverlay.setClickable(false);
        drawerOverlay.setFocusable(false);
        navigationView.setClickable(false);
        navigationView.setFocusable(false);
        navigationView.setFocusableInTouchMode(false);

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

    // ── Bottom Navigation ─────────────────────────────────────────────
    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_top24);

        // Couleurs icône : vert si sélectionné, gris sinon (identique à HomeTnActivity)
        ColorStateList iconColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.parseColor("#88BD2E"),
                        Color.parseColor("#AAAAAA")
                }
        );

        // Couleurs texte : blanc si sélectionné, gris sinon
        ColorStateList textColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.WHITE,
                        Color.parseColor("#AAAAAA")
                }
        );

        bottomNavigationView.setItemIconTintList(iconColors);
        bottomNavigationView.setItemTextColor(textColors);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(PlusLusActivity.this, HomeTnActivity.class));
                finish();
                return true;

            } else if (itemId == R.id.nav_horaires) {
                Intent intent = new Intent(PlusLusActivity.this, MainActivity.class);
                intent.putExtra("FRAGMENT_TO_LOAD", "horaires");
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_enregistres) {
                Intent intent = new Intent(PlusLusActivity.this, MainActivity.class);
                intent.putExtra("FRAGMENT_TO_LOAD", "favoris");
                startActivity(intent);
                return true;

            } else if (itemId == R.id.nav_top24) {
                // Déjà sur cette page
                return true;
            }

            return false;
        });
    }

    // ── onResume : détecter changement de langue ──────────────────────
    @Override
    protected void onResume() {
        super.onResume();
        updateLanguageButton();

        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_top24);
        }

        // Recharger si la langue a changé
        String currentLang   = SessionManager.getInstance().getCurrentLang(this);
        String displayedLang = languageButton.getText().toString().toLowerCase();

        if ((currentLang.equals(Constant.AR) && !displayedLang.equals("ar")) ||
                (currentLang.equals(Constant.EN) && !displayedLang.equals("en")) ||
                (currentLang.equals(Constant.FR) && !displayedLang.equals("fr"))) {

            Log.d(TAG, "🔄 Langue changée - Rechargement Plus Lus");
            updateTitle();
            loadPlusLus();
        }
    }

    // ── Bouton retour ──────────────────────────────────────────────────
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}