package com.mdweb.tunnumerique.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.banner.BannerView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Blague;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.style.ShareSN;

import java.util.List;

public class BlaguesDetailsFragment extends Fragment {
    private static final String ARG_PARAM = "blagues";
    private List<Blague> blagueList;
    Blague currentBlague;
    private Utils utils;
    WebView blagueTxt;
    BannerView huaweiBannerView;



    @SuppressLint("SetJavaScriptEnabled")
    public BlaguesDetailsFragment() {
        // Required empty public constructor
    }

    public static BlaguesDetailsFragment newInstance(Blague blague) {

        BlaguesDetailsFragment fragment = new BlaguesDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, blague);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            currentBlague = (Blague) getArguments().getSerializable(ARG_PARAM);
        }
        utils = new Utils(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Enable the drawing of the whole document for Lollipop to get the whole WebView
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_details_blagues, container, false);
         blagueTxt = view.findViewById(R.id.blague);
        //TextView noteTxt = view.findViewById(R.id.note);
        blagueTxt.getSettings().setJavaScriptEnabled(true);
            huaweiBannerView = view.findViewById(R.id.hw_banner_view);
            huaweiBannerView.setVisibility(View.VISIBLE);
            // Create an ad request to load an ad.
            AdParam adParam = new AdParam.Builder().build();
            huaweiBannerView.loadAd(adParam);
            Log.d("TestPub : " ,  "Test");

        blagueTxt.setWebViewClient(new UriWebViewClient());
       // blagueTxt.setWebViewClient(new UriWebViewClient());
        blagueTxt.setVisibility(View.INVISIBLE);

        blagueTxt.loadDataWithBaseURL("https://www.tunisienumerique.com", currentBlague.getContenuBlagues(), "text/html", "UTF-8", null);
      //  noteTxt.setText(currentBlague.getNoteBlagues());
        LinearLayout shareBtn = (LinearLayout) view.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareSN shareSN = new ShareSN(getActivity());
                shareSN.share(currentBlague.getBlagueUrl(), currentBlague.getTitleBlague());

            }
        });
        return view;
    }

    private class UriWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            if(request.contains("about:blank"))
                return true;
            if (view.getHitTestResult().getType() > 0) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(request))));
                return true;
            } else {
                return false;
            }

        }

        @RequiresApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if(request.getUrl().toString().contains("about:blank"))
                return true;
            if (view.getHitTestResult().getType() > 0) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(request.getUrl()))));
                return true;
            } else {
                return false;
            }

        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.d("ModeDARK3", "Actifs");


            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                Log.e("ModeDARK32", "Actifs");


                executeJavascript("javascript:document.body.classList.add('dark-mode');", new ValueCallback() {
                    @Override
                    public void onReceiveValue(Object value) {
                        blagueTxt.setVisibility(View.VISIBLE);

                        Log.e("ModeDARK31", "Actifs");                }
                });
//            Log.d("ModeDARK3", "Actif");
//            /// if(news.getDark_Mode()!=null && !news.getDark_Mode().isEmpty()){
//            Log.d("ModeDARK3", "Actif2");
//            contenu = news.getDark_Mode();
                // }

            }
            else {
                blagueTxt.setVisibility(View.VISIBLE);
            }
            String host = Uri.parse(url).getHost();



        }

    }

    private void executeJavascript(String javascript, ValueCallback callback){
        blagueTxt.evaluateJavascript(javascript, callback);

    }
}
