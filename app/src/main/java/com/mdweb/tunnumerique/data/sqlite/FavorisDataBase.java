package com.mdweb.tunnumerique.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mdweb.tunnumerique.data.model.News;

import java.util.ArrayList;
import java.util.List;

public class FavorisDataBase extends SQLiteOpenHelper {

    private static FavorisDataBase sInstance;

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "favorisDatabase";
    private static final String TABLE_NEWS = "news";

    private static final String KEY_NEWS_ID = "news_id";
    private static final String KEY_NEWS_TYPE_NEWS = "news_type_news";
    private static final String KEY_NEWS_TITLE = "news_title";
    private static final String KEY_NEWS_TYPE = "news_type";
    private static final String KEY_NEWS_DESCRIPTION = "news_description";
    private static final String KEY_NEWS_CONTENU = "news_contenu";
    private static final String KEY_NEWS_DARK = "news_dark";
    private static final String KEY_NEWS_COMMENTAIRE_ANDROID = "News_commentaire_android";
    private static final String KEY_NEWS_DATE = "news_date";
    private static final String KEY_NEWS_IMAGENAME = "news_image_name";
    private static final String KEY_NEWS_IMAGEURL = "news_image_url";
    private static final String KEY_NEWS_IMAGENAME_DETAIL = "news_imabeDetail_news";
    private static final String KEY_NEWS_AUDIO_URL = "news_audioUrl";
    private static final String KEY_NEWS_IMAGEURL_DETAIL = "news_imageDetail_url";
    private static final String KEY_NEWS_AUTHOR = "news_author";
    private static final String KEY_NEWS_SHAREURL = "news_shareUrl";
    private static final String KEY_NEWS_WORDS = "news_keyWords";
    private static final String KEY_NEWS_LNG = "news_lng";
    private static final String KEY_NEWS_PAYWALL = "news_is_paywall";

    public static synchronized FavorisDataBase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FavorisDataBase(context.getApplicationContext());
        }
        return sInstance;
    }

    public FavorisDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =============================
    // CREATE TABLE
    // =============================
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + KEY_NEWS_ID + " TEXT,"
                + KEY_NEWS_TYPE_NEWS + " INTEGER,"
                + KEY_NEWS_TITLE + " TEXT,"
                + KEY_NEWS_TYPE + " TEXT,"
                + KEY_NEWS_DESCRIPTION + " TEXT,"
                + KEY_NEWS_CONTENU + " TEXT,"
                + KEY_NEWS_DARK + " TEXT,"
                + KEY_NEWS_COMMENTAIRE_ANDROID + " TEXT,"
                + KEY_NEWS_DATE + " TEXT,"
                + KEY_NEWS_IMAGENAME + " TEXT,"
                + KEY_NEWS_IMAGEURL + " TEXT,"
                + KEY_NEWS_IMAGENAME_DETAIL + " TEXT,"
                + KEY_NEWS_AUDIO_URL + " TEXT,"
                + KEY_NEWS_IMAGEURL_DETAIL + " TEXT,"
                + KEY_NEWS_AUTHOR + " TEXT,"
                + KEY_NEWS_SHAREURL + " TEXT,"
                + KEY_NEWS_LNG + " TEXT,"
                + KEY_NEWS_WORDS + " TEXT,"
                + KEY_NEWS_PAYWALL + " INTEGER DEFAULT 0"
                + ")";

        db.execSQL(CREATE_TABLE);
    }

    // =============================
    // UPGRADE
    // =============================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE " + TABLE_NEWS +
                    " ADD COLUMN " + KEY_NEWS_PAYWALL + " INTEGER DEFAULT 0;");
        }
    }

    // =============================
    // ADD NEWS
    // =============================
    public void addNews(News news) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NEWS_ID, news.getIdNews());
        values.put(KEY_NEWS_TYPE_NEWS, news.getArtOrPubOrVid());
        values.put(KEY_NEWS_TITLE, news.getTitleNews());
        values.put(KEY_NEWS_TYPE, news.getTypeNews());
        values.put(KEY_NEWS_DESCRIPTION, news.getDescriptionNews());
        values.put(KEY_NEWS_CONTENU, news.getContenuNews());
        values.put(KEY_NEWS_DARK, news.getDark_Mode());
        values.put(KEY_NEWS_COMMENTAIRE_ANDROID, news.getNews_commentaire_android());
        values.put(KEY_NEWS_DATE, news.getDateNews());
        values.put(KEY_NEWS_IMAGENAME, news.getImageNameNews());
        values.put(KEY_NEWS_IMAGEURL, news.getImageUrlNews());
        values.put(KEY_NEWS_IMAGENAME_DETAIL, news.getImageNameDetailsNews());
        values.put(KEY_NEWS_AUDIO_URL, news.getUrlAudioNews());
        values.put(KEY_NEWS_IMAGEURL_DETAIL, news.getImageUrlDetailsNews());
        values.put(KEY_NEWS_AUTHOR, news.getAuthorNameNews());
        values.put(KEY_NEWS_SHAREURL, news.getShareUrlNews());
        values.put(KEY_NEWS_WORDS, news.getKeyWordsNews());
        values.put(KEY_NEWS_LNG, news.getNewsLng());
        values.put(KEY_NEWS_PAYWALL, news.isPaywall() ? 1 : 0);

        db.insert(TABLE_NEWS, null, values);
        db.close();
    }

    // =============================
    // GET SINGLE NEWS
    // =============================
    public News getNews(String id, int type) {

        SQLiteDatabase db = this.getReadableDatabase();
        News news = null;

        Cursor cursor = db.query(TABLE_NEWS, null,
                KEY_NEWS_ID + "=? AND " + KEY_NEWS_TYPE_NEWS + "=?",
                new String[]{id, String.valueOf(type)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            news = new News(
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NEWS_TYPE_NEWS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_CONTENU)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DARK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_COMMENTAIRE_ANDROID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGENAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGEURL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGENAME_DETAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGEURL_DETAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_AUDIO_URL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_AUTHOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_SHAREURL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_WORDS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_LNG)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NEWS_PAYWALL)) == 1
            );

            cursor.close();
        }

        return news;
    }

    // =============================
    // GET ALL NEWS
    // =============================
    public List<News> getAllNews(String lng) {

        List<News> newsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NEWS, null,
                KEY_NEWS_LNG + "=?",
                new String[]{lng},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {

                News news = new News();

                news.setIdNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_ID)));
                news.setArtOrPubOrVid(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NEWS_TYPE_NEWS)));
                news.setTitleNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_TITLE)));
                news.setTypeNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_TYPE)));
                news.setDescriptionNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DESCRIPTION)));
                news.setContenuNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_CONTENU)));
                news.setDark_Mode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DARK)));
                news.setNews_commentaire_android(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_COMMENTAIRE_ANDROID)));
                news.setDateNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_DATE)));
                news.setImageUrlNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGEURL)));
                news.setImageUrlDetailsNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_IMAGEURL_DETAIL)));
                news.setUrlAudioNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_AUDIO_URL)));
                news.setAuthorNameNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_AUTHOR)));
                news.setShareUrlNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_SHAREURL)));
                news.setKeyWordsNews(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_WORDS)));
                news.setNewsLng(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NEWS_LNG)));
                news.setPaywall(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NEWS_PAYWALL)) == 1);

                newsList.add(news);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return newsList;
    }

    // =============================
    // DELETE
    // =============================
    public void deleteNews(String idNews, int type) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEWS,
                KEY_NEWS_ID + "=? AND " + KEY_NEWS_TYPE_NEWS + "=?",
                new String[]{idNews, String.valueOf(type)});
        db.close();
    }

    // =============================
    // COUNT
    // =============================
    public int getNewsCount() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NEWS, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}