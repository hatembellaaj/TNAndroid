package com.mdweb.tunnumerique.ui.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Categories;
import com.mdweb.tunnumerique.data.model.ItemMenu;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.LocalFilesManager;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.activitys.FilterArticleActivity;
import com.mdweb.tunnumerique.ui.activitys.GuideActivity;
import com.mdweb.tunnumerique.ui.activitys.MainActivity;
import com.mdweb.tunnumerique.ui.adapters.ArticleAdapter;
import com.mdweb.tunnumerique.ui.adapters.ExpandableListAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a Actualite fragment .
 *
 * @author SOFIENE ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @version 1.0
 * @since 03-05-2017
 */

public class ActualiteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_TYPE = "type";
    private static final String ARG_TYPE_FRAGMENT = "type_fragment";
    private static final String ARG_FAVORIS = "favoris";
    private static final String ARG_SCROLL = "scroll";
    private static final String ARG_VIDEO = "video";
    private static int type;
    private String typeFragment = "";
    private ArticleAdapter articleAdapter;
    private Dialog dialog;
    private List<News> newsList;
    private List<News> listSearch;
    public ExpandableListAdapter expandableListAdapter;
    public RecyclerView recyclerview;
    public LinearLayoutManager linearLayoutManager;
    private Drawable drawable;
    private MenuItem itemFilter;
    private MainActivity activity;
    private boolean isFavoris;
    private boolean isScroll;
    private boolean isVideo;
    private boolean isSearch = false;
    private boolean isLoad = false;
    private String categorie;
    public FloatingActionButton floatingActionButton;
    private List<Categories> categoriesList;
    private String idLastArticle;
    private LinearLayout emptyListe;
    private RecyclerView recyclerNews;
    public static ActualiteFragment actualiteFragmentRunningInstance;
    private MyReceiver br;
    private Handler handler;
    private Handler handlerdialog;
    private Handler handlerOption;
    private Handler handlerRefresh;
    private LocalFilesManager locallyFiles;
    final static int mScrollOffset = 4;
    private long mLastClickTime = 0;


    /**
     * default constructor
     */
    public ActualiteFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param typeNews Parameter 1.
     * @return A new instance of fragment ActualiteFragment.
     */
    public static ActualiteFragment newInstance(int typeNews, boolean isFavoris, boolean isScroll, boolean isVideo, String typeFragment) {
        ActualiteFragment fragment = new ActualiteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, typeNews);
        args.putString(ARG_TYPE_FRAGMENT, typeFragment);
        args.putBoolean(ARG_FAVORIS, isFavoris);
        args.putBoolean(ARG_VIDEO, isVideo);
        args.putBoolean(ARG_SCROLL, isScroll);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActualiteFragment getInstace() {
        return actualiteFragmentRunningInstance;
    }

    public static int getType() {
        return type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualiteFragmentRunningInstance = this;
        handler = new Handler();
        handlerdialog = new Handler();
        handlerOption = new Handler();
        handlerRefresh = new Handler();
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
            isFavoris = getArguments().getBoolean(ARG_FAVORIS);
            isVideo = getArguments().getBoolean(ARG_VIDEO);
            isScroll = getArguments().getBoolean(ARG_SCROLL);
            typeFragment = getArguments().getString(ARG_TYPE_FRAGMENT);
        }
        activity = ((MainActivity) getActivity());
        //show filter icon in toolbar
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_actualite, container, false);
        initView(rootView);

        return rootView;
    }

    /**
     * initialize the views
     *
     * @param rootView layout of fragment
     */
    public void initView(View rootView) {
        emptyListe = (LinearLayout) rootView.findViewById(R.id.empty_liste1);

        String lng = SessionManager.getInstance().getCurrentLang(requireActivity());
        // get categories list from local json file
        if (lng.equals(Constant.AR)) {
            categoriesList = new DataParser().getListCategories(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_CATEGORIES_AR),0);
        } else if (lng.equals(Constant.EN)) {
            categoriesList = new DataParser().getListCategories(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_CATEGORIES_EN),0);

        } else {
            categoriesList = new DataParser().getListCategories(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_CATEGORIES),1);

        }
        recyclerNews = (RecyclerView) rootView.findViewById(R.id.recycler_news);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerNews.setLayoutManager(linearLayoutManager);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_guide);
        floatingActionButton.setOnClickListener(this);
        newsList = new ArrayList<>();
        listSearch = new ArrayList<News>();
        articleAdapter = new ArticleAdapter(getActivity(), isFavoris, type);
        articleAdapter.setLinearLayoutManager(linearLayoutManager);
        recyclerNews.setAdapter(articleAdapter);
        floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
        floatingActionButton.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
        changeColorArrow();
        //  scrollOffset when hide/show fab in scroll recyclerView
//        recyclerNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (newsList.size() > 1)
//                    if (dy > 0 || dy < 0 && floatingActionButton.isShown())
//                        floatingActionButton.hide();
//            }
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE)
//                    floatingActionButton.show();
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });

    }

    private void methodeSeparation() {

    }

    @Override
    public void onAttach(Context context) {
        setHasOptionsMenu(true);
        super.onAttach(context);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.menu_filter, menu);
        itemFilter = menu.findItem(R.id.filter_categeries);
        if (isVideo) {
            itemFilter.setVisible(false);
        }

        MenuItemCompat.setOnActionExpandListener(itemFilter,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        if (isFavoris)
                            articleAdapter.setFilter(FavorisDataBase.getInstance(getContext()).getAllNews(SessionManager.getInstance().getCurrentLang(getActivity())));
                        else
                            articleAdapter.setFilter(newsList);
                        changeColorArrow();
                        // set visibility to view filter
                        itemFilter.setVisible(true);
                        isSearch = false;
                        // Return true to collapse action view
                        return true;

                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // disable load when search view is expanded
                        // Do something when expanded
                        changeColorArrow();
                        // set visibility to view filter
                        itemFilter.setVisible(false);
                        isSearch = true;
                        return true; // Return true to expand action view
                    }
                });
        //get green color
        int greenColor = (ContextCompat.getColor(getContext(), R.color.green_color));

        // change color menu
        drawable = menu.findItem(R.id.filter_categeries).getIcon();
        // set green color to icon filter
        drawable.setColorFilter(greenColor, PorterDuff.Mode.SRC_ATOP);
        super.onCreateOptionsMenu(menu, inflater);

    }

//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        super.onOptionsItemSelected(item);
//
//        switch (item.getItemId()) {
//            case R.id.filter_categeries:
//                //disabled dialog filter when empty list
//                if (newsList.size() > 1) {
//                    dialogFilter();
//                }
//                // disabled filter
//                itemFilter.setEnabled(false);
//                // enabled button filter
//                handlerOption.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        itemFilter.setEnabled(true);
//                    }
//                }, 1000);
//                return true;
//        }
//        return true;
//    }

    /**
     * show dialog for filter
     */
    @SuppressLint("WrongConstant")
    private void dialogFilter() {
        //dismiss dialog
        dialog = new Dialog(getContext(), R.style.DialogSlideAnimFilter);
        View view = (getActivity()).getLayoutInflater().inflate(R.layout.dialog_filter, null);
        dialog.setContentView(view);

        LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        recyclerview = (RecyclerView) view.findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
        //disable over scroll effect of recyclerView
        recyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //
        expandableListAdapter = new ExpandableListAdapter(getContext(), getItemMenu(), this, true);
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
        window.setGravity(Gravity.CENTER);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

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
//            for (News news : newses) {
            for (int i = 1; i < newses.size(); i++) {
                News news = newses.get(i);
                final String filterPattern = constraint.toString().toLowerCase();
                //
                String keyWord = news.getKeyWordsNews();
                String[] parts = keyWord.split(",");
                //create list of string to contain a list of key word
                List<String> keyWordList = new ArrayList<>();
                for (int j = 0; j < parts.length; j++) {
                    keyWordList.add(parts[j].toString().toLowerCase());
                }
                news.setNewsKeyWords(keyWordList);
                //
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
     * change color arrow of search view
     */
    public void changeColorArrow() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.changeDrawerColor(R.color.green_color_tittle);
            }
        }, Constant.duration);
    }

    /**
     * Display item filter in fragment
     *
     * @param id id item
     */
    public void displayView(int id) {
        Categories categoriesSelected = getCateoriesFromId(categoriesList, id);
        categorie = categoriesSelected.getTitleCategories();
        final Intent intent = new Intent(getActivity(), FilterArticleActivity.class);
        Bundle bundle = new Bundle();
        List<News> newsList = listFilter(listSearch, categorie);
        // bundle.putSerializable("initialList", (Serializable) newsList);
        //stockage de la liste d'article dans un fichier json
        String jsonNewsList = new Gson().toJson(newsList);
        locallyFiles = new LocalFilesManager(getActivity());
        locallyFiles.saveLocallyFile("MonfileFilter", jsonNewsList);
        intent.putExtras(bundle);

        intent.putExtra("categorie", (Serializable) categoriesSelected);
        intent.putExtra("type", type);
        // dismiss dialog filter after 3 s

        handlerdialog.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                getActivity().startActivityForResult(intent,MainActivity.STARTER_CODE);
            }
        }, 300);


    }

    /**
     * @param categoriesList
     * @param idCategories
     * @return
     */
    private Categories getCateoriesFromId(List<Categories> categoriesList, int idCategories) {
        for (int i = 0; i < categoriesList.size(); i++) {
            if (Integer.parseInt(categoriesList.get(i).getIdCategories()) == idCategories)
                return categoriesList.get(i);
        }
        return null;
    }

    @Override
    public void onPause() {
        //unregister receiver
        // getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
        if (br != null && typeFragment.equals(getResources().getString(R.string.actualite_detail)) && getActivity() != null)
            getActivity().unregisterReceiver(br);
    }

    @Override
    public void onResume() {
        //register receiver
        //  getActivity().registerReceiver(broadcastReceiver, new IntentFilter(ServiceSynchronisation.BROADCAST_ACTON));
        super.onResume();

        Application.getInstance().trackScreenView(getActivity(), typeFragment);
        if (typeFragment.equals(getResources().getString(R.string.actualite_detail))) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.mdweb.tn.refresh");
            br = new MyReceiver();
            if (getActivity() != null)
                getActivity().registerReceiver(br, intentFilter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();


    }

    /**
     * Load data from json file
     */
    private void loadData() {
        // clear list news
        newsList.clear();
        // add object for publicity
        News news = new News(Constant.isPublicity);
        newsList.add(news);
        String currentLng = SessionManager.getInstance().getCurrentLang(requireActivity());
        switch (type) {
            case Constant.idArticle:
                // get list article news from json file
                if (currentLng.equals(Constant.AR)) {
                    newsList.addAll(new DataParser().getListFetchNews(new Utils(getContext()).getStringFromFile(Communication.FILE_NEWS_INIT_AR)));
                } else if (currentLng.equals(Constant.EN)) {
                    newsList.addAll(new DataParser().getListFetchNews(new Utils(getContext()).getStringFromFile(Communication.FILE_NEWS_INIT_EN)));
                } else {
                    newsList.addAll(new DataParser().getListFetchNews(new Utils(getContext()).getStringFromFile(Communication.FILE_NEWS_INIT)));

                }

                // get id last article
                idLastArticle = newsList.get(newsList.size() - 1).getIdNews();
                floatingActionButton.setVisibility(View.VISIBLE);
                break;
            case Constant.idDossier:
                // get list article dossier from json file
                //   Log.d("DossierContenu",new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_DOSSIER));
                if (currentLng.equals(Constant.AR)) {
                    newsList.addAll(new DataParser().getListDossier(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_DOSSIER_AR)));
                } else if (currentLng.equals(Constant.EN)) {
                    newsList.addAll(new DataParser().getListNews(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_DOSSIER_EN)));

                } else {
                    newsList.addAll(new DataParser().getListDossier(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_DOSSIER)));

                }
                floatingActionButton.setVisibility(View.GONE);
                break;
            case Constant.idPlusLus:
                // get list article plusLus from json file
                if (currentLng.equals(Constant.AR)) {
                    newsList.addAll(new DataParser().getListNews(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_PLUS_LUS_AR)));
                } else if (currentLng.equals(Constant.EN)) {
                    newsList.addAll(new DataParser().getListNews(new Utils(getContext()).getStringFromFile(Communication.
                            FILE_NAME_PLUS_LUS_EN)));

                } else {
                    newsList.addAll(new DataParser().getListNews(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_PLUS_LUS)));

                }
                floatingActionButton.setVisibility(View.GONE);
                break;
            case Constant.idVideo:
                // get list video from local json file
                newsList.addAll(new DataParser().getListVideos(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_VIDEO)));
                floatingActionButton.setVisibility(View.GONE);
                break;
            case Constant.idFavorite:
                floatingActionButton.setVisibility(View.GONE);
                newsList.addAll(FavorisDataBase.getInstance(getContext()).getAllNews(SessionManager.getInstance().getCurrentLang(getActivity())));
                break;
        }

        articleAdapter.addAll(newsList);
        listSearch.clear();
        listSearch.addAll(newsList);

        listeEmptyTest();

    }

    /**
     *
     */
    private void listeEmptyTest() {
        if (newsList.size() == 1) {
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
         List<News> filtereNewslList = new ArrayList<>();
        for (News news : newses) {
            List<String> items = new ArrayList<>();
            if (news.getTypeNews() != null) {
                Log.e("NewCateg", news.getTypeNews());
                Log.e("Catego",categorie);
                items = Arrays.asList(news.getTypeNews().split("\\s*,\\s*"));
                Log.e("CategoTest",items.size()+"");
            }

            for (String cat : items) {
                Log.e("NewCategTest", categorie);
                Log.e("NewCategTestCa", cat);
                if (cat.contains(categorie)) {
                    Log.e("NewCategOK", "OK");
                    filtereNewslList.add(news);
                }
            }
        }
        Log.e("NewCategOK", "OKS"+filtereNewslList.size());
        return filtereNewslList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_guide:

                if (newsList.size() > 1) {
                    dialogFilter();
                }
                // disabled filter
                itemFilter.setEnabled(false);
                // enabled button filter
                handlerOption.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemFilter.setEnabled(true);
                        //
                        //  throw new RuntimeException("Test Crash"); // Force a crash
                    }
                }, 1000);

                // disabled filter
                // itemFilter.setEnabled(false);
                // open dialog upload photo
                // dialogPartagePhoto();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.DialogSlideAnim);
        View view = (getActivity()).getLayoutInflater().inflate(R.layout.partage_photo, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        TextView takePhoto = (TextView) view.findViewById(R.id.take_photo);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView loadPhoto = (TextView) view.findViewById(R.id.load_photo);
        // take photo from camera
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enabled filter
                itemFilter.setEnabled(true);
                Intent intent = new Intent(getContext(), GuideActivity.class);
                intent.putExtra("GaleryOrCamera", 1);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        // upload from gallery
        loadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enabled filter
                itemFilter.setEnabled(true);
                Intent intent = new Intent(getContext(), GuideActivity.class);
                intent.putExtra("GaleryOrCamera", 0);
                startActivity(intent);
                dialog.dismiss();

            }
        });
        // dismiss dialog upload
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enabled filter
                itemFilter.setEnabled(true);
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


    public void refreshAdapter() {

        if (type == Constant.idArticle) {
            handlerRefresh.postDelayed(new Runnable() {
                @Override
                public void run() {
                    newsList.clear();
                    News news = new News(Constant.isPublicity);
                    newsList.add(news);
                    // newsList.addAll(new DataParser().getListFetchNews(new Utils(getContext()).getStringFromFile(Communication.FILE_NEWS_INIT)));

                    if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("ar"))
                        newsList.addAll(new DataParser().getListFetchNews(new Utils(getActivity()).getStringFromFile(Communication.FILE_NEWS_INIT_AR)));
                    else
                        newsList.addAll(new DataParser().getListFetchNews(new Utils(getActivity()).getStringFromFile(Communication.FILE_NEWS_INIT)));

                    idLastArticle = newsList.get(newsList.size() - 1).getIdNews();
                    articleAdapter.addAll(newsList);
                    listSearch.clear();
                    listSearch.addAll(newsList);
                    listeEmptyTest();
                }
            }, 500);
        }

    }


    public static class MyReceiver extends BroadcastReceiver {
        String msg;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActualiteFragment.getInstace() != null)
                ActualiteFragment.getInstace().refreshAdapter();
        }
    }

    private static boolean m_iAmVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        m_iAmVisible = isVisibleToUser;

    }
}