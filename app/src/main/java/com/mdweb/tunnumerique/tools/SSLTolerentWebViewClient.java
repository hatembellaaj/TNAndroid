package com.mdweb.tunnumerique.tools;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;


public class SSLTolerentWebViewClient extends WebViewClient {

    Context context;

    public SSLTolerentWebViewClient(Context context) {
        this.context = context;
    }

    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        String message = "Le certificat pour ce site n'est pas considéré comme fiable.";
        switch (error.getPrimaryError()) {
            case SslError.SSL_UNTRUSTED:
                message = "Le certificat pour ce site n'est pas considéré comme fiable.";
                break;
            case SslError.SSL_EXPIRED:
                message = "Le certificat pour ce site n'est pas considéré comme fiable.";
                break;
            case SslError.SSL_IDMISMATCH:
                message = "Le certificat pour ce site n'est pas considéré comme fiable.";
                break;
            case SslError.SSL_NOTYETVALID:
                message = "Le certificat pour ce site n'est pas considéré comme fiable.";
                break;
        }

        message += " Souhaitez-vous quand même continuer ?";
        alertDialog.setTitle("Erreur de certificat SSL");
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ignore SSL certificate errors
                handler.proceed();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                handler.cancel();
            }
        });
        alertDialog.show();
    }
}

