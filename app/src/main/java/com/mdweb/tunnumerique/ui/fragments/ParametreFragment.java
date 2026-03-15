package com.mdweb.tunnumerique.ui.fragments;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.MobileInfoUtils;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.activitys.HomeTnActivity;
import com.mdweb.tunnumerique.ui.activitys.MainActivity;

import java.util.Locale;


/**
 * Paremetre Fragment - Version adaptée pour HomeTnActivity
 */
public class ParametreFragment extends Fragment implements View.OnClickListener {

    private TextView jourtxt;
    private RadioButton radioFr;
    private RadioButton radioAr;
    private RadioButton radioEn;
    private RadioButton radioday;
    private RadioButton radioNight;
    private SwitchCompat autoCheck;

    TextView modeChoiceTxt;
    TextView nuittxt;
    TextView autoTxt;
    TextView choixangue;
    private Handler handler;

    // Support pour les deux activités
    private MainActivity mainActivity;
    private HomeTnActivity homeTnActivity;
    private boolean isFromHomeTn = false;

    /**
     * empty public constructor
     */
    public ParametreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();

        // Déterminer quelle activité héberge le fragment
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            isFromHomeTn = false;
        } else if (getActivity() instanceof HomeTnActivity) {
            homeTnActivity = (HomeTnActivity) getActivity();
            isFromHomeTn = true;
        }

        if (getArguments() != null) {
            // Traiter les arguments si nécessaire
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_parmetre, container, false);

        radioFr = rootView.findViewById(R.id.fr);
        radioAr = rootView.findViewById(R.id.ar);
        radioEn = rootView.findViewById(R.id.en);
        radioday = rootView.findViewById(R.id.daybtn);
        radioNight = rootView.findViewById(R.id.nightbtn);
        autoCheck = rootView.findViewById(R.id.auto_switch);

        modeChoiceTxt = rootView.findViewById(R.id.choix_mode_text);
        jourtxt = rootView.findViewById(R.id.jours);
        nuittxt = rootView.findViewById(R.id.nuit);
        autoTxt = rootView.findViewById(R.id.auto);
        choixangue = rootView.findViewById(R.id.choix_langue);

        // Appeler changeColorArrow seulement si on est depuis MainActivity
        if (!isFromHomeTn) {
            changeColorArrow();
        }

        // Set Typo
        Typeface typoAr = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/din-next_-ar-regular.otf");
        Typeface typoFr = Typeface.createFromAsset(requireActivity().getAssets(), "Roboto/Roboto-Regular.ttf");

        if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("ar")) {
            setTypo(typoAr);
        } else {
            setTypo(typoFr);
        }

        radioAr.setTypeface(typoAr);
        radioEn.setTypeface(typoFr);
        radioFr.setTypeface(typoFr);

        // Init check lng
        switch (SessionManager.getInstance().getCurrentLang(requireActivity())) {
            case "ar": {
                radioAr.setChecked(true);
            }
            break;
            case "en": {
                radioEn.setChecked(true);
            }
            break;
            default: {
                radioFr.setChecked(true);
            }
            break;
        }

        if (SessionManager.getInstance().isModeAuto(requireActivity())) {
            autoCheck.setChecked(true);
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                radioNight.setChecked(true);
                SessionManager.getInstance().setCurrentMode(requireContext(), "night");
                Log.e("ModeActivated", "night");
                Log.e("ModeActivated", nightModeFlags + "");
            } else {
                radioday.setChecked(true);
                SessionManager.getInstance().setCurrentMode(requireContext(), "day");
                Log.e("ModeActivated", "day");
                Log.e("ModeActivated", nightModeFlags + "");
            }
        } else {
            autoCheck.setChecked(false);
            switch (SessionManager.getInstance().getCurrentMode(requireActivity())) {
                case "day":
                    radioday.setChecked(true);
                    break;
                case "night":
                    radioNight.setChecked(true);
                    break;
            }
        }

        radioEn.setOnClickListener(this);
        radioAr.setOnClickListener(this);
        radioFr.setOnClickListener(this);
        radioday.setOnClickListener(this);
        radioNight.setOnClickListener(this);

        autoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                Log.e("ModeActivated", "Clicked");

                if (isChecked) {
                    Log.e("ModeActivated", "checked" + isChecked);
                    SessionManager.getInstance().setModeAuomatique(requireContext(), true);

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                        radioNight.setChecked(true);
                        SessionManager.getInstance().setCurrentMode(requireContext(), "night");
                    } else {
                        radioday.setChecked(true);
                        SessionManager.getInstance().setCurrentMode(requireContext(), "day");
                    }
                } else {
                    Log.e("ModeActivated", "nochecked" + isChecked);
                    SessionManager.getInstance().setModeAuomatique(requireContext(), false);
                }
            }
        });

        // Initialize views
        intView(rootView);

        return rootView;
    }

    /**
     * Initialize view
     */
    public void intView(View rootView) {
        // Initialisation des vues si nécessaire
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.getInstance().trackScreenView(getActivity(), getResources().getString(R.string.paremtre));
    }

    public void jumpStartInterface() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Pour continuer à recevoir des notifications même lorsque l'application est fermée, veuillez activer le mode protégé de l'application Tunisie Numérique.");
            builder.setPositiveButton("Activer",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MobileInfoUtils.jumpStartInterface(getActivity());
                        }
                    });
            builder.setNegativeButton("Ne plus afficher",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SessionManager.getInstance().setPopNotif(getActivity(), false);
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(true);
            builder.create().show();
        } catch (Exception e) {
        }
    }

    public void onRadioButtonClicked(View view) {
    }

    @Override
    public void onClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.fr:
                if (checked) {
                    SessionManager.getInstance().setCurrentLng(requireActivity(), "fr");
                    String lang = "fr";
                    Locale locale = new Locale(lang);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    Application.getInstance().trackScreenView(requireActivity(), Constant.FRENCH);
                    requireActivity().onConfigurationChanged(config);
                }
                break;

            case R.id.ar:
                if (checked) {
                    SessionManager.getInstance().setCurrentLng(requireActivity(), "ar");
                    String lang = "ar";
                    Locale locale = new Locale(lang);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    Application.getInstance().trackScreenView(requireActivity(), Constant.ARABIC);
                    requireActivity().onConfigurationChanged(config);
                    Log.d("LangueTn", "Ar");
                }
                break;

            case R.id.en:
                if (checked) {
                    SessionManager.getInstance().setCurrentLng(requireActivity(), "en");
                    String lang = "en";
                    Locale locale = new Locale(lang);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    Application.getInstance().trackScreenView(requireActivity(), Constant.ENGLISH);
                    requireActivity().onConfigurationChanged(config);
                    Log.d("LangueTn", "En");
                }
                break;

            case R.id.daybtn:
                if (SessionManager.getInstance().getCurrentMode(requireActivity()).equals("night")) {
                    SessionManager.getInstance().setCurrentMode(requireContext(), "day");
                    autoCheck.setChecked(false);
                    SessionManager.getInstance().setModeAuomatique(requireContext(), false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;

            case R.id.nightbtn:
                if (SessionManager.getInstance().getCurrentMode(requireActivity()).equals("day")) {
                    SessionManager.getInstance().setCurrentMode(requireContext(), "night");
                    SessionManager.getInstance().setModeAuomatique(requireContext(), false);
                    autoCheck.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;
        }
    }

    public void setTypo(Typeface typo) {
        modeChoiceTxt.setTypeface(typo);
        jourtxt.setTypeface(typo);
        nuittxt.setTypeface(typo);
        autoTxt.setTypeface(typo);
        choixangue.setTypeface(typo);
    }

    /**
     * Change la couleur de la flèche - Seulement pour MainActivity
     */
    public void changeColorArrow() {
        // Cette méthode ne s'exécute que si on est depuis MainActivity
        if (!isFromHomeTn && mainActivity != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mainActivity.getLngIcon().setVisibility(View.GONE);
                        mainActivity.changeDrawerColor(R.color.green_color_tittle);
                    } catch (Exception e) {
                        Log.e("ParametreFragment", "Erreur changeColorArrow: " + e.getMessage());
                    }
                }
            }, Constant.duration);
        }
    }
}