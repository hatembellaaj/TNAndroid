package com.mdweb.tunnumerique.data.parsers;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Blague;
import com.mdweb.tunnumerique.data.model.Categories;
import com.mdweb.tunnumerique.data.model.Country;
import com.mdweb.tunnumerique.data.model.DateSalat;
import com.mdweb.tunnumerique.data.model.Gouvernorat;
import com.mdweb.tunnumerique.data.model.HorairePriere;
import com.mdweb.tunnumerique.data.model.HoraireSalat;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DataParser {

    // ==============================
    // getListNews
    // ==============================
    public List<News> getListNews(String response) {
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray _news = jsonObject.getJSONArray("news");
            for (int i = 0; i < _news.length(); i++) {
                JSONObject _jsonObject = _news.getJSONObject(i);
                News news = new News();

                news.setIdNews(_jsonObject.getString("News_ID"));
                news.setTitleNews(_jsonObject.getString("News_Titre"));
                news.setImageUrlNews(_jsonObject.getString("News_Url_Image"));
                news.setImageNameNews(_jsonObject.getString("News_Name_Image"));
                news.setImageUrlDetailsNews(_jsonObject.getString("News_Url_Image_Details"));
                news.setImageNameDetailsNews(_jsonObject.getString("News_Name_Image_Details"));
                if (_jsonObject.has("News_Url_audio")) {
                    news.setUrlAudioNews(_jsonObject.getString("News_Url_audio"));
                } else {
                    news.setUrlAudioNews("");
                }
                news.setShareUrlNews(_jsonObject.getString("News_Url_Partage"));
                if (_jsonObject.has("News_Format_Date")) {
                    news.setDateNews(_jsonObject.getString("News_Format_Date"));
                } else {
                    news.setDateNews(_jsonObject.getString("News_Date"));
                }
                news.setDescriptionNews(_jsonObject.getString("News_Description"));
                news.setContenuNews(_jsonObject.getString("News_Contenu"));
                if (_jsonObject.has("Dark_Mode"))
                    news.setDark_Mode(_jsonObject.getString("Dark_Mode"));
                news.setNews_commentaire_android("");
                if (_jsonObject.has("News_list_category")) {
                    news.setTypeNews(_jsonObject.getString("News_list_category"));
                } else {
                    news.setTypeNews("");
                }
                news.setAuthorNameNews(_jsonObject.getString("News_Auteur_Nom"));
                news.setArtOrPubOrVid(Constant.isArticle);
                news.setKeyWordsNews(_jsonObject.getString("News_Liste_Mot_Cle"));

                // ✅ is_paywall
                if (_jsonObject.has("is_paywall")) {
                    news.setPaywall(_jsonObject.getBoolean("is_paywall"));
                } else {
                    news.setPaywall(false);
                }

                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    // ==============================
    // getListHorairePriere
    // ==============================
    public List<HorairePriere> getListHorairePriere(String response) {
        List<HorairePriere> horairePriereList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);
                HorairePriere horairePriere = new HorairePriere();
                horairePriere.setDateMillidai(_jsonObject.getString("Date_Millidai_Horaire"));
                horairePriere.setFajer(_jsonObject.getString("Fajr_Horaires"));
                horairePriere.setSobeh(_jsonObject.getString("Sobeh_Horaires"));
                horairePriere.setDhohr(_jsonObject.getString("Dhohr_Horaires"));
                horairePriere.setAser(_jsonObject.getString("Asr_Horaires"));
                horairePriere.setMaghreb(_jsonObject.getString("Maghreb_Horaires"));
                horairePriere.setIcha(_jsonObject.getString("Icha_Horaires"));
                horairePriereList.add(horairePriere);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return horairePriereList;
    }

    // ==============================
    // getListCategories
    // ==============================
    public List<Categories> getListCategories(String response, int state) {
        List<Categories> categoriesList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("menu");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);
                Categories categories = new Categories();
                categories.setIdCategories(_jsonObject.getString("menu_id"));
                String decodedName = _jsonObject.getString("menu_titre");
                if (state == 1) {
                    String name = "";
                    try {
                        name = new String(_jsonObject.getString("menu_titre").getBytes("ISO-8859-1"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    decodedName = Html.fromHtml(name).toString();
                }
                categories.setTitleCategories(decodedName);
                categories.setTitleUrlCategories(_jsonObject.getString("menu_titre_url"));
                categories.setIconUrl(_jsonObject.getString("menu_icone_url"));
                categories.setIconTitle(_jsonObject.getString("menu_icone_titre"));
                categories.setIconEnabledUrl(_jsonObject.getString("menu_icone_active_url"));
                categories.setIconEnabledTitle(_jsonObject.getString("menu_icone_active_titre"));
                categories.setBgImageFilterUrl(_jsonObject.getString("menu_image_url"));
                categories.setBgImageFilterName(_jsonObject.getString("menu_image_titre"));
                categories.setColorR(Integer.parseInt(_jsonObject.getString("menu_couleur_r")));
                categories.setColorG(Integer.parseInt(_jsonObject.getString("menu_couleur_g")));
                categories.setColorB(Integer.parseInt(_jsonObject.getString("menu_couleur_b")));
                categoriesList.add(categories);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categoriesList;
    }

    // ==============================
    // getBlagueList
    // ==============================
    public List<Blague> getBlagueList(String response) {
        List<Blague> blagues = new ArrayList<>();
        Blague bPub = new Blague();
        bPub.setArtOrPubOrVid(2);
        blagues.add(bPub);
        try {
            JSONObject x = new JSONObject(response);
            JSONArray jsonArray = x.getJSONArray("blagues");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);
                Blague blague = new Blague();
                blague.setIdBlague(_jsonObject.getString("Blague_ID"));
                String tittle = _jsonObject.getString("Blague_Titre");
                String contenu = _jsonObject.getString("Blague_Contenu");
                blague.setTitleBlague(tittle);
                blague.setContenuBlagues(contenu);
                blague.setDateBlague(_jsonObject.getString("Blague_Date"));
                blague.setBlagueUrl(_jsonObject.getString("Blague_Url_Partage"));
                blague.setDescriptionBlagues(_jsonObject.getString("Blague_Description"));
                blague.setNoteBlagues(_jsonObject.getString("Blague_Ratings"));
                blagues.add(blague);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return blagues;
    }

    // ==============================
    // getListFetchNews
    // ==============================
    public List<News> getListFetchNews(String response) {
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject x = new JSONObject(response);
            JSONArray jsonArray = x.getJSONArray("news");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);

                News news = new News();
                news.setIdNews(_jsonObject.getString("News_ID"));
                news.setTitleNews(_jsonObject.getString("News_Titre"));
                news.setImageUrlNews(_jsonObject.getString("News_Url_Image"));
                news.setImageNameNews(_jsonObject.getString("News_Name_Image"));
                news.setImageUrlDetailsNews(_jsonObject.getString("News_Url_Image_Details"));
                news.setImageNameDetailsNews(_jsonObject.getString("News_Name_Image_Details"));
                news.setShareUrlNews(_jsonObject.getString("News_Url_Partage"));
                if (_jsonObject.has("News_Format_Date")) {
                    news.setDateNews(_jsonObject.getString("News_Format_Date"));
                } else {
                    news.setDateNews(_jsonObject.getString("News_Date"));
                }
                if (_jsonObject.has("News_Url_audio")) {
                    news.setUrlAudioNews(_jsonObject.getString("News_Url_audio"));
                } else {
                    news.setUrlAudioNews("");
                }
                news.setDescriptionNews(_jsonObject.getString("News_Description"));
                news.setContenuNews(_jsonObject.getString("News_Contenu"));
                if (_jsonObject.has("Dark_Mode")) {
                    news.setDark_Mode(_jsonObject.getString("Dark_Mode"));
                }
                if (_jsonObject.has("News_commentaire_android"))
                    news.setNews_commentaire_android(_jsonObject.getString("News_commentaire_android"));
                news.setTypeNews(_jsonObject.getString("News_list_category"));
                news.setAuthorNameNews(_jsonObject.getString("News_Auteur_Nom"));
                news.setArtOrPubOrVid(Constant.isArticle);
                news.setKeyWordsNews(_jsonObject.getString("News_Liste_Mot_Cle"));

                // ✅ is_paywall
                if (_jsonObject.has("is_paywall")) {
                    news.setPaywall(_jsonObject.getBoolean("is_paywall"));
                } else {
                    news.setPaywall(false);
                }

                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    // ==============================
    // getListNewsFromLocal
    // ==============================
    public List<News> getListNewsFromLocal(String response) {
        List<News> newsList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);

                News news = new News();
                news.setArtOrPubOrVid(_jsonObject.getInt("artOrPubOrVid"));
                news.setIdNews(_jsonObject.getString("idNews"));
                news.setTitleNews(_jsonObject.getString("titleNews"));
                news.setImageUrlNews(_jsonObject.getString("imageUrlNews"));
                news.setImageNameNews(_jsonObject.getString("imageNameNews"));
                news.setImageUrlDetailsNews(_jsonObject.getString("imageUrlDetailsNews"));
                news.setImageNameDetailsNews(_jsonObject.getString("imageNameDetailsNews"));
                news.setShareUrlNews(_jsonObject.getString("shareUrlNews"));
                if (_jsonObject.has("News_Format_Date")) {
                    news.setDateNews(_jsonObject.getString("News_Format_Date"));
                } else {
                    news.setDateNews(_jsonObject.getString("dateNews"));
                }
                if (_jsonObject.has("audioUrlNews")) {
                    news.setUrlAudioNews(_jsonObject.getString("audioUrlNews"));
                } else {
                    news.setUrlAudioNews("");
                }
                news.setDescriptionNews(_jsonObject.getString("descriptionNews"));
                news.setContenuNews(_jsonObject.getString("contenuNews"));
                if (_jsonObject.has("Dark_Mode"))
                    news.setDark_Mode(_jsonObject.getString("Dark_Mode"));
                if (_jsonObject.has("News_commentaire_android"))
                    news.setNews_commentaire_android(_jsonObject.getString("News_commentaire_android"));
                news.setTypeNews(_jsonObject.getString("News_list_category"));
                news.setAuthorNameNews(_jsonObject.getString("authorNameNews"));

                // ✅ is_paywall
                if (_jsonObject.has("is_paywall")) {
                    news.setPaywall(_jsonObject.getBoolean("is_paywall"));
                } else {
                    news.setPaywall(false);
                }

                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    // ==============================
    // getListDossier
    // ==============================
    public List<News> getListDossier(String response) {
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("news");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _jsonObject = jsonArray.getJSONObject(i);

                News news = new News();
                news.setIdNews(_jsonObject.getString("News_ID"));
                news.setTitleNews(_jsonObject.getString("News_Titre"));
                news.setImageUrlNews(_jsonObject.getString("News_Url_Image"));
                news.setImageNameNews(_jsonObject.getString("News_Name_Image"));
                news.setImageUrlDetailsNews(_jsonObject.getString("News_Url_Image_Details"));
                news.setImageNameDetailsNews(_jsonObject.getString("News_Name_Image_Details"));
                news.setShareUrlNews(_jsonObject.getString("News_Url_Partage"));
                if (_jsonObject.has("News_Format_Date")) {
                    news.setDateNews(_jsonObject.getString("News_Format_Date"));
                } else {
                    news.setDateNews(_jsonObject.getString("News_Date"));
                }
                if (_jsonObject.has("News_Url_audio")) {
                    news.setUrlAudioNews(_jsonObject.getString("News_Url_audio"));
                } else {
                    news.setUrlAudioNews("");
                }
                news.setDescriptionNews(_jsonObject.getString("News_Description"));
                news.setContenuNews(_jsonObject.getString("News_Contenu"));
                if (_jsonObject.has("Dark_Mode"))
                    news.setDark_Mode(_jsonObject.getString("Dark_Mode"));
                if (_jsonObject.has("News_commentaire_android"))
                    news.setNews_commentaire_android(_jsonObject.getString("News_commentaire_android"));
                if (_jsonObject.has("News_list_category")) {
                    news.setTypeNews(_jsonObject.getString("News_list_category"));
                } else {
                    news.setTypeNews("");
                }
                news.setAuthorNameNews(_jsonObject.getString("News_Auteur_Nom"));
                news.setArtOrPubOrVid(Constant.isArticle);
                news.setKeyWordsNews(_jsonObject.getString("News_Liste_Mot_Cle"));

                // ✅ is_paywall
                if (_jsonObject.has("is_paywall")) {
                    news.setPaywall(_jsonObject.getBoolean("is_paywall"));
                } else {
                    news.setPaywall(false);
                }

                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DossierContenu", e.getMessage() + "");
        }
        Log.d("DossierContenu", newsList.size() + "");
        return newsList;
    }

    // ==============================
    // getListVideos
    // ==============================
    public List<News> getListVideos(String response) {
        List<News> newsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("video");
            for (int i = 0; i < jsonArray.length(); i++) {
                News news = new News();
                JSONObject _jsonObject = jsonArray.getJSONObject(i);
                news.setIdNews(String.valueOf(i));
                news.setArtOrPubOrVid(Constant.isVideo);
                news.setDescriptionNews(Communication.URL_YOUTUBE + _jsonObject.getString("id"));
                news.setShareUrlNews(Communication.URL_YOUTUBE + _jsonObject.getString("id"));
                String tittle = _jsonObject.getString("title");
                news.setTitleNews(tittle.replace("&quot;", "\""));
                news.setImageUrlNews(_jsonObject.getString("image"));
                if (_jsonObject.has("date")) {
                    news.setDateNews(_jsonObject.getString("date"));
                } else {
                    news.setDateNews(_jsonObject.getString("published"));
                }
                news.setKeyWordsNews(",");
                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    // ==============================
    // getListDate
    // ==============================
    public ArrayList<DateSalat> getListDate(String response, Context context) {
        ArrayList<DateSalat> dateArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray _date = jsonObject.getJSONArray("Priere");
            for (int j = 0; j < _date.length(); j++) {
                JSONObject _jsonObjectDate = _date.getJSONObject(j);
                DateSalat _dateSalat = new DateSalat();
                _dateSalat.setDateHijriText(_jsonObjectDate.getString("date_hejri_txt"));
                _dateSalat.setDateMiladi(_jsonObjectDate.getString("date_meladi"));
                _dateSalat.setDateMiladiText(_jsonObjectDate.getString("date_miladi_txt"));
                _dateSalat.setNewsAr(_jsonObjectDate.getString("newsAr"));

                JSONArray _governerat = _jsonObjectDate.getJSONArray("gouvernorat");
                ArrayList<Gouvernorat> gouvernoratsList = new ArrayList<>();
                for (int i = 0; i < _governerat.length(); i++) {
                    JSONObject _jsonObjectGov = _governerat.getJSONObject(i);
                    Gouvernorat gouvernorat = new Gouvernorat();
                    gouvernorat.setId(_jsonObjectGov.getInt("idGouvernorat"));
                    gouvernorat.setNomGouvernoratAr(_jsonObjectGov.getString("nomGouvernoratAr"));
                    gouvernorat.setNomGouvernoratFr(_jsonObjectGov.getString("nomGouvernoratFr"));
                    JSONArray _salat = _jsonObjectGov.getJSONArray("priere");
                    ArrayList<HoraireSalat> horaireList = new ArrayList<>();
                    for (int k = 0; k < _salat.length(); k++) {
                        JSONObject _jsonObjectSalat = _salat.getJSONObject(k);
                        HoraireSalat horaireSalat = new HoraireSalat();
                        switch (k) {
                            case 1:
                                horaireSalat.setNomSalat(context.getString(R.string.fajr));
                                break;
                            case 2:
                                horaireSalat.setNomSalat(context.getString(R.string.dhohr));
                                break;
                            case 3:
                                horaireSalat.setNomSalat(context.getString(R.string.asr));
                                break;
                            case 4:
                                horaireSalat.setNomSalat(context.getString(R.string.maghreb));
                                break;
                            case 5:
                                horaireSalat.setNomSalat(context.getString(R.string.isha));
                                break;
                        }
                        horaireSalat.setId(_jsonObjectSalat.getInt("idPriere"));
                        horaireSalat.setNomSalat(_jsonObjectSalat.getString("nomPriereAr"));
                        horaireSalat.setHeure(_jsonObjectSalat.getString("heurePriere"));
                        horaireList.add(horaireSalat);
                    }
                    gouvernorat.setmListHoraire(horaireList);
                    gouvernoratsList.add(gouvernorat);
                }
                _dateSalat.setmListeGouvernorat(gouvernoratsList);
                dateArrayList.add(_dateSalat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateArrayList;
    }

    // ==============================
    // getListCountries
    // ==============================
    public ArrayList<Country> getListCountries(String response, Context context) {
        ArrayList<Country> countriesArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray countries = jsonObject.getJSONArray("pays");
            for (int j = 0; j < countries.length(); j++) {
                JSONObject _jsonObjectCountry = countries.getJSONObject(j);
                Country country = new Country();
                country.setIdPays(_jsonObjectCountry.getString("idPays"));
                country.setNomPaysAr(_jsonObjectCountry.getString("nomPaysAr"));
                country.setNomPaysFr(_jsonObjectCountry.getString("nomPaysFr"));
                country.setPaysIconURL(_jsonObjectCountry.getString("paysIconURL"));
                country.setIconName(_jsonObjectCountry.getString("iconName"));
                country.setPriereURL(_jsonObjectCountry.getString("PriereURL"));
                country.setPriereFileName(_jsonObjectCountry.getString("priere_file_name"));
                country.setNomZoneAdminisrativeAr(_jsonObjectCountry.getString("nomZoneAdminisrativeAr"));
                country.setNomZoneAdminisrativeFr(_jsonObjectCountry.getString("nomZoneAdminisrativeFr"));
                country.setNomZoneAdminisrativeEn(_jsonObjectCountry.getString("nomZoneAdminisrativeEn"));
                countriesArrayList.add(country);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("countries JsonArray", e.getMessage() + "");
        }
        return countriesArrayList;
    }
}