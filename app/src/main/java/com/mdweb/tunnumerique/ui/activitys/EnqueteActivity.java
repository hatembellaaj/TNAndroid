package com.mdweb.tunnumerique.ui.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;

public class EnqueteActivity extends AppCompatActivity {

    private WebView enqueteView;
    private ImageView closeBtn;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_enquete);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        enqueteView = findViewById(R.id.webview_enquete);
        closeBtn = findViewById(R.id.closePop);
        String urlSurvey = getIntent().getStringExtra("urlSurvey");
      //  enqueteView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        enqueteView.loadUrl(urlSurvey);

        enqueteView.getSettings().setJavaScriptEnabled(true);
        //enqueteView.getSettings().setAllowFileAccess(true);
       // enqueteView.getSettings().setAllowFileAccess(true);
        enqueteView.getSettings().setDomStorageEnabled(true);
        enqueteView.addJavascriptInterface(new MyJavaScriptInterface(this), "ButtonRecognizer");

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        enqueteView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadEvent(clickListener());
                closeBtn.setVisibility(View.VISIBLE);
                Log.e("Error", "Finished");
            }


            private void loadEvent(String javascript) {
                enqueteView.loadUrl("javascript:" + javascript);
            }

            private String clickListener() {
                return getButtons() + "for(var i = 0; i < buttons.length; i++){\n" +
                        "\tbuttons[i].onclick = function(){ console.log('click worked.'); ButtonRecognizer.boundMethod('button clicked'); };\n" +
                        "}";
            }

            private String getButtons() {
                return "var buttons = document.getElementsByClassName('buttonsubmit'); console.log(buttons.length + ' buttons');\n";
            }
        });



    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void boundMethod(String html) {
            SessionManager.getInstance().setStateSurvey(EnqueteActivity.this, false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();

                }
            },4000);
//            new AlertDialog.Builder(ctx).setTitle("HTML").setMessage("Merci pour votre reponse")
//                    .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
        }

    }


    public void showPopUp() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_enquete, null);
        dialogBuilder.setView(view);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        Window window = alertDialog.getWindow();
        WebView enqueteView = view.findViewById(R.id.webview_enquete);

        enqueteView.loadUrl("http://mattel-satisfaction.mdweb-int.com");
        enqueteView.setWebChromeClient(new WebChromeClient() {
        });

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        alertDialog.show();
        //   alertDialog.getWindow().setDimAmount(0f);
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);


    }

    class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (error.toString() == "piglet")
                handler.cancel();
            else
                handler.proceed(); // Ignore SSL certificate errors
        }
    }

    @Override
    protected void onResume() {
        SessionManager.getInstance().activity_onTop(EnqueteActivity.this, EnqueteActivity.this.getClass().getName());
        Log.d("LaunchUrlA", EnqueteActivity.this.getClass().getName() +"create");

        super.onResume();
    }

    @Override
    protected void onDestroy() {
       // SessionManager.getInstance().activity_onTop(getApplicationContext(), "");
        Log.d("LaunchUrlA", EnqueteActivity.this.getClass().getName() +"create");

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("LaunchUrlA", EnqueteActivity.this.getClass().getName() +"Pause");

        SessionManager.getInstance().activity_onTop(getApplicationContext(), "");
        super.onPause();
    }
}
