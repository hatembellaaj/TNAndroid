package com.mdweb.tunnumerique.ui.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private static final String ARG_TYPE_FRAGMENT = "type_fragment";
    private static final String ARG_FAVORIS = "favoris";
    private static int type;
    private String typeFragment;
    private Dialog dialog;
    public RecyclerView recyclerview;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean isFavoris;
    public FloatingActionButton floatingActionButton;


    public FavoriFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param typeNews Parameter 1.
     * @return A new instance of fragment FavoriFragment.
     */
    public static FavoriFragment newInstance(int typeNews, boolean isFavoris, String typeFragment) {
        FavoriFragment fragment = new FavoriFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, typeNews);
        args.putString(ARG_TYPE_FRAGMENT, typeFragment);
        args.putBoolean(ARG_FAVORIS, isFavoris);
        fragment.setArguments(args);
        return fragment;
    }

    public static int getType() {
        return type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
            isFavoris = getArguments().getBoolean(ARG_FAVORIS);
            typeFragment = getArguments().getString(ARG_TYPE_FRAGMENT);
        }

        //show filter icon in toolbar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favori, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);//setting tab over viewpager
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        changeTabsFont();
        //Implementing tab selected listener over tablayout
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());//setting current selected item over viewpager

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
//        initView(rootView);
        return rootView;
    }

    //Setting View Pager
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapterFavori adapter = new ViewPagerAdapterFavori(getChildFragmentManager());
        ArticleFavoriFragment fragment1 = ArticleFavoriFragment.newInstance(type, isFavoris);
        VideoFavoriFragment fragment2 = VideoFavoriFragment.newInstance(type, isFavoris);
        adapter.addFrag(fragment1, getString(R.string.mes_articles));
        adapter.addFrag(fragment2, getString(R.string.mes_video));
        viewPager.setAdapter(adapter);
    }


    //View Pager fragments setting adapter class
    public class ViewPagerAdapterFavori extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();//fragment arraylist
        private final List<String> mFragmentTitleList = new ArrayList<>();//title arraylist*

        public ViewPagerAdapterFavori(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        //adding fragments and title method
        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onAttach(Context context) {
        setHasOptionsMenu(true);
        super.onAttach(context);

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.getInstance().trackScreenView(getActivity(), typeFragment);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTextSize(18f);

                    if (SessionManager.getInstance().getCurrentLang(getActivity()).equals(Constant.AR)) {
                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(
                                getActivity().getAssets(), "fonts/din-next_-ar-regular.otf"));
                    } else {

                        ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "Roboto/Roboto-Regular.ttf"));

                    }
                }
            }
        }
    }


}