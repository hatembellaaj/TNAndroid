package com.mdweb.tunnumerique.ui.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.banner.BannerView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.style.ShareSN;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailFragment extends Fragment {

    private static String TAG = ArticleDetailFragment.class.getSimpleName();
    private static final String ARG_PARAM = "news";
    private News news;
    public WebView webviewDescription;
    private TextView titleToolbar;
    public FloatingActionButton fab;
    public FloatingActionButton fabPlayAudio;
    private ScrollView scrollView;
    private int positionScrollView;
    private Utils utils;
    private FrameLayout mContainer;
    private WebView mWebviewPop;
    private ImageView imageArticle;
    private long mLastClickTime = 0;
    //Add huawei banner
    BannerView huaweiBannerView;
    private long mLastClickTimeOnShare = 0;
    private MediaPlayer mediaPlayer;
    private boolean firstClickPlayAudio = false;

    @SuppressLint("SetJavaScriptEnabled")
    public ArticleDetailFragment() {
        // Required empty public constructor
    }

    public static ArticleDetailFragment newInstance(News news) {

        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, news);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            news = (News) getArguments().getSerializable(ARG_PARAM);
        }
        utils = new Utils(getActivity());
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Enable the drawing of the whole document for Lollipop to get the whole WebView
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mContainer = (FrameLayout) view.findViewById(R.id.webview_frame);
        // *//////////////////////////////////
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fabPlayAudio = (FloatingActionButton) view.findViewById(R.id.play_mp3);
        if(news.getUrlAudioNews().isEmpty()){
            prepareAudio();
        }
        imageArticle = (ImageView) view.findViewById(R.id.image_details);
        webviewDescription = (WebView) view.findViewById(R.id.details_article);


        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        positionScrollView = scrollView.getScrollY();
        // set title toolbar
        ImageLoader.getInstance().displayImage(news.getImageUrlDetailsNews(), imageArticle, utils.getImageLoaderOptionDetailsNews());
      // check if  device  is huawei
            huaweiBannerView = view.findViewById(R.id.hw_banner_view);
            huaweiBannerView.setVisibility(View.VISIBLE);
            // Create an ad request to load an ad.
            AdParam adParam = new AdParam.Builder().build();
            huaweiBannerView.loadAd(adParam);
            Log.d("TestPub : " ,  "Test");
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                fabPlayAudio.setVisibility(View.VISIBLE);
            }
        });

        loadComments();
        //prepare audio before display artical details fragment
        fab.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
        fab.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                if (scrollView.getScrollY() != 0 && scrollView.getScrollY() > 0) {
//                    if (scrollView.getScrollY() > positionScrollView)
//                        fab.hide();
//                    else {
//                        if ((positionScrollView - scrollView.getScrollY()) > 10)
//                            fab.show();
//                    }
//                    positionScrollView = scrollView.getScrollY();
//
//                }
//            }
//        });
        String contenu =news.getContenuNews();
        webviewDescription.loadDataWithBaseURL("https://www.tunisienumerique.com", contenu, "text/html", "UTF-8", null);


        // share article
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button
                //fab.setEnabled(false);
                // share
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                ShareSN shareSN = new ShareSN(getActivity());
                shareSN.share(news.getShareUrlNews(), news.getTitleNews());

            }
        });
        fabPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    fabPlayAudio.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_icon_stop));
                    //Application.getInstance().trackScreenView(getActivity(), "Audio_Android");
                   /* Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "AUDIO_ANDORID"+ news.getTitleNews());
                    bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ArticalDetailFragment");*/
                    if(!firstClickPlayAudio) {
                        //Application.getInstance().trackEventScreenView("play_audio_android", news.getIdNews(), news.getTitleNews());
                        Application.getInstance().trackScreenView(getActivity(), "Audio-Android: " + news.getTitleNews());
                        firstClickPlayAudio = true;
                    }
                }
                else {
                    mediaPlayer.pause();
                    fabPlayAudio.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_icon_play));
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                fabPlayAudio.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_icon_play));
                firstClickPlayAudio = false;
                mediaPlayer.reset();
                prepareAudio();
            }

        });



        return view;
    }


    private void loadComments() {

        webviewDescription.setWebViewClient(new UriWebViewClient());
        webviewDescription.setWebChromeClient(new UriChromeClient());
        webviewDescription.getSettings().setJavaScriptEnabled(true);
        webviewDescription.getSettings().setAppCacheEnabled(true);
        webviewDescription.getSettings().setDomStorageEnabled(true);
        webviewDescription.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      //  webviewDescription.getSettings().setSupportMultipleWindows(true);
        webviewDescription.getSettings().setSupportZoom(false);
        webviewDescription.getSettings().setBuiltInZoomControls(false);
        webviewDescription.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webviewDescription.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webviewDescription, true);
        }

        // facebook comment widget including the article url
        // webviewDescription.loadDataWithBaseURL("https://www.tunisienumerique.com", news.getContenuNews()+news.getNews_commentaire_android(), "text/html", "UTF-8", null);


    }

    @Override
    public void onResume() {
        mediaPlayer.reset();
        prepareAudio();
        super.onResume();
        positionScrollView = 0;
        Log.d("HeyKar", "Onresume");
        if(huaweiBannerView!=null){
            huaweiBannerView.resume();
        }

        try {
            Class.forName("android.webkit.WebView")
                    .getDeclaredMethod("onResume", (Class[]) null)
                    .invoke(webviewDescription, (Object[]) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
     //   webviewDescription.resumeTimers();



//        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
//            WebSettingsCompat.setForceDark(webviewDescription.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
//        }

    }

    @Override
    public void onPause() {
        fabPlayAudio.setVisibility(View.GONE);
        fabPlayAudio.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_icon_play));
        firstClickPlayAudio = false;
        mediaPlayer.reset();
        super.onPause();

        //  Log.d("HeyKar", "OnPause");
      //  webviewDescription.onPause();

        //stop video if exist in webView when app is paused
        try {
            Class.forName("android.webkit.WebView")
                    .getDeclaredMethod("onPause", (Class[]) null)
                    .invoke(webviewDescription, (Object[]) null);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }

        executeJavascript("javascript:document.querySelector('audio').pause();", new ValueCallback() {
            @Override
            public void onReceiveValue(Object value) {
               // Trace.d(TAG, value.toString());
            }
        });

        //webviewDescription.pauseTimers();
      //  webviewDescription.getSettings().setJavaScriptEnabled(false);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        webviewDescription.stopLoading();
        webviewDescription.loadData("", "text/html", "utf-8");
        webviewDescription.reload();
        webviewDescription.setWebChromeClient(null);
        webviewDescription.setWebViewClient(null);
        webviewDescription.removeAllViews();
        webviewDescription.clearHistory();
        webviewDescription.destroy();
        webviewDescription = null;
        mediaPlayer.release();


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
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            Log.d("ModeDARK3", "Actifs");
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                Log.e("ModeDARK32", "Actifs");


                executeJavascript("javascript:document.body.classList.add('dark-mode');", new ValueCallback() {
                    @Override
                    public void onReceiveValue(Object value) {
                        Log.e("ModeDARK31", "Actifs");                }
                });
//            Log.d("ModeDARK3", "Actif");
//            /// if(news.getDark_Mode()!=null && !news.getDark_Mode().isEmpty()){
//            Log.d("ModeDARK3", "Actif2");
//            contenu = news.getDark_Mode();
                // }

            }

            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            String host = Uri.parse(url).getHost();


            //setLoading(false);
            if (url.contains("/plugins/close_popup.php?reload")) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        mContainer.removeView(mWebviewPop);
                        loadComments();
                        imageArticle.setVisibility(View.VISIBLE);
                    }
                }, 600);
            }
        }

    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {

            if (isDialog) {
                imageArticle.setVisibility(View.GONE);
                mWebviewPop = new WebView(getActivity());
                mWebviewPop.setVerticalScrollBarEnabled(false);
                mWebviewPop.setHorizontalScrollBarEnabled(false);
                mWebviewPop.setWebViewClient(new UriWebViewClient());
                mWebviewPop.setWebChromeClient(this);
                mWebviewPop.getSettings().setJavaScriptEnabled(true);
                mWebviewPop.getSettings().setDomStorageEnabled(true);
                mWebviewPop.getSettings().setSupportZoom(false);
                mWebviewPop.getSettings().setBuiltInZoomControls(false);
                mWebviewPop.getSettings().setSupportMultipleWindows(true);
                mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mContainer.addView(mWebviewPop);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(mWebviewPop);
                resultMsg.sendToTarget();
                scrollView.scrollTo(0, 0);
            }
            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.i(TAG, "onConsoleMessage: " + cm.message());

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {

        }
    }

    @Override
    public void onDestroyView(

    ) {
//        webviewDescription.stopLoading();
//        webviewDescription.loadData("", "text/html", "utf-8");
//        webviewDescription.reload();
//        webviewDescription.setWebChromeClient(null);
//        webviewDescription.setWebViewClient(null);
//        webviewDescription.removeAllViews();
//        webviewDescription.clearHistory();
//        webviewDescription.destroy();
//        webviewDescription = null;

        if(webviewDescription!=null){
            webviewDescription.destroy();
        }

        super.onDestroyView();
    }

    private void executeJavascript(String javascript, ValueCallback callback){
        webviewDescription.evaluateJavascript(javascript, callback);

    }
   /* private void playAudio() {
        mediaPlayer.start();
    }
    private void pauseAudio() {
        mediaPlayer.pause();
    }*/

    private void prepareAudio() {
        //String audioUrl = "https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3";

        // initializing media player

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(news.getUrlAudioNews());
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

}

