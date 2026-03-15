package com.mdweb.tunnumerique.ui.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Categories;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.adapters.ArticleAdapter;
import com.mdweb.tunnumerique.ui.adapters.FavorisAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavorisActivity extends AppCompatActivity implements View.OnClickListener {

    // ── Extras keys ──
    public static final String EXTRA_TYPE     = "type";
    public static final String EXTRA_FAVORIS  = "favoris";

    // ── Views ──
    private DrawerLayout          drawerLayout;
    private NavigationView        navigationView;
    private View                  drawerOverlay;
    private TextView              toolbarTitle;
    private TextView              languageButton;
    private RecyclerView          recyclerFavoris;
  //  private RelativeLayout        emptyListe;
   // private FloatingActionButton  fabGuide;
    private BottomNavigationView  bottomNavigationView;

    // ── Data ──
    private FavorisAdapter articleAdapter;
    private LinearLayoutManager   linearLayoutManager;
    private List<News>            newsList;
    private List<News>            newsListArticle;
    private List<News>            listSearch;
    private List<Categories>      categoriesList;

    // ── State ──
    private int     type;
    private boolean isFavoris;
    private String  categorie;
    private Dialog  dialog;
    private Handler handler;

    // ────────────────────────────────────────────────
    //  Lifecycle
    // ────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);

        // Récupérer les extras
        if (getIntent() != null) {
            type      = getIntent().getIntExtra(EXTRA_TYPE, 0);
            isFavoris = getIntent().getBooleanExtra(EXTRA_FAVORIS, true);
        }

        handler = new Handler();
        initViews();
        loadCategories();
        setupDrawer();
        setupBottomNav();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData();
    }

    // ────────────────────────────────────────────────
    //  Init
    // ────────────────────────────────────────────────

    private void initViews() {
        drawerLayout         = findViewById(R.id.drawerLayout);
        navigationView       = findViewById(R.id.navigationView);
        drawerOverlay        = findViewById(R.id.drawerOverlay);
        toolbarTitle         = findViewById(R.id.toolbar_title);
        languageButton       = findViewById(R.id.language_button);
        recyclerFavoris      = findViewById(R.id.recycler_favoris);
        //emptyListe           = findViewById(R.id.empty_liste);
        //fabGuide             = findViewById(R.id.fab_guide);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Bouton retour
       // findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // FAB
       // fabGuide.setOnClickListener(this);

        // RecyclerView
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerFavoris.setLayoutManager(linearLayoutManager);

        newsList        = new ArrayList<>();
        newsListArticle = new ArrayList<>();
        listSearch      = new ArrayList<>();

        // Jdid
        articleAdapter = new FavorisAdapter(this, type);
        recyclerFavoris.setAdapter(articleAdapter);

        // Masquer/afficher le FAB au scroll
        final int mScrollOffset = 4;
        recyclerFavoris.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (newsList.size() > 1) {
                    if (Math.abs(dy) > mScrollOffset) {
                        if (dy > 0) {
                           // fabGuide.hide();
                        } else {
                           // fabGuide.show();
                        }
                    }
                }
            }
        });

        // Langue courante dans le bouton
        String lng = SessionManager.getInstance().getCurrentLang(this);
        languageButton.setText(lng.toUpperCase());
    }

    private void loadCategories() {
        String lng = SessionManager.getInstance().getCurrentLang(this);
        if (lng.equals(Constant.AR)) {
            categoriesList = new DataParser().getListCategories(
                    new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES_AR), 0);
        } else if (lng.equals(Constant.EN)) {
            categoriesList = new DataParser().getListCategories(
                    new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES_EN), 0);
        } else {
            categoriesList = new DataParser().getListCategories(
                    new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES), 1);
        }
    }

    // ────────────────────────────────────────────────
    //  Drawer
    // ────────────────────────────────────────────────

    private void setupDrawer() {
        // Ouvrir le drawer via le bouton menu (si tu en as un dans un autre écran)
        // Ici on gère juste l'overlay pour fermer
        drawerOverlay.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerOverlay.setVisibility(View.GONE);
            }
        });
    }

    // ────────────────────────────────────────────────
    //  Bottom Navigation
    // ────────────────────────────────────────────────

    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Gérer la navigation selon le menu
            // Exemple : retourner à MainActivity sur certains items
            int itemId = item.getItemId();
            // Adapte selon tes items de menu
            return true;
        });
    }

    // ────────────────────────────────────────────────
    //  Data
    // ────────────────────────────────────────────────

    private void loadData() {
        newsList.clear();
        newsListArticle.clear();

        // Ajouter la publicité en tête de liste
        News pubNews = new News(Constant.isPublicity);
        newsListArticle.add(pubNews);

        // Charger tous les favoris depuis la base SQLite
        newsList.addAll(
                FavorisDataBase.getInstance(this)
                        .getAllNews(SessionManager.getInstance().getCurrentLang(this))
        );

        // Filtrer : garder seulement les articles (pas les vidéos)
        for (int i = 0; i < newsList.size(); i++) {
            News unNews = newsList.get(i);
            if (unNews.getArtOrPubOrVid() != Constant.isVideo) {
                newsListArticle.add(unNews);
            }
        }

        articleAdapter.addAll(newsListArticle);

        listSearch.clear();
        listSearch.addAll(newsListArticle);

        checkListEmpty();
    }

    // ────────────────────────────────────────────────
    //  UI helpers
    // ────────────────────────────────────────────────

    /**
     * Affiche la vue "liste vide" si aucun favori n'est enregistré.
     */
    private void checkListEmpty() {
        // newsListArticle contient toujours au moins 1 élément (la pub)
        if (newsListArticle.size() == 1) {
          //  emptyListe.setVisibility(View.VISIBLE);
            recyclerFavoris.setVisibility(View.GONE);
        } else {
           // emptyListe.setVisibility(View.GONE);
            recyclerFavoris.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Ouvre le dialogue de partage photo (caméra / galerie).
     */
    private void dialogPartagePhoto() {
        if (dialog != null) dialog.dismiss();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogSlideAnim);
        View view = getLayoutInflater().inflate(R.layout.partage_photo, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);

        TextView takePhoto = view.findViewById(R.id.take_photo);
        TextView cancel    = view.findViewById(R.id.cancel);
        TextView loadPhoto = view.findViewById(R.id.load_photo);

        takePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuideActivity.class);
            intent.putExtra("GaleryOrCamera", 1);
            startActivity(intent);
            dialog.dismiss();
        });

        loadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuideActivity.class);
            intent.putExtra("GaleryOrCamera", 0);
            startActivity(intent);
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog = alertDialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        float density   = getResources().getDisplayMetrics().density;
        int paddingDp   = (int) (300 * density);
        dialog.getWindow().setLayout(paddingDp, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Navigue vers FilterArticleActivity pour une catégorie donnée.
     *
     * @param id identifiant de la catégorie sélectionnée
     */
    public void displayView(int id) {
        Categories categoriesSelected = getCategoriesFromId(categoriesList, id);
        if (categoriesSelected == null) return;

        categorie = categoriesSelected.getTitleCategories();

        final Intent intent = new Intent(this, FilterArticleActivity.class);
        Bundle bundle = new Bundle();
        List<News> filtered = listFilter(listSearch, categorie);
        bundle.putSerializable("initialList", (Serializable) filtered);
        intent.putExtras(bundle);
        intent.putExtra("categorie", (Serializable) categoriesSelected);
        intent.putExtra("type", type);

        handler.postDelayed(() -> {
            if (dialog != null) dialog.dismiss();
            startActivityForResult(intent, MainActivity.STARTER_CODE);
        }, 300);
    }

    // ────────────────────────────────────────────────
    //  Utilitaires
    // ────────────────────────────────────────────────

    private Categories getCategoriesFromId(List<Categories> list, int idCategories) {
        for (Categories cat : list) {
            if (Integer.parseInt(cat.getIdCategories()) == idCategories)
                return cat;
        }
        return null;
    }

    private List<News> listFilter(List<News> newses, String categorie) {
        List<News> filtered = new ArrayList<>();
        for (News news : newses) {
            if (news.getTypeNews() == null) continue;
            List<String> items = Arrays.asList(news.getTypeNews().split("\\s*,\\s*"));
            for (String cat : items) {
                if (cat.contains(categorie)) {
                    filtered.add(news);
                    break; // éviter les doublons
                }
            }
        }
        return filtered;
    }

    // ────────────────────────────────────────────────
    //  OnClickListener
    // ────────────────────────────────────────────────

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_guide) {
            dialogPartagePhoto();
        }
    }
}