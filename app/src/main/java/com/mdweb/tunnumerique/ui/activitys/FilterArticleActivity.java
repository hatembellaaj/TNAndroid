package com.mdweb.tunnumerique.ui.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Categories;
import com.mdweb.tunnumerique.data.model.ItemMenu;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.adapters.ArticleAdapter;
import com.mdweb.tunnumerique.ui.adapters.ExpandableListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Represents a FilterArticle Activity  .
 *
 * @author SOFIENE ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @version 1.0
 * @since 03-05-2017
 */
public class FilterArticleActivity extends BaseActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private int type;
    private ArticleAdapter articleAdapter;
    private Dialog dialog;
    private List<News> newsList;
    private List<News> listSearch;
    public ExpandableListAdapter expandableListAdapter;
    public RecyclerView recyclerview;
    public boolean isFilter = false;
    public LinearLayoutManager linearLayoutManager;
    private TextView titleToolbar;
    private TextView textButtonFilter;
    public ImageView bgCategrie;
    public ImageView bgTextCategrie;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Drawable drawable;
    private MenuItem item;
    private EditText editText;
    private AppBarLayout appbar;
    private String categorie;
    // public FloatingActionButton floatingActionButton;
    private List<Categories> categoriesList;
    private LinearLayout emptyListe;
    private RecyclerView recyclerNews;
    private List<News> initialList;
    private Categories categorieFilter;
    private RelativeLayout back;
    private Toolbar toolbar;
    private Handler handler;

    private ImageView lngIcon;
    private String currentLng;
    private BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_filter_article);
        // change color toolbar when collapsing

        Bundle bundle = getIntent().getExtras();
        initialList = (ArrayList<News>) new DataParser().getListNewsFromLocal(new Utils(getBaseContext()).getStringFromFile("MonfileFilter"));
        // initialList = (List<News>) bundle.getSerializable("initialList");
        Intent intent = getIntent();
        categorieFilter = (Categories) intent.getExtras().getSerializable("categorie");
        type = (int) intent.getExtras().getSerializable("type");
        // initialize views
        initView();

    }


    /**
     * initialize the views
     */
    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        back = (RelativeLayout) findViewById(R.id.back_image);
        back.setOnClickListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        emptyListe = (LinearLayout) findViewById(R.id.empty_liste);
        // get categories list from local json file

        String lng = SessionManager.getInstance().getCurrentLang(this);
        if (lng.equals(Constant.AR)) {
            categoriesList = new DataParser().getListCategories(new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES_AR),0);
        } else if (lng.equals(Constant.EN)) {
            categoriesList = new DataParser().getListCategories(new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES_EN),0);
        } else {
            categoriesList = new DataParser().getListCategories(new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES),1);

        }
        // initialize views
        recyclerNews = (RecyclerView) findViewById(R.id.recycler_news);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerNews.setLayoutManager(linearLayoutManager);
        // floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_guide);
        //  floatingActionButton.setVisibility(View.GONE);
//        floatingActionButton.setOnClickListener(this);
        newsList = new ArrayList<>();
        listSearch = new ArrayList<News>();
        articleAdapter = new ArticleAdapter(this, false, type, categorieFilter.getTitleCategories());
        articleAdapter.setLinearLayoutManager(linearLayoutManager);
        recyclerNews.setAdapter(articleAdapter);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        changeColorToolbar();
        titleToolbar = (TextView) findViewById(R.id.titleToolbar);
        lngIcon = findViewById(R.id.lng_icon);
        setIconLang();
        lngIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpLng();
            }
        });
        textButtonFilter = (TextView) findViewById(R.id.text_button_filter);
        bgCategrie = (ImageView) findViewById(R.id.bg_categorie);
        bgTextCategrie = (ImageView) findViewById(R.id.image_bt_filter);
        // set aniation to floatingActionButton onScrolled
//        floatingActionButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_to_bottom));
//        floatingActionButton.setAnimation(AnimationUtils.loadAnimation(this, R.anim.show_from_bottom));
//        //  scrollOffset when hide/show fab in scroll recyclerView
//        final int mScrollOffset = 4;
//        recyclerNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                //  if (newsList.size() > 1)
//                if (Math.abs(dy) > mScrollOffset) {
//                    if (dy > 0) {
//                        // hide floating
//                        floatingActionButton.hide();
//                    } else {
//                        //show floating
//                        floatingActionButton.show();
//                    }
//                }
//            }
//        });
        //
        changeViewFilter(categorieFilter, 0);
    }


    public void changeColorIcon() {
        // change color menu
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        // set icon to search view
        item.setIcon(R.drawable.ic_white_search);
        // set text color to searchView
        int color = Color.WHITE;
        // set color hint text search view
        editText.setHintTextColor(color);
        // set color text search view
        editText.setTextColor(color);
        // change color icon drawer
        changeDrawerColor(R.color.white_color);

    }

    /**
     * Change color icon drawer of toolbar
     *
     * @param color the color of icon drawer
     */
    public void changeDrawerColor(int color) {
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(ContextCompat.getColor(this, color), PorterDuff.Mode.SRC_ATOP);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View v = toolbar.getChildAt(i);
            if (v instanceof ImageButton) {
                ((ImageButton) v).setColorFilter(colorFilter);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_categeries:
                // search action
                dialogFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        List<News> filteredNewsList = new ArrayList<>();
        if (isFilter) {
            filteredNewsList = filter(listFilter(listSearch, categorie), newText);
        } else {
            filteredNewsList = filter(listSearch, newText);
        }
        // set list article to adapter
        articleAdapter.setFilter(filteredNewsList);
        // set default text when empty list
        if (filteredNewsList.size() == 0) {
            emptyListe.setVisibility(View.VISIBLE);
            recyclerNews.setVisibility(View.GONE);
        } else {
            emptyListe.setVisibility(View.GONE);
            recyclerNews.setVisibility(View.VISIBLE);
        }

        return true;
    }


    /**
     * show dialog for filter
     */
    @SuppressLint("WrongConstant")
    private void dialogFilter() {
        //dismiss dialog
        dialog = new Dialog(FilterArticleActivity.this, R.style.DialogSlideAnimFilter);
        View view = (this).getLayoutInflater().inflate(R.layout.dialog_filter, null);
        dialog.setContentView(view);

        LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        recyclerview = (RecyclerView) view.findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
        //disable over scroll effect of recyclerView
        recyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //
        expandableListAdapter = new ExpandableListAdapter(getApplication(), getItemMenu(), this);
        recyclerview.setAdapter(expandableListAdapter);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    /**
     * @param newses list article
     * @return list article
     */

    private List<News> filter(List<News> newses, String constraint) {

        final List<News> filtereNewslList = new ArrayList<>();
        if (constraint.length() == 0) {
            ///if no key word taped in searchView reset initial list of news
            filtereNewslList.addAll(newses);
        } else {
            // add item publicity
            filtereNewslList.add(new News(Constant.isPublicity));
            for (int i = 0; i < newses.size(); i++) {
                News news = newses.get(i);
                final String filterPattern = constraint.toString().toLowerCase();

                //////////////////////////////////
                String keyWord = news.getKeyWordsNews();
                String[] parts = keyWord.split(",");
                //create list of string to contain a list of key word
                List<String> keyWordList = new ArrayList<>();
                for (int j = 0; j < parts.length; j++) {
                    keyWordList.add(parts[j].toString().toLowerCase());
                }
                news.setNewsKeyWords(keyWordList);
                /////////////////////////

                ///////
                for (String key : keyWordList) {
                    //search wich news contain key word in her list of key word
                    if (key.contains(filterPattern) && !new Utils().containNews(filtereNewslList, news.getIdNews())) {
                        //if key is retreived in news add it to filtered list
                        filtereNewslList.add(news);
                    }
                }

            }
        }
        return filtereNewslList;
    }

    /**
     * create men for filter
     *
     * @return return list item menu
     */
    public List<ItemMenu> getItemMenu() {
        List<ItemMenu> data = new ArrayList<>();
        for (int i = 0; i < categoriesList.size(); i++) {
            Categories categories = categoriesList.get(i);
            data.add(new ItemMenu(categories.getTitleCategories(), categories.getIconUrl(), categories.getIconEnabledUrl(), Integer.parseInt(categories.getIdCategories())));
        }
        return data;
    }


    /**
     * Display item filter in fragment
     *
     * @param id id item
     */
    public void displayView(int id) {
        // expanded appbar
        appbar.setExpanded(true, true);
        isFilter = true;
        //display view by id
        List<Categories> categoriesList1 = new DataParser().getListCategories(new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES),1);
        String lng = SessionManager.getInstance().getCurrentLang(this);
        if (lng.equals(Constant.AR)) {
            categoriesList1 = new DataParser().getListCategories(new Utils(this).getStringFromFile(Communication.
                    FILE_NAME_CATEGORIES_AR),0);
        } else if (lng.equals(Constant.EN)) {
            categoriesList1 = new DataParser().getListCategories(new Utils(this).getStringFromFile(Communication.FILE_NAME_CATEGORIES_EN),0);
        }
        Categories categoriesSelected = getCateoriesFromId(categoriesList1, id);
        categorie = categoriesSelected.getTitleCategories();
        changeViewFilter(categoriesSelected, 1);

        articleAdapter.addAll(initialList);
        // set empty text while list size equal zero
        if (initialList.size() == 0) {
            emptyListe.setVisibility(View.VISIBLE);
            recyclerNews.setVisibility(View.GONE);

        } else {
            emptyListe.setVisibility(View.GONE);
            recyclerNews.setVisibility(View.VISIBLE);

        }

    }

    /**
     * @param categories
     * @param idCategories
     * @return
     */
    private Categories getCateoriesFromId(List<Categories> categories, int idCategories) {
        for (int i = 0; i < categories.size(); i++) {
            if (Integer.parseInt(categories.get(i).getIdCategories()) == idCategories)
                return categories.get(i);
        }
        return null;
    }

    /**
     * change view when click item filter
     */
    public void changeViewFilter(Categories categoriesSelected, int x) {


        titleToolbar.setText(categoriesSelected.getTitleCategories());
        // set text button filter
        textButtonFilter.setText(categoriesSelected.getTitleCategories());
        // get image name
        String imageName = getImageName(categoriesSelected.getBgImageFilterName());
        //get image from drawable
        Context context = bgCategrie.getContext();
        int id = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        if (id != 0)
            bgCategrie.setImageResource(id);
        else
            ImageLoader.getInstance().displayImage(categoriesSelected.getBgImageFilterUrl(), bgCategrie, new Utils(this).getImageLoaderOptionTheme());
        int rgb = Color.rgb(categoriesSelected.getColorR(), categoriesSelected.getColorG(), categoriesSelected.getColorB());
        //  create the color values in view
        bgTextCategrie.getDrawable().setColorFilter(rgb, PorterDuff.Mode.MULTIPLY);

        if (x == 1) {
            // dismiss ialog filter

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 300);
        }

    }

    public String getImageName(String image) {
        String[] parts = image.split("\\.");
        return parts[0];
    }

    @Override
    public void onResume() {
        SessionManager.getInstance().activity_onTop(FilterArticleActivity.this, FilterArticleActivity.this.getClass().getName());

        super.onResume();


    }

    @Override
    protected void onDestroy() {
        //   SessionManager.getInstance().activity_onTop(getApplicationContext(), "");

        Log.d("LaunchUrlA", FilterArticleActivity.this.getClass().getName() + "destroy");
        super.onDestroy();
    }

    /**
     * change color toolbar, status bar when  collapse toolbar is collapsed
     */
    public void changeColorToolbar() {
        // get color
        int color = ContextCompat.getColor(this, R.color.green_color);
        // set color to toolbar
        collapsingToolbarLayout.setContentScrimColor(color);
        /// set color to status bar
        collapsingToolbarLayout.setStatusBarScrimColor(color);
        supportStartPostponedEnterTransition();
    }


    @Override
    public void onStart() {
        super.onStart();
        // load data from json on start
        loadData();

    }

    /**
     * Load data from json file
     */
    private void loadData() {
        // clear list news
        newsList.clear();
        newsList.addAll(initialList);
        articleAdapter.addAll(newsList);
        listSearch.clear();
        listSearch.addAll(newsList);
        if (initialList.size() == 0) {
            emptyListe.setVisibility(View.VISIBLE);
            recyclerNews.setVisibility(View.GONE);

        } else {
            emptyListe.setVisibility(View.GONE);
            recyclerNews.setVisibility(View.VISIBLE);

        }


    }

    /**
     * return list news by category
     *
     * @param newses    input list
     * @param categorie category article
     * @return list news by category
     */
    private List<News> listFilter(List<News> newses, String categorie) {
        Log.e("testFilter","Enter" );
        final List<News> filtereNewslList = new ArrayList<>();
        for (News news : newses) {
            List<String> items = new ArrayList<>();
            Log.e("testFilter","Enter" );
            if (news.getTypeNews() != null) {
                Log.e("testFilter",news.getTypeNews() );
                items = Arrays.asList(news.getTypeNews().split("\\s*,\\s*"));
            }

            for (String cat : items) {
                if (cat.contains(categorie))
                    filtereNewslList.add(news);
            }
        }

        return filtereNewslList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.fab_guide:
//                // open guide
//                dialogPartagePhoto();
////                Intent intent = new Intent(getContext(), UploadActivity.class);
////                startActivity(intent);
//                break;
            case R.id.back_image:
                finish();
                break;
        }

    }

    /**
     * dialog upload photo
     */
    public void dialogPartagePhoto() {
        //dismiss dialog
        if (dialog != null)
            dialog.dismiss();
        //cerate alert builder dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogSlideAnim);
        View view = (this).getLayoutInflater().inflate(R.layout.partage_photo, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        TextView takePhoto = (TextView) view.findViewById(R.id.take_photo);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView loadPhoto = (TextView) view.findViewById(R.id.load_photo);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterArticleActivity.this, GuideActivity.class);
                intent.putExtra("GaleryOrCamera", 1);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        loadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterArticleActivity.this, GuideActivity.class);
                intent.putExtra("GaleryOrCamera", 0);
                startActivity(intent);
                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog = alertDialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int paddingPixel = 300;
        float density = getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        //fix size of dialog
        dialog.getWindow().setLayout(paddingDp,
                WindowManager.LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void onPause() {
        Log.d("LaunchUrlA", FilterArticleActivity.this.getClass().getName() + "Pause");

        SessionManager.getInstance().activity_onTop(getApplicationContext(), "");

        super.onPause();
    }
    public void setIconLang() {
        switch (SessionManager.getInstance().getCurrentLang(this)) {
            case Constant.AR: {
                lngIcon.setImageResource(R.drawable.icon_ar_det);
            }
            break;
            case Constant.EN: {
                lngIcon.setImageResource(R.drawable.icon_en_det);

            }
            break;
            default: {
                lngIcon.setImageResource(R.drawable.icon_fr_det);

            }
        }

    }

    public void showPopUpLng() {
        Log.e("DisplayedPOP", getString(R.string.actualite));

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        TextView btnAr = view.findViewById(R.id.btnar);
        TextView btnFr = view.findViewById(R.id.btnfr);
        TextView btnEn = view.findViewById(R.id.btnEn);
        TextView arTxt = view.findViewById(R.id.ar);
        TextView enTxt = view.findViewById(R.id.en);
        TextView frTxt = view.findViewById(R.id.fr);
        Button btnValidate = view.findViewById(R.id.validateBtn);
        Typeface typoAr = Typeface.createFromAsset(getAssets(), "fonts/din-next_-ar-regular.otf");
        Typeface typoFr = Typeface.createFromAsset(getAssets(), "Roboto/Roboto-Regular.ttf");
        btnAr.setTypeface(typoAr);
        btnEn.setTypeface(typoFr);
        btnFr.setTypeface(typoFr);
        arTxt.setTypeface(typoAr);
        enTxt.setTypeface(typoFr);
        frTxt.setTypeface(typoFr);
        currentLng = SessionManager.getInstance().getCurrentLang(this);

        switch (currentLng) {
            case "ar": {
                btnAr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng));
                btnFr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(this, R.color.black_color));
                enTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                btnValidate.setTypeface(typoAr);

            }
            break;
            case "en": {
                btnAr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnFr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng));
                arTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                enTxt.setTextColor(ContextCompat.getColor(this, R.color.black_color));
                frTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                btnValidate.setTypeface(typoFr);

            }
            break;
            default: {
                btnValidate.setTypeface(typoFr);

                btnAr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnFr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng));
                btnEn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                enTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(this, R.color.black_color));


            }
            break;
        }


        btnAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAr.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng));
                btnFr.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.black_color));
                enTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.text_color_off));
                currentLng = Constant.AR;

            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentLng.equals(SessionManager.getInstance().getCurrentLang(FilterArticleActivity.this))) {
                    SessionManager.getInstance().setCurrentLng(FilterArticleActivity.this, currentLng);
                    setIconLang();
                    Locale locale = new Locale(currentLng);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
//                    requireActivity().getBaseContext().getResources().updateConfiguration(config,
//                            requireActivity().getBaseContext().getResources().getDisplayMetrics());
//                    //   LocaleHelper.setLocale(requireActivity(), "ar");

                    onConfigurationChanged(config);
                }
                bottomSheetDialog.dismiss();
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });
        btnFr.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng));
            btnEn.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng_off));
            arTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.text_color_off));
            frTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.black_color));
            currentLng = Constant.FR;
        });

        btnEn.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng_off));
            btnEn.setBackground(ContextCompat.getDrawable(FilterArticleActivity.this, R.drawable.bg_selected_lng));
            arTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.black_color));
            frTxt.setTextColor(ContextCompat.getColor(FilterArticleActivity.this, R.color.text_color_off));
            currentLng = Constant.EN;

        });
//
//        TextView message_poup = view.findViewById(R.id.message_poup);
//        message_poup.setVisibility(View.VISIBLE);
//        message_poup.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/SamsungSans-Regular.ttf"));
//        message_poup.setTextColor(ContextCompat.getColor(
//                this,
//                R.color.gris_text
//        ));
//
//        message_poup.setText(showedText);

        bottomSheetDialog.setOnCancelListener(dialog -> {
            //   dialogue_open = 0;

        });

        bottomSheetDialog.show();
//
//        if (dialogue_open < 1) {
//            dialogue_open = 1;
//            dialog.show();
//        }
    }
}
