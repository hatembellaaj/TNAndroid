package com.mdweb.tunnumerique.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.tools.LocaleHelper;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a DetailsVideo Activity .
 *
 * @author SOFIENE ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @version 1.0
 * @since 03-05-2017
 */
public class DetailsVideoActivity extends AppCompatActivity implements YouTubePlayerListener {
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
     private YouTubePlayer player;
    private News news;
    private TextView videoTitle;
    private Utils utils;
    private ImageView lngIcon;
    private String currentLng;
    private BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this, SessionManager.getInstance().getCurrentLang(this));

        setContentView(R.layout.activity_details_video);
        utils = new Utils(this);
        // find views
        videoTitle = (TextView) findViewById(R.id.video_title);
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .build();

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.setEnableAutomaticInitialization(false);
        youTubeView.initialize(this,true,  iFramePlayerOptions);

        final ImageView imageBack = (ImageView) findViewById(R.id.retour);

        lngIcon = findViewById(R.id.lng_icon);
        setIconLang();
        lngIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpLng();
            }
        });


        // get intent
        Intent intent = getIntent();
        news = (News) intent.getExtras().getSerializable("Article");
        videoTitle.setText(news.getTitleNews());
        youTubeView.getYouTubePlayerWhenReady(youTubePlayer -> {
            // do stuff with it
            youTubePlayer.loadVideo(getYoutubeVideoId(news.getDescriptionNews()),0);
        });

//        if (SessionManager.getInstance().getCurrentLang(this).equals("ar")) {
//            imageBack.setRotation(180f);
//        }
        // listener to click back button
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disabled back
                imageBack.setEnabled(false);
                // finish activity
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager.getInstance().activity_onTop(DetailsVideoActivity.this, DetailsVideoActivity.this.getClass().getName());
        utils.checkSurvey();
        Application.getInstance().trackScreenView(this, news.getTitleNews());
    }

    @Override
    protected void onDestroy() {
        //   SessionManager.getInstance().activity_onTop(getApplicationContext(),"");
        Log.d("LaunchUrlA", DetailsVideoActivity.this.getClass().getName() + "Destroy");

        //   SessionManager.getInstance().activity_onTop(getApplicationContext(), "");

        super.onDestroy();
        youTubeView.release();
    }




    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onApiChange( com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {

    }

    @Override
    public void onCurrentSecond(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, float v) {

    }

    @Override
    public void onError( com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer,  PlayerConstants.PlayerError playerError) {

    }

    @Override
    public void onPlaybackQualityChange( com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer,  PlayerConstants.PlaybackQuality playbackQuality) {

    }

    @Override
    public void onPlaybackRateChange( com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {

    }

    @Override
    public void onReady( com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {

    }

    @Override
    public void onStateChange( com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, PlayerConstants.PlayerState playerState) {

    }

    @Override
    public void onVideoDuration(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, float v) {

    }

    @Override
    public void onVideoId( com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer,  String s) {

    }

    @Override
    public void onVideoLoadedFraction(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, float v) {

    }




    /**
     * get youtube video from url
     *
     * @param youtubeUrl url video youtube
     * @return id video
     */
    public static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {

            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    @Override
    protected void onPause() {
        Log.d("LaunchUrlA", DetailsVideoActivity.this.getClass().getName() + "Pause");

        SessionManager.getInstance().activity_onTop(getApplicationContext(), "");

        super.onPause();
    }

    public void setIconLang() {
        switch (SessionManager.getInstance().getCurrentLang(this)) {
            case Constant.AR: {
                lngIcon.setImageResource(R.drawable.icon_ar_det);
            }
            break;
            case Constant.EN: {
                lngIcon.setImageResource(R.drawable.icon_en_det);

            }
            break;
            default: {
                lngIcon.setImageResource(R.drawable.icon_fr_det);

            }
        }

    }

    public void showPopUpLng() {
        Log.e("DisplayedPOP", getString(R.string.actualite));

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        TextView btnAr = view.findViewById(R.id.btnar);
        TextView btnFr = view.findViewById(R.id.btnfr);
        TextView btnEn = view.findViewById(R.id.btnEn);
        TextView arTxt = view.findViewById(R.id.ar);
        TextView enTxt = view.findViewById(R.id.en);
        TextView frTxt = view.findViewById(R.id.fr);
        Button btnValidate = view.findViewById(R.id.validateBtn);
        Typeface typoAr = Typeface.createFromAsset(getAssets(), "fonts/din-next_-ar-regular.otf");
        Typeface typoFr = Typeface.createFromAsset(getAssets(), "Roboto/Roboto-Regular.ttf");
        btnAr.setTypeface(typoAr);
        btnEn.setTypeface(typoFr);
        btnFr.setTypeface(typoFr);
        arTxt.setTypeface(typoAr);
        enTxt.setTypeface(typoFr);
        frTxt.setTypeface(typoFr);
        currentLng = SessionManager.getInstance().getCurrentLang(this);

        switch (currentLng) {
            case "ar": {
                btnAr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng));
                btnFr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(this, R.color.black_color));
                enTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                btnValidate.setTypeface(typoAr);

            }
            break;
            case "en": {
                btnAr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnFr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng));
                arTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                enTxt.setTextColor(ContextCompat.getColor(this, R.color.black_color));
                frTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                btnValidate.setTypeface(typoFr);

            }
            break;
            default: {
                btnValidate.setTypeface(typoFr);

                btnAr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                btnFr.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng));
                btnEn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                enTxt.setTextColor(ContextCompat.getColor(this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(this, R.color.black_color));


            }
            break;
        }


        btnAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAr.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng));
                btnFr.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng_off));
                btnEn.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng_off));
                arTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.black_color));
                enTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.text_color_off));
                frTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.text_color_off));
                currentLng = Constant.AR;

            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentLng.equals(SessionManager.getInstance().getCurrentLang(DetailsVideoActivity.this))) {
                    SessionManager.getInstance().setCurrentLng(DetailsVideoActivity.this, currentLng);
                    setIconLang();
                    Locale locale = new Locale(currentLng);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
//                    requireActivity().getBaseContext().getResources().updateConfiguration(config,
//                            requireActivity().getBaseContext().getResources().getDisplayMetrics());
//                    //   LocaleHelper.setLocale(requireActivity(), "ar");

                    onConfigurationChanged(config);
                }
                bottomSheetDialog.dismiss();
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });
        btnFr.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng));
            btnEn.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng_off));
            arTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.text_color_off));
            frTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.black_color));
            currentLng = Constant.FR;
        });

        btnEn.setOnClickListener(v -> {
            btnAr.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng_off));
            btnFr.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng_off));
            btnEn.setBackground(ContextCompat.getDrawable(DetailsVideoActivity.this, R.drawable.bg_selected_lng));
            arTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.text_color_off));
            enTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.black_color));
            frTxt.setTextColor(ContextCompat.getColor(DetailsVideoActivity.this, R.color.text_color_off));
            currentLng = Constant.EN;

        });
//
//        TextView message_poup = view.findViewById(R.id.message_poup);
//        message_poup.setVisibility(View.VISIBLE);
//        message_poup.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/SamsungSans-Regular.ttf"));
//        message_poup.setTextColor(ContextCompat.getColor(
//                this,
//                R.color.gris_text
//        ));
//
//        message_poup.setText(showedText);

        bottomSheetDialog.setOnCancelListener(dialog -> {
            //   dialogue_open = 0;

        });

        bottomSheetDialog.show();
//
//        if (dialogue_open < 1) {
//            dialogue_open = 1;
//            dialog.show();
//        }
    }

}
