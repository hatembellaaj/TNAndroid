package com.mdweb.tunnumerique.ui.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.InvocationTargetException;

/**
 * Represents a DetailsArticle Activity .
 *
 * @author SOFIENE ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @version 1.0
 * @since 03-05-2017
 */
public class DetailsArticleActivity extends BaseActivity {
    private WebView webviewDescription;
    private Utils utils = new Utils(this);
    private TextView titleToolbar;
    private FloatingActionButton fab;
    private ScrollView scrollView;
    private int positionScrollView;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //  get intent
        Intent intent = getIntent();
        // find views
        final News news = (News) intent.getExtras().getSerializable("Article");
        int type = (int) intent.getExtras().getSerializable("type");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ImageView imageArticle = (ImageView) findViewById(R.id.image_details);
        RelativeLayout imageBack = (RelativeLayout) findViewById(R.id.back_image);
        titleToolbar = (TextView) findViewById(R.id.titleToolbar);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        positionScrollView = scrollView.getScrollY();
        // set title toolbar
        setToolbarTitle(type);
        ImageLoader.getInstance().displayImage(news.getImageUrlDetailsNews(), imageArticle, utils.getImageLoaderOptionDetailsNews());
        webviewDescription = (WebView) findViewById(R.id.details_article);
        Log.d("TestResultat",news.getContenuNews());
        if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webviewDescription.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
        webviewDescription.loadDataWithBaseURL(null, news.getContenuNews(), "text/html", "utf-8", null);
        webviewDescription.setWebChromeClient(new WebChromeClient() {
        });
        fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_to_bottom));
        fab.setAnimation(AnimationUtils.loadAnimation(this, R.anim.show_from_bottom));
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY(); // For ScrollView
                // For HorizontalScrollView
                // DO SOMETHING WITH THE SCROLL COORDINATES
                if (scrollY > positionScrollView) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reload web view
                webviewDescription.reload();
                // finish activity
                finish();
            }
        });

        // share article

    }


    /**
     * change toolbar title
     *
     * @param type type fragment
     */
    private void setToolbarTitle(int type) {
        switch (type) {
            case Constant.idArticle:
                titleToolbar.setText(getResources().getString(R.string.actualite_detail));
                break;
            case Constant.idDossier:
                titleToolbar.setText(getResources().getString(R.string.dossiers));
                break;
            case Constant.idPlusLus:
                titleToolbar.setText(getResources().getString(R.string.les_plus_lus));
                break;

            case Constant.idVideo:
                titleToolbar.setText(getResources().getString(R.string.videos));
                break;

            case Constant.idFavorite:
                titleToolbar.setText(getResources().getString(R.string.ma_liste));
                break;
        }
    }

    @Override
    protected void onResume() {
        // enabled fab share
      //  SessionManager.getInstance().activity_onTop(DetailsArticleActivity.this,getApplicationContext().getClass().getName());

        utils.checkSurvey();
        fab.setEnabled(true);
        //start video if exist in webView when app is resume
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


        super.onResume();
    }

    @Override
    protected void onPause() {
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
        webviewDescription.onPause();
        webviewDescription.pauseTimers();
        super.onPause();

    }



    @Override
    public void onBackPressed() {
        //webviewDescription.reload();
        finish();
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SessionManager.getInstance().activity_onTop(getApplicationContext(),DetailsArticleActivity.this.getClass().getName());

        webviewDescription.stopLoading();
        webviewDescription.loadData("", "text/html", "utf-8");
        webviewDescription.reload();
        webviewDescription.setWebChromeClient(null);
        webviewDescription.setWebViewClient(null);
        webviewDescription.removeAllViews();
        webviewDescription.clearHistory();
        webviewDescription.destroy();
        webviewDescription = null;
    }
}
