package com.mdweb.tunnumerique.ui.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebSettings;
import android.webkit.WebView;


import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.shared.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleDetailActivity extends BaseActivity {

    // Vues du header
    private ImageView backButton;
    private ImageView logoTn;
    private TextView languageButton;

    // Vues du contenu
    private ImageView articleImage;
    private ImageView bookmarkButton;
    private ImageView shareButton;
    private TextView articleCategory;
    private TextView articleTitle;
    private TextView articleTime;
    private TextView msgAbonne;
    private BottomNavigationView bottomNavigationView;

    private WebView articleContentWebView;

    // ✅ Badge Premium
    private LinearLayout premiumBadgeContainer;

    // Lecteur audio
    private LinearLayout audioPlayerContainer;
    private ImageView playButton;
    private SeekBar audioSeekbar;
    private TextView audioDuration;

    // Données de l'article
    private News currentNews;
    private boolean isBookmarked = false;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        initViews();
        setupHeader();
        getArticleData();
        displayArticle();
        setupActions();
    }

    /**
     * Initialise toutes les vues
     */
    private void initViews() {
        // Header
        backButton = findViewById(R.id.back_button);
        logoTn = findViewById(R.id.logo_tn);
        languageButton = findViewById(R.id.language_button);

        // Contenu
        articleImage = findViewById(R.id.article_image);
        bookmarkButton = findViewById(R.id.bookmark_button);
        shareButton = findViewById(R.id.share_button);
        articleCategory = findViewById(R.id.article_category);
        articleTitle = findViewById(R.id.article_title);
        msgAbonne = findViewById(R.id.msg_abonne);
        articleTime = findViewById(R.id.article_time);
        articleContentWebView = findViewById(R.id.article_content_webview);

        // ✅ Badge Premium
        premiumBadgeContainer = findViewById(R.id.premium_badge_container);

        // Lecteur audio
        audioPlayerContainer = findViewById(R.id.audio_player_container);
        playButton = findViewById(R.id.play_button);
        audioSeekbar = findViewById(R.id.audio_seekbar);
        audioDuration = findViewById(R.id.audio_duration);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupHeader() {
        backButton.setOnClickListener(v -> finish());
        languageButton.setOnClickListener(v -> changeLanguage());
        updateLanguageButton();
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) return;
        bottomNavigationView.setSelectedItemId(R.id.nav_horaires);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ArticleDetailActivity.this, HomeTnActivity.class));
                return true;
            } else if (itemId == R.id.nav_horaires) {
                return true;
            } else if (itemId == R.id.nav_enregistres) {
                startActivity(new Intent(ArticleDetailActivity.this, FavorisActivity.class));
                return true;
            } else if (itemId == R.id.nav_top24) {
                startActivity(new Intent(ArticleDetailActivity.this, PlusLusActivity.class));
                return true;
            }
            return false;
        });
    }

    private void getArticleData() {
        Intent intent = getIntent();

        if (intent.hasExtra("news_object")) {
            currentNews = (News) intent.getSerializableExtra("news_object");
        } else {
            String title    = intent.getStringExtra("article_title");
            String imageUrl = intent.getStringExtra("article_image");
            String category = intent.getStringExtra("article_category");
            String content  = intent.getStringExtra("article_content");
            String dateStr  = intent.getStringExtra("article_date");

            currentNews = new News();
            currentNews.setTitleNews(title);
            currentNews.setImageUrlNews(imageUrl);
            currentNews.setTypeNews(category);
            currentNews.setDescriptionNews(content);
            currentNews.setDateNews(dateStr);
        }
    }

    private void displayArticle() {
        if (currentNews == null) {
            Toast.makeText(this, "Erreur: Article introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        applyCurrentLanguageDirection();

        // =========================================
        // ✅ GESTION PAYWALL (badge + catégorie)
        // =========================================
        if (currentNews.isPaywall()) {
            premiumBadgeContainer.setVisibility(View.VISIBLE);
            articleContentWebView.setVisibility(View.GONE);
            audioPlayerContainer.setVisibility(View.GONE);
            msgAbonne.setVisibility(View.VISIBLE);

            if (currentNews.getTypeNews() != null && !currentNews.getTypeNews().isEmpty()) {
                String[] categories = currentNews.getTypeNews().split(",");
                articleCategory.setText(categories[0].trim().toUpperCase());
            } else {
                articleCategory.setText("ACTUALITÉ");
            }

        } else {
            premiumBadgeContainer.setVisibility(View.GONE);
            articleContentWebView.setVisibility(View.VISIBLE);
            msgAbonne.setVisibility(View.GONE);

            if (currentNews.getTypeNews() != null && !currentNews.getTypeNews().isEmpty()) {
                String[] categories = currentNews.getTypeNews().split(",");
                articleCategory.setText(categories[0].trim().toUpperCase());
            } else {
                articleCategory.setText("ACTUALITÉ");
            }

            if (currentNews.getUrlAudioNews() != null && !currentNews.getUrlAudioNews().isEmpty()) {
                audioPlayerContainer.setVisibility(View.VISIBLE);
                setupAudioPlayer(currentNews.getUrlAudioNews());
            } else {
                audioPlayerContainer.setVisibility(View.GONE);
            }
        }

        // =========================================
        // Image de l'article
        // =========================================
        String imageUrl = currentNews.getImageUrlDetailsNews();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = currentNews.getImageUrlNews();
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(articleImage);
        }

        // =========================================
        // Titre
        // =========================================
        if (currentNews.getTitleNews() != null) {
            articleTitle.setText(currentNews.getTitleNews().toUpperCase(Locale.getDefault()));
        }

        // =========================================
        // Temps de publication
        // =========================================
        if (currentNews.getDateNews() != null) {
            String author = "Tunisie Numérique";
            if (currentNews.getAuthorNameNews() != null && !currentNews.getAuthorNameNews().trim().isEmpty()) {
                author = currentNews.getAuthorNameNews().trim();
            }
            String formattedDate = formatPublicationTime(currentNews.getDateNews());
            String row = "Par " + author + " | " + formattedDate;
            SpannableString styled = new SpannableString(row);
            int dateStart = row.lastIndexOf(formattedDate);
            if (dateStart >= 0) {
                styled.setSpan(new ForegroundColorSpan(0xFF88BD2E), dateStart, row.length(), 0);
            }
            articleTime.setText(styled);
        }

        // =========================================
        // Contenu HTML
        // =========================================
        configureContentWebView();
        String contentHtml = currentNews.getContenuNews();
        if (contentHtml == null || contentHtml.isEmpty()) {
            contentHtml = currentNews.getDescriptionNews();
        }
        if (contentHtml != null && !contentHtml.isEmpty()) {
            articleContentWebView.loadDataWithBaseURL(
                    "https://www.tunisienumerique.com",
                    wrapArticleHtml(contentHtml),
                    "text/html",
                    "UTF-8",
                    null
            );
        }
    }

    private void configureContentWebView() {
        WebSettings settings = articleContentWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        articleContentWebView.setVerticalScrollBarEnabled(false);
        articleContentWebView.setHorizontalScrollBarEnabled(false);
    }

    private String wrapArticleHtml(String bodyHtml) {
        boolean isArabic = Constant.AR.equals(SessionManager.getInstance().getCurrentLang(this));
        String direction = isArabic ? "rtl" : "ltr";
        String align = isArabic ? "right" : "left";
        return "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0' />"
                + "<style>"
                + "body{margin:0;padding:0;color:#212121;font-size:11px;line-height:16px;font-family:sans-serif;"
                + "direction:" + direction + ";text-align:" + align + ";}"
                + (isArabic ? "*{direction:rtl !important;text-align:right !important;unicode-bidi:embed;}" : "")
                + "img,iframe,video{max-width:100%;height:auto;}"
                + "table{max-width:100%!important;}"
                + "</style></head><body dir='" + direction + "'>"
                + bodyHtml
                + "</body></html>";
    }

    private void applyCurrentLanguageDirection() {
        boolean isArabic = Constant.AR.equals(SessionManager.getInstance().getCurrentLang(this));

        if (isArabic) {
            articleContentWebView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            articleContentWebView.setTextDirection(View.TEXT_DIRECTION_RTL);
            articleTitle.setGravity(Gravity.END);
            articleTitle.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            articleCategory.setGravity(Gravity.END);
            articleCategory.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            articleTime.setGravity(Gravity.END);
            articleTime.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            msgAbonne.setGravity(Gravity.END);
            msgAbonne.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            articleContentWebView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            articleContentWebView.setTextDirection(View.TEXT_DIRECTION_LTR);
            articleTitle.setGravity(Gravity.START);
            articleTitle.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            articleCategory.setGravity(Gravity.START);
            articleCategory.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            articleTime.setGravity(Gravity.START);
            articleTime.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            msgAbonne.setGravity(Gravity.START);
            msgAbonne.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
    }

    private String formatPublicationTime(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date articleDate = sdf.parse(dateStr);
            Date now = new Date();

            long diffMs      = now.getTime() - articleDate.getTime();
            long diffMinutes = diffMs / (60 * 1000);
            long diffHours   = diffMs / (60 * 60 * 1000);
            long diffDays    = diffMs / (24 * 60 * 60 * 1000);

            if (diffMinutes < 60) {
                return "Il y a " + diffMinutes + " minutes";
            } else if (diffHours < 24) {
                return "Il y a " + diffHours + " heures";
            } else if (diffDays < 7) {
                return "Il y a " + diffDays + " jours";
            } else {
                return new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(articleDate);
            }
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * ✅ Configure les actions (bookmark, share, msgAbonne)
     */
    private void setupActions() {
        bookmarkButton.setOnClickListener(v -> toggleBookmark());
        shareButton.setOnClickListener(v -> shareArticle());

        // ✅ Click sur msgAbonne -> ouvre l'URL dans le navigateur
        msgAbonne.setOnClickListener(v -> {
            String url = currentNews.getShareUrlNews();
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void toggleBookmark() {
        isBookmarked = !isBookmarked;

        if (isBookmarked) {
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_filled);
            Toast.makeText(this, "Article sauvegardé", Toast.LENGTH_SHORT).show();
        } else {
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_outline);
            Toast.makeText(this, "Article retiré", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareArticle() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String shareText = currentNews.getTitleNews() + "\n\n" +
                currentNews.getDescriptionNews();

        if (currentNews.getShareUrlNews() != null && !currentNews.getShareUrlNews().isEmpty()) {
            shareText += "\n\nLire plus: " + currentNews.getShareUrlNews();
        }

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }

    private void setupAudioPlayer(String audioUrl) {
        playButton.setOnClickListener(v -> toggleAudioPlayback());
    }

    private void toggleAudioPlayback() {
        isPlaying = !isPlaying;

        if (isPlaying) {
            playButton.setImageResource(R.drawable.ic_pause);
        } else {
            playButton.setImageResource(R.drawable.ic_play);
        }
    }

    private void changeLanguage() {
        Intent intent = new Intent(ArticleDetailActivity.this, LangueActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguageButton();
    }
}
