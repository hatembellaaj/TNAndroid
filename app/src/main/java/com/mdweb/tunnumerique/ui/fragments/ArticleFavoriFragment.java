package com.mdweb.tunnumerique.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Categories;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.activitys.FilterArticleActivity;
import com.mdweb.tunnumerique.ui.activitys.GuideActivity;
import com.mdweb.tunnumerique.ui.activitys.MainActivity;
import com.mdweb.tunnumerique.ui.adapters.ArticleAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */


public class ArticleFavoriFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_TYPE = "type";
    private static final String ARG_FAVORIS = "favoris";
    private static int type;
    private ArticleAdapter articleAdapter;
    private Dialog dialog;
    private List<News> newsList;
    private List<News> newsListArticle;
    private List<News> listSearch;
    public RecyclerView recyclerview;
    public LinearLayoutManager linearLayoutManager;
    private boolean isFavoris;
    private String categorie;
    private List<Categories> categoriesList;
    private RelativeLayout emptyListe;
    private RecyclerView recyclerNews;
    private Handler handler;
    public FloatingActionButton floatingActionButton;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param typeNews Parameter 1.
     * @return A new instance of fragment FavoriFragment.
     */
    public static ArticleFavoriFragment newInstance(int typeNews, boolean isFavoris) {

        ArticleFavoriFragment fragment = new ArticleFavoriFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, typeNews);
        args.putBoolean(ARG_FAVORIS, isFavoris);
        fragment.setArguments(args);
        return fragment;
    }


    public ArticleFavoriFragment() {
        // Required empty public constructor
    }

    public static int getType() {
        return type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
            isFavoris = getArguments().getBoolean(ARG_FAVORIS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_article_favori, container, false);
        initView(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    /**
     * initialize the views
     *
     * @param rootView layout of fragment
     */
    public void initView(View rootView) {
        emptyListe = (RelativeLayout) rootView.findViewById(R.id.empty_listeA);
        // get categories list from local json file
        String lng = SessionManager.getInstance().getCurrentLang(requireActivity());
        if (lng.equals(Constant.AR)) {
            categoriesList = new DataParser().getListCategories(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_CATEGORIES_AR), 0);
        } else if (lng.equals(Constant.EN)) {
            categoriesList = new DataParser().getListCategories(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_CATEGORIES_EN), 0);

        } else {
            categoriesList = new DataParser().getListCategories(new Utils(getContext()).getStringFromFile(Communication.FILE_NAME_CATEGORIES), 1);

        }
        recyclerNews = (RecyclerView) rootView.findViewById(R.id.recycler_news);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerNews.setLayoutManager(linearLayoutManager);
        newsList = new ArrayList<>();
        newsListArticle = new ArrayList<>();
        listSearch = new ArrayList<News>();
        articleAdapter = new ArticleAdapter(getActivity(), isFavoris, type);
        articleAdapter.setLinearLayoutManager(linearLayoutManager);
        recyclerNews.setAdapter(articleAdapter);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_guide);
        floatingActionButton.setOnClickListener(this);

        final int mScrollOffset = 4;
        recyclerNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (newsList.size() > 1)
                    if (Math.abs(dy) > mScrollOffset) {
                        if (dy > 0) {
                            // hide floating
                            floatingActionButton.hide();
                        } else {
                            //show floating
                            floatingActionButton.show();
                        }
                    }
            }
        });

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
        bundle.putSerializable("initialList", (Serializable) newsList);
        intent.putExtras(bundle);
        intent.putExtra("categorie", (Serializable) categoriesSelected);
        intent.putExtra("type", type);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                getActivity().startActivityForResult(intent, MainActivity.STARTER_CODE);
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

    /**
     * Load data from json file
     */
    private void loadData() {
        News unNews;
        newsList.clear();
        newsListArticle.clear();
        News news = new News(Constant.isPublicity);
        newsListArticle.add(news);
        newsList.addAll(FavorisDataBase.getInstance(getContext()).getAllNews(SessionManager.getInstance().getCurrentLang(getActivity())));
        for (int i = 0; i < newsList.size(); i++) {
            unNews = newsList.get(i);
            if (unNews.getArtOrPubOrVid() != Constant.isVideo)
                newsListArticle.add(unNews);
        }
        articleAdapter.addAll(newsListArticle);
        listSearch.clear();
        listSearch.addAll(newsListArticle);
        listeEmptyTest();
    }

    /**
     *
     */
    private void listeEmptyTest() {
        if (newsListArticle.size() == 1) {
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
        final List<News> filtereNewslList = new ArrayList<>();
        for (News news : newses) {
            List<String> items = new ArrayList<>();
            if (news.getTypeNews() != null)
                items = Arrays.asList(news.getTypeNews().split("\\s*,\\s*"));

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
            case R.id.fab_guide:
                dialogPartagePhoto();
                break;
        }
    }
}
