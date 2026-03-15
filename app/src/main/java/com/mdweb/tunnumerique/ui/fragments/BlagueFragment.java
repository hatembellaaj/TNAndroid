package com.mdweb.tunnumerique.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Blague;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.ui.adapters.BlagueAdapter;

import java.util.ArrayList;
import java.util.List;

public class BlagueFragment extends Fragment {


    private Utils utils;
    List<Blague> blagueList = new ArrayList<>();
    private RecyclerView listViewBlague;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blagues, container, false);
        init(v);
        return v;
    }


    public void init(View rootView) {

//        AdView mAdView = (AdView) rootView.findViewById(R.id.ad_view_pub);
//
//         mAdView.setAdUnitId("ca-app-pub-2102118777505746/4044741086");
////        MobileAds.initialize(getActivity(), "ca-app-pub-8614164846286387~1481043532");
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//        mAdView.loadAd(adRequest);


        listViewBlague = rootView.findViewById(R.id.listViewBlag);
        LinearLayout emptyLayout = rootView.findViewById(R.id.empty_liste1);
        LinearLayout layout_not = rootView.findViewById(R.id.layout_not);

        utils = new Utils(getContext());

        blagueList = new DataParser().getBlagueList(utils.getStringFromFile(Communication.FILE_NAME_BLAGUES));
        BlagueAdapter blagueAdapter = new BlagueAdapter(blagueList, requireActivity());
        listViewBlague.setLayoutManager(new LinearLayoutManager(getContext()));
        listViewBlague.setAdapter(blagueAdapter);
        if (blagueList!=null && blagueList.size()>1) {
            Log.d("testEnter", "Enter"+blagueList.size());
            emptyLayout.setVisibility(View.GONE);
            layout_not.setVisibility(View.VISIBLE);
            listViewBlague.setVisibility(View.VISIBLE);
        } else {
            Log.d("testEnter", "Enter1");

            emptyLayout.setVisibility(View.VISIBLE);
            layout_not.setVisibility(View.GONE);
            listViewBlague.setVisibility(View.GONE);

        }


        Log.d("ListBlaguesSize", blagueList.size() + "");
    }


    @Override
    public void onResume() {
        Application.getInstance().trackScreenView(getActivity(),"Blagues");

        super.onResume();
    }
}
