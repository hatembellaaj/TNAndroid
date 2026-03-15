package com.mdweb.tunnumerique.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.shared.Constant;

public class LangueActivity extends AppCompatActivity {

    // Déclaration des vues
    private RelativeLayout optionArabic, optionEnglish, optionFrench;    private View radioArabic, radioEnglish;
    private ImageView radioFrench;
    private Button btnOk;

    // Langue sélectionnée (par défaut: français)
    private String selectedLanguage = Constant.FR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_langue);

        // Initialisation des vues
        initViews();

        // Configuration des écouteurs de clics
        setupClickListeners();

        // Charger la langue sauvegardée (si elle existe)
        loadSavedLanguage();
    }

    /**
     * Initialise toutes les vues
     */
    private void initViews() {
        optionArabic = findViewById(R.id.option_arabic);
        optionEnglish = findViewById(R.id.option_english);
        optionFrench = findViewById(R.id.option_french);

        radioArabic = findViewById(R.id.radio_arabic);
        radioEnglish = findViewById(R.id.radio_english);
        radioFrench = findViewById(R.id.radio_french);

        btnOk = findViewById(R.id.btn_ok);
    }

    /**
     * Configure les écouteurs de clics pour toutes les options
     */
    private void setupClickListeners() {
        // Clic sur l'option arabe
        optionArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(Constant.AR);
            }
        });

        // Clic sur l'option anglaise
        optionEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(Constant.EN);
            }
        });

        // Clic sur l'option française
        optionFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(Constant.FR);
            }
        });

        // Clic sur le bouton OK
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSelection();
            }
        });
    }

    /**
     * Sélectionne une langue et met à jour l'interface
     * @param languageCode Code de la langue (Constant.AR, Constant.EN, Constant.FR)
     */
    private void selectLanguage(String languageCode) {
        // Réinitialiser toutes les options
        resetAllOptions();

        // Mettre à jour la langue sélectionnée
        selectedLanguage = languageCode;

        // Mettre en évidence l'option sélectionnée
        if (languageCode.equals(Constant.AR)) {
            radioArabic.setBackgroundResource(R.drawable.ic_check_circle);
        } else if (languageCode.equals(Constant.EN)) {
            radioEnglish.setBackgroundResource(R.drawable.ic_check_circle);
        } else if (languageCode.equals(Constant.FR)) {
            radioFrench.setImageResource(R.drawable.ic_check_circle);
        }
    }

    /**
     * Réinitialise toutes les options à l'état non sélectionné
     */
    private void resetAllOptions() {
        // Réinitialiser les boutons radio
        radioArabic.setBackgroundResource(R.drawable.radio_unchecked);
        radioEnglish.setBackgroundResource(R.drawable.radio_unchecked);
        radioFrench.setImageResource(R.drawable.radio_unchecked);
    }

    /**
     * Confirme la sélection et passe à l'écran suivant
     */
    private void confirmSelection() {
        // Sauvegarder la langue sélectionnée dans SessionManager
       SessionManager.getInstance().setCurrentLng(this, selectedLanguage);

        String lng = SessionManager.getInstance().getCurrentLang(this);

        // Afficher un message de confirmation
        String languageName = getLanguageName(selectedLanguage);
        Toast.makeText(this, "Langue sélectionnée : " + languageName,
                Toast.LENGTH_SHORT).show();

        // Naviguer vers l'activité principale
        Intent intent = new Intent(LangueActivity.this, HomeTnActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Charge la langue sauvegardée depuis SessionManager
     */
    private void loadSavedLanguage() {
        String savedLanguage = SessionManager.getInstance().getCurrentLang(this);
        selectLanguage(savedLanguage);
    }

    /**
     * Retourne le nom de la langue en fonction du code
     * @param languageCode Code de la langue
     * @return Nom de la langue
     */
    private String getLanguageName(String languageCode) {
        if (languageCode.equals(Constant.AR)) {
            return "العربية";
        } else if (languageCode.equals(Constant.EN)) {
            return "English";
        } else if (languageCode.equals(Constant.FR)) {
            return "Français";
        } else {
            return "Unknown";
        }
    }
}