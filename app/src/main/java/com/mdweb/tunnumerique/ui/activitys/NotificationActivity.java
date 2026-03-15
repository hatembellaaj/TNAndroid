package com.mdweb.tunnumerique.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class NotificationActivity extends Activity {
    private WebView webView;
    private GifImageView gifImageView;
    GifDrawable gf;
    String openURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Enable the drawing of the whole document for Lollipop to get the whole WebView
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.pop_up_notification);

        gifImageView = (GifImageView) findViewById(R.id.imaa);

        //Load gif  from  assets
        try {
            gf = new GifDrawable(getAssets(), "loading.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifImageView.setImageDrawable(gf);
        // String openURL = "https://www.tunisienumerique.com/";
        webView = (WebView) findViewById(R.id.webview);
        openURL = getIntent().getStringExtra("openURL");
        // String openURL = "https://www.tunisienumerique.com/";
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageCommitVisible(WebView view, String url) {
                gifImageView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                // view.loadUrl(url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    gifImageView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            }
//
        });
        // webView.loadUrl("about:blank");
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearView();
        webView.loadUrl(openURL);

        gifImageView.setVisibility(View.VISIBLE);


    }

    @Override
    protected void onDestroy() {
       // SessionManager.getInstance().activity_onTop(getApplicationContext(), "");
        Log.d("LaunchUrlA", NotificationActivity.this.getClass().getName() +"destroy");
        webView.stopLoading();
        webView.loadData("", "text/html", "utf-8");
        webView.reload();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.removeAllViews();
        webView.clearHistory();
        webView.destroy();
        webView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager.getInstance().activity_onTop(NotificationActivity.this, NotificationActivity.this.getClass().getName());
        Log.d("LaunchUrlA", NotificationActivity.this.getClass().getName() +"create");

        if (!SessionManager.getInstance().getNotificationUrl(getApplicationContext()).equals("")) {
            Intent intent = getIntent();
            intent.putExtra("openURL", SessionManager.getInstance().getNotificationUrl(getApplicationContext()));
            SessionManager.getInstance().setNotificationUrl(this, "");
            finish();
            startActivity(intent);

        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LaunchUrlA", NotificationActivity.this.getClass().getName() +"pause");
        openURL = "";
    }

    public void onClick(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
