package com.mdweb.tunnumerique.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Blague;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.tools.style.CustomViewPager;
import com.mdweb.tunnumerique.ui.adapters.ViewPagerAdapter;
import com.mdweb.tunnumerique.ui.fragments.BlaguesDetailsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailsBlagueActivity extends BaseActivity implements ViewPager.OnPageChangeListener, CustomViewPager.OnSwipeOutListener {

//    String contenu = "";
//    String note = "0";
//    private String blagueTittle;
//    private String urlShare;

    private ViewPagerAdapter pagerAdapter;
    ArrayList<Blague> blagues;

    private int position;
    CustomViewPager viewPager;
    private List<Blague> blagueList;

    private Utils utils;
    private ImageView lngIcon;
    private String currentLng;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blague);
        blagueList = new ArrayList<>();
        utils = new Utils(this);
        blagues = (ArrayList<Blague>) new DataParser().getBlagueList(utils.getStringFromFile(Communication.FILE_NAME_BLAGUES));
        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        ImageView imageBack = (ImageView) findViewById(R.id.retour);
        lngIcon = (ImageView) findViewById(R.id.lng_icon);
        setIconLang();
        lngIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpLng();
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //  get intent
        Intent intent = getIntent();
//        type = (int) intent.getExtras().getInt("type");
//        cat = intent.getExtras().getString("cat");
        position = (int) intent.getExtras().getInt("position");
        setupViewPager(viewPager);
        viewPager.setOnSwipeOutListener(this);
//        WebView blagueTxt = findViewById(R.id.blague);
//        TextView noteTxt = findViewById(R.id.note);
//        contenu = getIntent().getStringExtra("contenu");
//        note = getIntent().getStringExtra("note");
//        blagueTittle = getIntent().getStringExtra("tittle");
//        urlShare = getIntent().getStringExtra("url");
//        RelativeLayout imageBack = (RelativeLayout) findViewById(R.id.back_image);
//        LinearLayout shareBtn = (LinearLayout) findViewById(R.id.shareBtn);
//        imageBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShareSN shareSN = new ShareSN(DetailsBlagueActivity.this);
//                shareSN.share(urlShare, blagueTittle);
//
//            }
//        });
//        blagueTxt.loadDataWithBaseURL("https://www.tunisienumerique.com", contenu, "text/html", "UTF-8", null);
//
//        noteTxt.setText(note);
    }

    private void setupViewPager(ViewPager viewPager) {
        //after adding all the fragments write the below lines
        pagerAdapter = new ViewPagerAdapter(super.getSupportFragmentManager());
        // test if we have connectivity


        for (int i = 0; i < blagues.size(); i++) {
            pagerAdapter.addFrag(BlaguesDetailsFragment.newInstance(blagues.get(i)));
        }
        // we set the adpater to the viewPager
        viewPager.setAdapter(pagerAdapter);

        Log.d("Position", "Test" + position);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(this);


    }

    @Override
    protected void onResume() {
        SessionManager.getInstance().activity_onTop(DetailsBlagueActivity.this, DetailsBlagueActivity.this.getClass().getName());

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // SessionManager.getInstance().activity_onTop(getApplicationContext(), "");
        Log.d("LaunchUrlA", DetailsBlagueActivity.this.getClass().getName() + "Destroy");

        // SessionManager.getInstance().activity_onTop(getApplicationContext(), "");

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("LaunchUrlA", DetailsBlagueActivity.this.getClass().getName() + "Pause");

        SessionManager.getInstance().activity_onTop(getApplicationContext(), "");

        super.onPause();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Application.getInstance().trackScreenView(this, blagues.get(position).getTitleBlague());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSwipeOutAtStart() {

    }

    @Override
    public void onSwipeOutAtEnd() {

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
                btnAr.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng));
                btnFr.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.black_color));
                enTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.text_color_off));
                currentLng = Constant.AR;

            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentLng.equals(SessionManager.getInstance().getCurrentLang(DetailsBlagueActivity.this))) {
                    SessionManager.getInstance().setCurrentLng(DetailsBlagueActivity.this, currentLng);
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
            btnAr.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng));
            btnEn.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng_off));
            arTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.text_color_off));
            frTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.black_color));
            currentLng = Constant.FR;
        });

        btnEn.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng_off));
            btnEn.setBackground(ContextCompat.getDrawable(DetailsBlagueActivity.this, R.drawable.bg_selected_lng));
            arTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.black_color));
            frTxt.setTextColor(ContextCompat.getColor(DetailsBlagueActivity.this, R.color.text_color_off));
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
