package com.mdweb.tunnumerique.tools.shared;

// file for the url, json

public class Communication {


//    // url publicity
//    public static final String URL_PUB = "https://www.tunisienumerique.com/jsondata/ads.html";
//    // youtube base url
//    public static final String URL_YOUTUBE = "https://www.youtube.com/watch?v=";
//    // url upload image to server
//    public static final String URL_UPLOAD_IMAGE = "https://www.tunisienumerique.com/AppsUploads/upload.php";
//    // mdweb url
//    public static final String URL_MDWEB = "http://www.mdsoft-int.com";
//    //
//    public static final String URL_NEWS_INIT = "https://www.tunisienumerique.com/jsondata/jsonfetch.php?type=0";
//    public static final String FILE_NEWS_INIT ="init.json";
//
//    public static final String URL_REFRESH = "https://www.tunisienumerique.com/jsondata/jsonfetch.php?type=1&id_article=";
//    public static final String FILE_NAME_URL_REFRESH ="refresh.json";
//
//    public static final String URL_SCROLL_DOWN = "https://www.tunisienumerique.com/jsondata/jsonfetch.php?type=2&id_article=";
//    public static final String FILE_SCROLL_DOWN ="scrollDown.json";
//
//    public static final String URL_DOSSIER = "https://www.tunisienumerique.com/jsondata/jsondossiers.php";
//    public static final String FILE_NAME_DOSSIER ="dossier.json";
//
//    public static final String URL_PLUS_LUS = "https://www.tunisienumerique.com/jsondata/popular.json";
//    public static final String FILE_NAME_PLUS_LUS ="popular.json";
//
//    public static final String URL_CATEGORIES = " https://www.tunisienumerique.com/jsondata/categories.json";
//    public static final String FILE_NAME_CATEGORIES ="categories.json";
//
//    public static final String URL_PRIERE = "https://www.tunisienumerique.com/priere/priere";
//    public static final String FILE_NAME_PIERE ="priere";
//
//    public static final String URL_VIDEO = "https://www.tunisienumerique.com/videoTn/";
//    public static final String FILE_NAME_VIDEO ="video";
//    public static final String JSON =".json";
//    public static final String TEXT =".txt";


    // url publicity
    public static final String URL_PUB = "https://www.tunisienumerique.com/jsondata/ads.html";
    // youtube base url
    public static final String URL_YOUTUBE = "https://www.youtube.com/watch?v=";
    // url upload image to server
    public static final String URL_UPLOAD_IMAGE = "https://www.tunisienumerique.com/AppsUploads/upload.php";
    // mdweb url
    public static final String URL_MDWEB = "http://www.mdsoft-int.com";
    //
    // public static final String URL_NEWS_INIT = "https://jsondata.tunisienumerique.com/jsonfetch.php?type=0";
    // https://www.tunisienumerique.com/results.json
    // public static final String URL_NEWS_INIT = "https://jsondata.tunisienumerique.com/results.json";


    //Get actulaité from server  in multilanguage

    //fr
    public static final String URL_NEWS_INIT = "https://preprod.tunisienumerique.com/results.json";
    public static final String FILE_NEWS_INIT = "init.json";
    // ar
    public static final String URL_NEWS_INIT_AR = "https://arabe.tunisienumerique.com/results.json";
    public static final String FILE_NEWS_INIT_AR = "initar.json";

    //En
    public static final String URL_NEWS_INIT_EN = "https://news-tunisia.tunisienumerique.com/results.json";
    public static final String FILE_NEWS_INIT_EN = "initen.json";


    public static final String URL_REFRESH = "https://jsondata.tunisienumerique.com/jsonfetch.php?type=1&id_article=";
    public static final String FILE_NAME_URL_REFRESH = "refresh.json";

    public static final String URL_SCROLL_DOWN = "https://jsondata.tunisienumerique.com/jsonfetch.php?type=2&id_article=";
    public static final String FILE_SCROLL_DOWN = "scrollDown.json";

    public static final String URL_DOSSIER = "https://jsondata.tunisienumerique.com/dossiers.json";
    public static final String FILE_NAME_DOSSIER = "dossier.json";

    //http://196.203.63.32/nps/arrakmia/dossiers.json

    public static final String URL_DOSSIER_AR = "https://arabe.tunisienumerique.com/dossiers.json";
    public static final String FILE_NAME_DOSSIER_AR = "dossier_ar.json";

    //dossier
   // http://196.203.63.32/nps/anglais/dossiers.json
    public static final String URL_DOSSIER_EN = "https://news-tunisia.tunisienumerique.com/jsondata/dossiers.json";
    public static final String FILE_NAME_DOSSIER_EN = "dossier_en.json";

    public static final String URL_PLUS_LUS = "https://www.tunisienumerique.com/jsondata/popular.json";
    public static final String FILE_NAME_PLUS_LUS = "popular.json";

    //http://196.203.63.32/nps/arrakmia/popular.json

    public static final String URL_PLUS_LUS_AR = "https://arabe.tunisienumerique.com/jsondata/popular.json";
    public static final String FILE_NAME_PLUS_LUS_AR = "popular_ar.json";

    public static final String URL_PLUS_LUS_EN = "https://news-tunisia.tunisienumerique.com/jsondata/popular.json";
    public static final String FILE_NAME_PLUS_LUS_EN = "popular_en.json";

    //Catgories
    public static final String URL_CATEGORIES = "https://preprod.tunisienumerique.com/jsondata/categories.json ";
    public static final String FILE_NAME_CATEGORIES = "categories.json";

    public static final String URL_CATEGORIES_AR = "https://news-tunisia.tunisienumerique.com/jsondata/categories.json";
    public static final String FILE_NAME_CATEGORIES_AR = "categoriesar.json";

    // en
    public static final String URL_CATEGORIES_EN = "https://news-tunisia.tunisienumerique.com/jsondata/categories.json";
    public static final String FILE_NAME_CATEGORIES_EN = "categoriesen.json";

    //    public static final String URL_PRIERE = "https://www.tunisienumerique.com/priere/priere";
//    public static final String FILE_NAME_PIERE ="priere";

    // Prayer times in Tunisia
    public static final String URL_PRIERE = "http://196.203.63.50/Isslamyat/web/json/priere.json";
    public static final String FILE_NAME_PIERE = "priere.json";

    // supported countries  with their prayer times details
    public static final String URL_SUPPORTED_COUNTRIES = "http://mdweb-int.com/appTN/prieretn/country.json";
    public static final String FILE_NAME_SUPPORTED_COUNTRIES = "country.json";

    // Prayer times in Frensh
    public static final String URL_PRIERE_FR = "http://196.203.63.50/Isslamyat/web/json/priere.json";
    public static final String FILE_NAME_PIERE_FR = "priere_fr.json";

    // Prayer times in Algeria
    public static final String URL_PRIERE_ALG = "http://196.203.63.50/Isslamyat/web/json/priere.json";
    public static final String FILE_NAME_PIERE_ALG = "priere_alg.json";

    //BLAGUE URL
    public static final String URL_BLAGUE = "https://humour.tunisienumerique.com/hummor.json";
    public static final String FILE_NAME_BLAGUES = "blagues.json";


    public static final String URL_VIDEO = "https://preprod.tunisienumerique.com/jsondata/videotunisienumerique";
    public static final String FILE_NAME_VIDEO = "video";
    public static final String FILE_SURVEY = "survey.json";
    public static final String JSON = ".json";
    public static final String TEXT = ".txt";
    public static String URL_Pub = "https://jsondata.tunisienumerique.com/pub.json";
    public static final String FILE_NAME_PUB = "publicite.json";

    public static final String URL_SURVEY = "https://mdweb-int.com/appTN/npsTN.json";

}
