package com.mdweb.tunnumerique.ui.activitys;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.fragments.ActualiteFragment;
import com.mdweb.tunnumerique.ui.fragments.AproposFragment;
import com.mdweb.tunnumerique.ui.fragments.BlagueFragment;
import com.mdweb.tunnumerique.ui.fragments.FavoriFragment;
import com.mdweb.tunnumerique.ui.fragments.HomeFragment;
import com.mdweb.tunnumerique.ui.fragments.HoraireFragment;
import com.mdweb.tunnumerique.ui.fragments.HorairePriereFragment;
import com.mdweb.tunnumerique.ui.fragments.ParametreFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a Main Activity.
 *
 * @author SOFIENE ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @author Karim Mahari
 * @version 1.0
 * @since 03-05-2017
 */

public class MainActivity extends BaseActivity
        implements View.OnClickListener {


    private Fragment fragment = null;
    private Runnable runnable;
    public TextView titleToolbar;

    public static int STARTER_CODE = 325;

    public Toolbar toolbar;
    public boolean drawerOpened = false;
    public CoordinatorLayout coordinator;

    boolean fromMain = false;
//    private Intent intentService;


    private BottomSheetDialog bottomSheetDialog;

    private boolean isActuality = true;
    private DrawerLayout drawer = null;
    private LinearLayout actualites = null;
    private LinearLayout videos = null;
    private LinearLayout blagues = null;
    private LinearLayout plusLus = null;
    private LinearLayout dossiers = null;
    private LinearLayout mesFavoris = null;
    private LinearLayout horiaresPriere = null;
    private LinearLayout aPropos = null;
    private LinearLayout paremtre = null;
    private ActionBarDrawerToggle toggle = null;
    private String openURL = null;
    private LinearLayout frameLayout = null;
    private Snackbar bar = null;
    private Intent intent = null;
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder alertDialogBuilder = null;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction1 = null;
    private FragmentTransaction fragmentTransaction = null;
    ImageView lngIcon;
    String currentLng;
    //Date Time
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        utils = new Utils(this);
        if (!utils.getNetworkState()) {
            frameLayout = (LinearLayout) findViewById(R.id.content_main);
            bar = Snackbar.make(frameLayout, R.string.connexion, Snackbar.LENGTH_LONG);
            bar.show();
        }

        initView();
        openURL = getIntent().getStringExtra("openURL");
        //Load the notif  in browser
        if (openURL != null && !openURL.equals("")) {
            intent = new Intent(this, NotificationActivity.class);
            intent.putExtra("openURL", openURL);
            startActivity(intent);
        }
        showScreen();

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


    /**
     * initialize view
     */
    public void initView() {

        // initialize views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actualites = (LinearLayout) findViewById(R.id.actualite);
        videos = findViewById(R.id.videos);
        blagues = findViewById(R.id.blague);
        plusLus = findViewById(R.id.plus_lus);
        dossiers = findViewById(R.id.dossiers);
        mesFavoris = findViewById(R.id.mes_favoris);
        horiaresPriere = findViewById(R.id.horaire_prieres);
        aPropos = findViewById(R.id.a_propos);
        paremtre = findViewById(R.id.parametre);
        titleToolbar = findViewById(R.id.titleToolbar);

        lngIcon = findViewById(R.id.lng_icon);
        setIconLang();
        lngIcon.setOnClickListener(v -> showPopUpLng());
        String firstLaunch = SessionManager.getInstance().getFirstLaunch(this);
        coordinator = (CoordinatorLayout) findViewById(R.id.cordinator_main);
        // listener of click menu item
        titleToolbar.setTextColor(ContextCompat.getColor(this, R.color.green_color_tittle));
        // set color icon Drawer
        changeDrawerColor(R.color.green_color_tittle);
        actualites.setOnClickListener(this);
        videos.setOnClickListener(this);
        blagues.setOnClickListener(this);
        mesFavoris.setOnClickListener(this);
        plusLus.setOnClickListener(this);
        dossiers.setOnClickListener(this);
        horiaresPriere.setOnClickListener(this);
        aPropos.setOnClickListener(this);
        paremtre.setOnClickListener(this);
        TextView privacy = (TextView) findViewById(R.id.privacy);
//SpannableString content = new SpannableString(privacy.getText());
//content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        privacy.setMovementMethod(LinkMovementMethod.getInstance());
//privacy.setTextColor(getResources().getColor(R.color.red_color));
//privacy.setText(content);



//        // open guide when first start
//        if (!SessionManager.getInstance().isSavedGuideInPreferences(this)) {
//            showDialog();
//        } else
//            SessionManager.getInstance().setSavedGuide(this, "1");
//
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.e("DisplayedCrea", getResources().getString(R.string.actualite_detail));

        //listener of Drawer
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerOpened = true;
                Log.e("Displayed1", getResources().getString(R.string.actualite_detail));

                invalidateOptionsMenu();
                Log.e("Displayed2", getResources().getString(R.string.actualite_detail));


            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerOpened = false;
                Log.e("Displayed3", getResources().getString(R.string.actualite_detail));

                invalidateOptionsMenu();
                Log.e("Displayed4", getResources().getString(R.string.actualite_detail));


            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                // create new thread for change fragment when drawer change state
                if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                    runnable.run();

                    runnable = null;
                }

            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Log.e("DisplayedCrea", getResources().getString(R.string.actualite_detail));

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {


        // set color title
//        titleToolbar.setTextColor(ContextCompat.getColor(this, R.color.green_color_tittle));
//        // set color icon Drawer
//        changeDrawerColor(R.color.green_color_tittle);
        switch (v.getId()) {

            case R.id.actualite:
                deletFragmentCourant();
                // selected fragment
                fragment = new ActualiteFragment().newInstance(Constant.idArticle, false, false, false, getResources().getString(R.string.actualite_detail));
                isActuality = true;

                //   set title toolbar
                titleToolbar.setText(getResources().getString(R.string.actualite_detail));
                // execute the fragment
                displayView();

                break;
            case R.id.dossiers:
                deletFragmentCourant();
                // selected fragment
                fragment = new ActualiteFragment().newInstance(Constant.idDossier, false, true, false, getResources().getString(R.string.dossiers));
                isActuality = false;
                // set title toolbar
                titleToolbar.setText(getResources().getString(R.string.dossiers));
                // execute fragment selected
                displayView();

                break;
            case R.id.plus_lus:
                deletFragmentCourant();
                // selected fragment
                fragment = new ActualiteFragment().newInstance(Constant.idPlusLus, false, true, false, getResources().getString(R.string.les_plus_lus));
                isActuality = false;
                // set title toolbar
                titleToolbar.setText(getResources().getString(R.string.les_plus_lus));
                // execute fragment selected
                displayView();
                break;
            case R.id.videos:
                deletFragmentCourant();
                // selected fragment
                fragment = new ActualiteFragment().newInstance(Constant.idVideo, false, true, true, getResources().getString(R.string.videos));
                isActuality = false;
                // set title toolbar
                titleToolbar.setText(getResources().getString(R.string.videos));
                // execute fragment selected
                displayView();
                break;


            case R.id.blague:
                deletFragmentCourant();
                // selected fragment
                fragment = new BlagueFragment();
                // set title toolbar
                titleToolbar.setText(R.string.blagues_tittle);
                isActuality = false;
                // execute fragment selected
                displayView();
                break;

            case R.id.mes_favoris:
                deletFragmentCourant();
                // selected fragment
                fragment = FavoriFragment.newInstance(Constant.idFavorite, true, getResources().getString(R.string.ma_liste));
                // set title toolbar
                isActuality = false;
                titleToolbar.setText(getResources().getString(R.string.ma_liste));
                // execute fragment selected
                displayView();

                break;

            case R.id.horaire_prieres:
                deletFragmentCourant();
                // selected fragment
                fragment = new HomeFragment();
                // set title toolbar
                titleToolbar.setText(getResources().getString(R.string.horaire_title));
                isActuality = false;
                // execute fragment selected
                displayView();

                break;

            case R.id.a_propos:
                deletFragmentCourant();
                // selected fragment
                fragment = new AproposFragment();
                // set title toolbar
                titleToolbar.setText(getResources().getString(R.string.a_propos));
                isActuality = false;
                // execute fragment selected
                displayView();


                break;

            case R.id.parametre:
                deletFragmentCourant();
                // selected fragment
                fragment = new ParametreFragment();
                isActuality = false;
                // set title toolbar
                titleToolbar.setText(getResources().getString(R.string.paremtre));
                // execute fragment selected
                displayView();

        }
    }


    /**
     * exucute selected fragment
     */
    public void deletFragmentCourant() {
        if (fragment != null) {
            Log.e("DisplayedDelete", getResources().getString(R.string.actualite_detail));

            runnable = new Runnable() {
                @Override
                public void run() {
                    //replacing the fragment
                    if (fragment != null) {


                        getSupportFragmentManager().beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();

                    }
                }
            };
        }

    }

    /**
     * exucute selected fragment
     */
    public void displayView() {
        if (fragment != null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    //replacing the fragment
                    if (fragment != null) {


                        fragmentTransaction = getSupportFragmentManager().beginTransaction();

                        if (isActuality) {
                            lngIcon.setVisibility(View.VISIBLE);
                            fragmentTransaction.replace(R.id.fragment_container, fragment, Constant.IS_ACTUALITY);
                        } else {
                            lngIcon.setVisibility(View.VISIBLE);

                            fragmentTransaction.replace(R.id.fragment_container, fragment, "NotActuality");
                        }
                        fragmentTransaction.commit();


                    }
                }
            };
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * fragment execute on start
     */
    public void showScreen() {

        // Vérifier si on vient de HomeTnActivity avec un fragment spécifique à charger
        if (getIntent() != null && getIntent().hasExtra("FRAGMENT_TO_LOAD")) {
            String fragmentToLoad = getIntent().getStringExtra("FRAGMENT_TO_LOAD");

            switch (fragmentToLoad) {
                case "horaires":
                    // Charger le fragment Horaires de Prière
                    fragment = new HorairePriereFragment();
                    isActuality = false;
                    lngIcon.setVisibility(View.VISIBLE);
                    titleToolbar.setText(getResources().getString(R.string.horaire_title));
                    break;

                case "favoris":
                    // Charger le fragment Favoris
                    fragment = FavoriFragment.newInstance(Constant.idFavorite, true, getResources().getString(R.string.ma_liste));
                    isActuality = false;
                    lngIcon.setVisibility(View.VISIBLE);
                    titleToolbar.setText(getResources().getString(R.string.ma_liste));
                    break;

                case "top24":
                    // Charger le fragment Plus Lus (Top24)
                    fragment = new ActualiteFragment().newInstance(Constant.idPlusLus, false, true, false, getResources().getString(R.string.les_plus_lus));
                    isActuality = false;
                    lngIcon.setVisibility(View.VISIBLE);
                    titleToolbar.setText(getResources().getString(R.string.les_plus_lus));
                    break;

                default:
                    // Par défaut, charger Actualités
                    loadDefaultFragment();
                    return;
            }

            // Charger le fragment sélectionné
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction1 = fragmentManager.beginTransaction();
            fragmentTransaction1.replace(R.id.fragment_container, fragment);
            fragmentTransaction1.commit();

        } else if (getIntent() != null && getIntent().getStringExtra("current") != null && getIntent().getStringExtra("current").contains("ParametreFragment")) {
            // execute ParametreFragment
            fragment = new ParametreFragment();
            isActuality = false;
            lngIcon.setVisibility(View.GONE);
            titleToolbar.setText(getResources().getString(R.string.paremtre));
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction1 = fragmentManager.beginTransaction();
            fragmentTransaction1.replace(R.id.fragment_container, fragment);
            fragmentTransaction1.commit();

        } else {
            // Par défaut, charger Actualités
            loadDefaultFragment();
        }
    }

    /**
     * Charger le fragment par défaut (Actualités)
     */
    private void loadDefaultFragment() {
        fragment = new ActualiteFragment().newInstance(Constant.idArticle, false, false, false, getResources().getString(R.string.actualite_detail));
        titleToolbar.setText(getResources().getString(R.string.actualite_detail));
        Log.e("DisplayedCreat", getResources().getString(R.string.actualite_detail));

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1.replace(R.id.fragment_container, fragment, Constant.IS_ACTUALITY);
        fragmentTransaction1.commit();
    }


    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Yarja3 lil HomeActivity 3iwadh ma ysakkerch l'app
            Intent intent = new Intent(MainActivity.this, HomeTnActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        Log.d("LaunchUrlA", MainActivity.this.getClass().getName() + " pause");
        SessionManager.getInstance().activity_onTop(getApplicationContext(), "");
        Log.e("DisplayedCreap", getResources().getString(R.string.actualite_detail));


        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //   SessionManager.getInstance().activity_onTop(getApplicationContext(), "");
        Log.d("LaunchUrlA", MainActivity.this.getClass().getName() + " destroyed");
        super.onDestroy();
//        stopService(intentService);
    }

    /**
     * Show Alert Dialog before close app with back button
     */
    public void showAlertClose() {
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.quitter_app_text));

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.message_quit_app_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.oui_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
//                        stopService(intentService);
                        // current activity
                        MainActivity.this.finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        //   finish();


                    }
                })
                .setNegativeButton(getString(R.string.non_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //   Log.d("LaunchUrl",MainActivity.this.getClass().getName());
        SessionManager.getInstance().activity_onTop(MainActivity.this, MainActivity.this.getClass().getName());
        Log.d("LaunchUrlA", MainActivity.this.getClass().getName() + "Create");


        // Parse File  if not empty
        utils.checkSurvey();

        Log.e("DisplayedCreaRe1", getResources().getString(R.string.actualite_detail));
        Log.e("DisplayedCreaRe2", getString(R.string.actualite_detail));


    }

    public void startEnquete(String url) {
        Intent i = new Intent(this, EnqueteActivity.class);
        i.putExtra("urlSurvey", url);
        startActivity(i);
    }

    public long getDiffernceTime(String last) {

        try {
            Date date1 = dateFormat.parse(last);
            Date date = new Date();

            long dateDiff = date.getTime() - date1.getTime();
            return dateDiff;


        } catch (ParseException e) {

            e.printStackTrace();
            return 0;

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
                btnAr.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng));
                btnFr.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black_color));
                enTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_off));
                currentLng = "ar";

            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentLng.equals(SessionManager.getInstance().getCurrentLang(MainActivity.this))) {
                    SessionManager.getInstance().setCurrentLng(MainActivity.this, currentLng);
                    setIconLang();
                    Locale locale = new Locale(currentLng);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
//                    requireActivity().getBaseContext().getResources().updateConfiguration(config,
//                            requireActivity().getBaseContext().getResources().getDisplayMetrics());
//                    //   LocaleHelper.setLocale(requireActivity(), "ar");
                    fromMain = true;
                    onConfigurationChanged(config);
                }
                bottomSheetDialog.dismiss();

            }
        });
        btnFr.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng));
            btnEn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng_off));
            arTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_off));
            frTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black_color));
            currentLng = "fr";
        });

        btnEn.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng_off));
            btnEn.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.bg_selected_lng));
            arTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.black_color));
            frTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_color_off));
            currentLng = "en";

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

    @Override
    protected void onRestart() {
        Log.e("Testchanged", "changedR");
        super.onRestart();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        Log.e("FragmentCurrent", fragment.getClass().getName());
        String currentFragment = fragment.getClass().getName();
// setContentView(R.layout.activity_main);
//        Log.e("Testchanged", "changed");
        finish();
        Intent i = getIntent();
        i.putExtra("current", currentFragment);
        startActivity(i);
        Log.e("FragmentCurrent1", currentFragment);

        //add checking current mode app

        if (!SessionManager.getInstance().isModeAuto(this)) {
            if (SessionManager.getInstance().getCurrentMode(this).equals("night")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


            }
        }

    }

    public void setIconLang() {
        switch (SessionManager.getInstance().getCurrentLang(this)) {
            case Constant.AR: {
                lngIcon.setImageResource(R.drawable.icon_ar);
                Application.getInstance().trackScreenView(this, Constant.ARABIC);

            }
            break;
            case Constant.EN: {
                lngIcon.setImageResource(R.drawable.icon_en);
                Application.getInstance().trackScreenView(this, Constant.ENGLISH);


            }
            break;
            default: {
                lngIcon.setImageResource(R.drawable.icon_fr);
                Application.getInstance().trackScreenView(this, Constant.FRENCH);


            }
        }

    }

    public ImageView getLngIcon() {
        return lngIcon;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STARTER_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                onConfigurationChanged(new Configuration());
            }
        }
    }
}


