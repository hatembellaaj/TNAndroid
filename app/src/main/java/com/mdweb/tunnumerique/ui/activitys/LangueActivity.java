package com.mdweb.tunnumerique.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.LocaleHelper;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.shared.Constant;

public class LangueActivity extends BaseActivity {

    private RelativeLayout optionArabic;
    private RelativeLayout optionEnglish;
    private RelativeLayout optionFrench;
    private ImageView radioArabic;
    private ImageView radioEnglish;
    private ImageView radioFrench;
    private Button btnOk;

    private String selectedLanguage = Constant.FR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_langue);

        initViews();
        setupClickListeners();
        loadSavedLanguage();
    }

    private void initViews() {
        optionArabic = findViewById(R.id.option_arabic);
        optionEnglish = findViewById(R.id.option_english);
        optionFrench = findViewById(R.id.option_french);

        radioArabic = findViewById(R.id.radio_arabic);
        radioEnglish = findViewById(R.id.radio_english);
        radioFrench = findViewById(R.id.radio_french);

        btnOk = findViewById(R.id.btn_ok);
    }

    private void setupClickListeners() {
        optionArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(Constant.AR);
            }
        });

        optionEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(Constant.EN);
            }
        });

        optionFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(Constant.FR);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSelection();
            }
        });
    }

    private void selectLanguage(String languageCode) {
        resetAllOptions();
        selectedLanguage = languageCode;

        if (Constant.AR.equals(languageCode)) {
            radioArabic.setImageResource(R.drawable.ic_check_circle);
        } else if (Constant.EN.equals(languageCode)) {
            radioEnglish.setImageResource(R.drawable.ic_check_circle);
        } else {
            radioFrench.setImageResource(R.drawable.ic_check_circle);
        }
    }

    private void resetAllOptions() {
        radioArabic.setImageResource(android.R.color.transparent);
        radioEnglish.setImageResource(android.R.color.transparent);
        radioFrench.setImageResource(android.R.color.transparent);
    }

    private void confirmSelection() {
        SessionManager.getInstance().setCurrentLng(this, selectedLanguage);
        LocaleHelper.setLocale(this, selectedLanguage);
        Log.d("LangueActivity", "confirmSelection selectedLanguage=" + selectedLanguage);

        Intent intent = new Intent(LangueActivity.this, HomeTnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadSavedLanguage() {
        String savedLanguage = SessionManager.getInstance().getCurrentLang(this);
        if (savedLanguage == null || savedLanguage.isEmpty()) {
            savedLanguage = Constant.FR;
        }
        Log.d("LangueActivity", "loadSavedLanguage -> " + savedLanguage);
        selectLanguage(savedLanguage);
    }
}
