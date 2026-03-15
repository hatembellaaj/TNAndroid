package com.mdweb.tunnumerique.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class AproposFragment extends Fragment implements View.OnClickListener {

    private Dialog dialog;

    /**
     * empty public constructor
     */
    public AproposFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_apropos, container, false);
        // initialize views
        intView(rootView);
        return rootView;
    }

    /**
     * initialize view
     *
     * @param rootView present the root view in the xml file of fragment
     */
    public void intView(View rootView) {
        //find views
        LinearLayout call = (LinearLayout) rootView.findViewById(R.id.call);
        LinearLayout sendMail = (LinearLayout) rootView.findViewById(R.id.send_mail);
        LinearLayout tnWebsit = (LinearLayout) rootView.findViewById(R.id.tn_website);
        // listener of click button

        call.setOnClickListener(this);
        sendMail.setOnClickListener(this);
        tnWebsit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.call:
                         // phone call to mdweb
                dialogCall();
                break;
            case R.id.send_mail:
                Log.d("CallEmail", "Yeap");
                 // send mail to mdweb
                composeEmail(Constant.mailAdresse, "");
                break;
            case R.id.tn_website:
                // open mdweb web site
                openUrl(Communication.URL_MDWEB);
                break;
        }
    }

    /**
     * dialog upload photo
     */
    public void dialogCall() {
        //dismiss dialog
        if (dialog != null)
            dialog.dismiss();
        //create alert builder dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.DialogSlideAnim);
        View view = (getActivity()).getLayoutInflater().inflate(R.layout.dialog_call, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        TextView sendCall = (TextView) view.findViewById(R.id.send_call);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);


        // phone call to mdweb
        sendCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(Constant.telphoneNumber);
                // dismiss dilaog
                dialog.dismiss();
            }
        });
        // dismiss dialog
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
        //save parametre guide in SharedPreferences
        SessionManager.getInstance().setSavedGuide(getContext(), "1");
    }

    /**
     * phone call
     *
     * @param phone phone number
     */
    public void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    /**
     * send mail
     *
     * @param addresses mail address
     */

    public void composeEmail(String addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
       // intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        String[] TO = {addresses};
        intent.putExtra(Intent.EXTRA_EMAIL, TO);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);


        startActivity(
                Intent
                        .createChooser(intent,
                                ""));
    }



    /**
     * open url a new web page
     *
     * @param url link
     */
    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.getInstance().trackScreenView(getActivity(), getResources().getString(R.string.a_propos));

    }
}
