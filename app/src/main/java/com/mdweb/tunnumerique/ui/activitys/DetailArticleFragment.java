package com.mdweb.tunnumerique.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.tools.style.CustomViewPager;
import com.mdweb.tunnumerique.ui.adapters.ViewPagerAdapter;
import com.mdweb.tunnumerique.ui.fragments.ArticleDetailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DetailArticleFragment extends BaseActivity implements ViewPager.OnPageChangeListener, CustomViewPager.OnSwipeOutListener {

    private ViewPagerAdapter pagerAdapter;
    ArrayList<News> news;
    private TextView titleToolbar;
    private int type;
    private String cat;
    private int position;
    CustomViewPager viewPager;
    private List<News> newsListArticle;



    private Utils utils;

    private ArticleDetailFragment currentDetailsF;
    private ImageView lngIcon;
    private String currentLng;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article_fragment);
        newsListArticle = new ArrayList<>();
        utils = new Utils(this);
        news = (ArrayList<News>) new DataParser().getListNewsFromLocal(new Utils(getBaseContext()).getStringFromFile("Monfile"));
        titleToolbar = (TextView) findViewById(R.id.titleToolbar);
        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        ImageView imageBack = findViewById(R.id.retour);
        lngIcon = findViewById(R.id.lng_icon);
        setIconLang();

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lngIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpLng();
            }
        });

        //  get intent
        Intent intent = getIntent();
        type = (int) intent.getExtras().getInt("type");
        cat = intent.getExtras().getString("cat");
        position = (int) intent.getExtras().getInt("position");
        setToolbarTitle(type);
        setupViewPager(viewPager);
        viewPager.setOnSwipeOutListener(this);

    }

    @Override
    protected void onResume() {
        SessionManager.getInstance().activity_onTop(DetailArticleFragment.this, DetailArticleFragment.this.getClass().getName());
        Log.d("LaunchUrlA", DetailArticleFragment.this.getClass().getName() + "create");

        utils.checkSurvey();
        super.onResume();
    }


    private void setupViewPager(ViewPager viewPager) {
        //after adding all the fragments write the below lines
        pagerAdapter = new ViewPagerAdapter(super.getSupportFragmentManager());
        // test if we have connectivity
        if (news.get(0).getArtOrPubOrVid() == 0) {
            currentDetailsF = ArticleDetailFragment.newInstance(news.get(0));

            pagerAdapter.addFrag(currentDetailsF);
        }

        for (int i = 0; i < news.size() - 1; i++) {
            pagerAdapter.addFrag(ArticleDetailFragment.newInstance(news.get(i + 1)));
        }
        // we set the adpater to the viewPager
        viewPager.setAdapter(pagerAdapter);
        if (news.get(0).getArtOrPubOrVid() == 0) {
            viewPager.setCurrentItem(position + 1);
        } else
            viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(this);


    }

    private void setToolbarTitle(int type) {
        switch (type) {
            case Constant.idArticle:
                if (cat != null) {
                    titleToolbar.setText(cat);
                } else
                    titleToolbar.setText(getResources().getString(R.string.actualite_detail));
                break;

            case Constant.idDossier:
                if (cat != null) {
                    titleToolbar.setText(cat);
                } else
                    titleToolbar.setText(getResources().getString(R.string.dossiers));
                break;

            case Constant.idPlusLus:
                if (cat != null) {
                    titleToolbar.setText(cat);
                } else
                    titleToolbar.setText(getResources().getString(R.string.les_plus_lus));
                break;

            case Constant.idVideo:
                titleToolbar.setText(getResources().getString(R.string.videos));
                break;

            case Constant.idFavorite:
                titleToolbar.setText(getResources().getString(R.string.ma_liste));
                newsListArticle.addAll(FavorisDataBase.getInstance(getBaseContext()).getAllNews(SessionManager.getInstance().getCurrentLang(getApplicationContext())));

                News unNews;
                for (int i = 0; i < newsListArticle.size(); i++) {
                    unNews = newsListArticle.get(i);
                    if (unNews.getArtOrPubOrVid() != Constant.isVideo)
                        news.add(unNews);
                }


                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Application.getInstance().trackScreenView(this, news.get(position).getTitleNews());


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onPause() {
        Log.d("LaunchUrlA", DetailArticleFragment.this.getClass().getName() + " Pause");
        SessionManager.getInstance().activity_onTop(getApplicationContext(), "");

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("LaunchUrlA", DetailArticleFragment.this.getClass().getName() + "destroy");

        super.onDestroy();

    }

    @Override
    public void onSwipeOutAtStart() {
        finish();
    }

    @Override
    public void onSwipeOutAtEnd() {
        finish();
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
                btnAr.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng));
                btnFr.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.black_color));
                enTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.text_color_off));
                currentLng = Constant.AR;

            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentLng.equals(SessionManager.getInstance().getCurrentLang(DetailArticleFragment.this))) {
                    SessionManager.getInstance().setCurrentLng(DetailArticleFragment.this, currentLng);
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
            btnAr.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng));
            btnEn.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng_off));
            arTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.text_color_off));
            frTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.black_color));
            currentLng = Constant.FR;
        });

        btnEn.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng_off));
            btnEn.setBackground(ContextCompat.getDrawable(DetailArticleFragment.this, R.drawable.bg_selected_lng));
            arTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.black_color));
            frTxt.setTextColor(ContextCompat.getColor(DetailArticleFragment.this, R.color.text_color_off));
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
