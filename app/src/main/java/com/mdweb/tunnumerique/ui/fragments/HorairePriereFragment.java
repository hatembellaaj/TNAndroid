package com.mdweb.tunnumerique.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.banner.BannerView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Country;
import com.mdweb.tunnumerique.data.model.DateSalat;
import com.mdweb.tunnumerique.data.model.HorairePriere;
import com.mdweb.tunnumerique.data.model.ItemMenu;
import com.mdweb.tunnumerique.data.parsers.DataParser;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.application.Application;
import com.mdweb.tunnumerique.tools.shared.Communication;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.ui.adapters.ExpandableListAdapter;
import com.mdweb.tunnumerique.ui.adapters.ViewPagerAdapter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HorairePriereFragment extends Fragment implements View.OnClickListener {
    public LinearLayoutManager linearLayoutManager;
    public static Dialog dialog;
    public RecyclerView recyclerview;
    public ExpandableListAdapter gouvernorateListAdapter;
    public ExpandableListAdapter countryListAdapter;
    private RelativeLayout filterSelectGouvernorate;
    private RelativeLayout filterSelectCountry;
    private TextView currentDate;
    private TextView gouvernorateName;
    private TextView countryName;
    public static int idDefaultCountry;
    private ImageView nextPage;
    private ImageView backPage;
    public static int indexSelectedCountry = 0;
    EditText mSearchText;
    private Handler handler;
    ArrayList<DateSalat> datePrayArrayList;
    ArrayList<Country> countriesArrayList;
    private ViewPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    LinkedHashMap<String, String> countries;
    String[] gouvernorates;
    private Utils utils;
    private List<Fragment> fragments;
    private HorairePriere firstHorairePriere;
    private HorairePriere secondeHorairePriere;
    LinearLayout clearText;
    BannerView huaweiBannerView;
    private ArrayList<String> searchedGouvernorates = new ArrayList<String>();
    private ArrayList<ItemMenu> allSearchedGouvernorates = new ArrayList<>();
    private LinearLayout searchLayout;
    private ImageView closeIcon;
    private LinearLayout noAvailablePray;


    public HorairePriereFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        handler = new Handler();
        View rootView = inflater.inflate(R.layout.fragment_horaire_priere, container, false);
        //extract all supported counties prayer times
        utils = new Utils(getContext());
        countriesArrayList = new DataParser().getListCountries(utils.getStringFromFile(Communication.FILE_NAME_SUPPORTED_COUNTRIES), requireActivity());
        // initialize views
        initView(rootView);
        return rootView;
    }


    private void initView(View rootView) {

            huaweiBannerView = rootView.findViewById(R.id.hw_banner_view);
            huaweiBannerView.setVisibility(View.VISIBLE);
            // Create an ad request to load an ad.
            AdParam adParam = new AdParam.Builder().build();
            huaweiBannerView.loadAd(adParam);
            Log.d("TestPub : " ,  "Test");

        firstHorairePriere = new HorairePriere();
        secondeHorairePriere = new HorairePriere();
        //dateSalatArrayList = new DataParser().getListDate(utils.getStringFromFile(countriesArrayList.get(0).getPriereFileName()), requireActivity());
        fragments = new ArrayList<Fragment>();
        filterSelectCountry = (RelativeLayout) rootView.findViewById(R.id.filter_select_country);
        filterSelectGouvernorate = (RelativeLayout) rootView.findViewById(R.id.filter_select_zone);
        filterSelectCountry.setOnClickListener(this);
        filterSelectGouvernorate.setOnClickListener(this);
        countries = getAllCountries();
        countryName = (TextView) rootView.findViewById(R.id.country_name);
        gouvernorateName = (TextView) rootView.findViewById(R.id.zone_name);
        noAvailablePray = (LinearLayout) rootView.findViewById(R.id.no_priere);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        if (countriesArrayList == null || countriesArrayList.isEmpty()) {
            countryName.setText(R.string.tunisie);
            gouvernorateName.setText(R.string.tunis);
            filterSelectCountry.setEnabled(false);
            filterSelectGouvernorate.setEnabled(false);
            viewPager.setVisibility(View.GONE);
            noAvailablePray.setVisibility(View.VISIBLE);
        }
        else {
            String lastSelectedCountry = getSelectedWilayaOrCountry(SessionManager.getInstance().getCountryId(getActivity(), idDefaultCountry), getItemMenu(countries), Constant.COUNTRY);
            countryName.setText(lastSelectedCountry);
            indexSelectedCountry =  findSelectedCountryByName(lastSelectedCountry);
            datePrayArrayList = new DataParser().getListDate(utils.getStringFromFile(countriesArrayList.get(indexSelectedCountry).getPriereFileName()), requireActivity());
            gouvernorates = getListGouvornorat();
            gouvernorateName.setText(getSelectedWilayaOrCountry(SessionManager.getInstance().getGouvernorateId(getActivity()), getItemMenu(gouvernorates), Constant.Gouvernorate));
            currentDate = (TextView) rootView.findViewById(R.id.current_date);
            nextPage = (ImageView) rootView.findViewById(R.id.next_page);
            backPage = (ImageView) rootView.findViewById(R.id.back_page);
            nextPage.setOnClickListener(this);
            backPage.setOnClickListener(this);

            //todo
            if (!datePrayArrayList.isEmpty())
                currentDate.setText(datePrayArrayList.get(0).getDateMiladiText());
            backPage.setVisibility(View.INVISIBLE);
            nextPage.setVisibility(View.VISIBLE);
            firstHorairePriere = getHoraireDay(0, getSelectedWilayaOrCountry(SessionManager.getInstance().getGouvernorateId(getActivity()), getItemMenu(gouvernorates), Constant.Gouvernorate));
            secondeHorairePriere = getHoraireDay(1, getSelectedWilayaOrCountry(SessionManager.getInstance().getGouvernorateId(getActivity()), getItemMenu(gouvernorates), Constant.Gouvernorate));
            pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
            pagerAdapter.addFrag(HoraireFragment.newInstance(firstHorairePriere));
            pagerAdapter.addFrag(HoraireFragment.newInstance(secondeHorairePriere));
            viewPager.setAdapter(this.pagerAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    String dateByCurrentLang;
                    if (viewPager.getCurrentItem() == 0) {
                        backPage.setVisibility(View.INVISIBLE);
                        nextPage.setVisibility(View.VISIBLE);
                        dateByCurrentLang = getFormattedDate(datePrayArrayList.get(0));
                        currentDate.setText(dateByCurrentLang);

                    } else {
                        dateByCurrentLang = getFormattedDate(datePrayArrayList.get(1));
                        currentDate.setText(dateByCurrentLang);
                        backPage.setVisibility(View.VISIBLE);
                        nextPage.setVisibility(View.INVISIBLE);
                    }


                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }

                public String getFormattedDate(DateSalat dateSalat) {
                    if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("ar")) {
                        return dateSalat.getDateMiladiText().replace(" ", "/");
                    } else {
                        String[] date = dateSalat.getDateMiladi().split("-");

                        if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("fr")) {

                            return date[0] + "/" + new DateFormatSymbols().getInstance(new Locale("fr")).getMonths()[Integer.valueOf(date[1])-1] + "/" + date[2];
                        } else {
                            return date[0] + "/" + new DateFormatSymbols().getInstance(new Locale("en")).getMonths()[Integer.valueOf(date[1])-1] + "/" + date[2];
                        }

                    }
                }
            });
        }

    }

    /**
     * show dialog for filter
     */
    @SuppressLint("WrongConstant")
    private void dialogFilterGouvernorates() {
        //dismiss dialog
        dialog = new Dialog(getContext(), R.style.DialogSlideAnimFilter);
        View view = (getActivity()).getLayoutInflater().inflate(R.layout.dialog_priere, null);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        clearText = view.findViewById(R.id.clear_text);
        closeIcon = view.findViewById(R.id.close_popup);
        searchLayout = view.findViewById(R.id.search_layout);
        mSearchText = view.findViewById(R.id.search_text);
        String nomZoneAdminisrative;
        if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("ar")) {
            nomZoneAdminisrative = countriesArrayList.get(indexSelectedCountry).getNomZoneAdminisrativeAr();
        }
        else if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("fr")) {
            nomZoneAdminisrative = countriesArrayList.get(indexSelectedCountry).getNomZoneAdminisrativeFr();
        }
        else {
            nomZoneAdminisrative = countriesArrayList.get(indexSelectedCountry).getNomZoneAdminisrativeEn();
        }
        dialogTitle.setText(getString(R.string.title_dialog_priere) + " " +nomZoneAdminisrative);
        dialog.setContentView(view);
        recyclerview = (RecyclerView) view.findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
        //disable over scroll effect of recyclerView
        recyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        closeIcon.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.VISIBLE);
        gouvernorateListAdapter = new ExpandableListAdapter(getContext(), getItemMenu(gouvernorates), this, 1, false);
        recyclerview.setAdapter(gouvernorateListAdapter);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSearchText.addTextChangedListener(new SearchTextWatcher(false));
        clearText.setOnClickListener(this);
        closeIcon.setOnClickListener(this);
        dialog.show();

    }
    /**
     * show dialog for filter
     */
    @SuppressLint("WrongConstant")
    private void dialogFilterCountries() {
        //dismiss dialog
        dialog = new Dialog(getContext(), R.style.DialogSlideAnimFilter);
        View view = (getActivity()).getLayoutInflater().inflate(R.layout.dialog_priere, null);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.title_dialog_priere1));
        dialog.setContentView(view);
        recyclerview = (RecyclerView) view.findViewById(R.id.recycler);
//        float scale = getResources().getDisplayMetrics().density;
//        int dpAsPixels = (int) (100*scale + 0.5f);
//        view.setMinimumHeight(dpAsPixels);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
//        //disable over scroll effect of recyclerView
        recyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        countryListAdapter = new ExpandableListAdapter(getContext(), getItemMenu(countries), this, 1, true);
        recyclerview.setAdapter(countryListAdapter);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * create men for filter
     *
     * @return return list item menu
     */
    public List<ItemMenu> getItemMenu(String[] pays) {
        List<ItemMenu> data = new ArrayList<>();
        for (int i = 0; i < pays.length; i++) {
            data.add(new ItemMenu(pays[i], i + 1));
        }

        return data;
    }


    /**
     * create men for filter
     *
     * @return return list item menu
     */

    public List<ItemMenu> getItemMenu(HashMap<String, String> countries) {
        List<ItemMenu> data = new ArrayList<>();
        int i = 1;
        for (Map.Entry<String, String> entry : countries.entrySet()) {
            data.add(new ItemMenu(entry.getKey(),entry.getValue(), entry.getValue(), i++));
        }

        return data;
    }

    // return the last selected country or wilaya
    public String getSelectedWilayaOrCountry(int id, List<ItemMenu> data,  String type) {

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == id) {
                if (type.equals(Constant.COUNTRY)) {
                    indexSelectedCountry = i;
                }
                    return data.get(i).getText();
            }
        }
        if(type.equals(Constant.COUNTRY))
            return getString(R.string.tunisie);
        else
            return getString(R.string.tunis);
    }


    /**
     * Display item filter in fragment
     *
     * @param id id item
     */
    public void displayView(int id) {
        backPage.setVisibility(View.INVISIBLE);
        nextPage.setVisibility(View.VISIBLE);
        //display view by id
        gouvernorateName.setText(gouvernorates[id - 1]);


        getHoraireDay(0, getSelectedWilayaOrCountry(SessionManager.getInstance().getGouvernorateId(getActivity()), getItemMenu(gouvernorates), Constant.Gouvernorate));
        getHoraireDay(1, getSelectedWilayaOrCountry(SessionManager.getInstance().getGouvernorateId(getActivity()), getItemMenu(gouvernorates), Constant.Gouvernorate));
        // change time

        updateHoraire(firstHorairePriere, getHoraireDay(0, getSelectedWilayaOrCountry(SessionManager.getInstance().getGouvernorateId(getActivity()), getItemMenu(gouvernorates), Constant.Gouvernorate)));

        updateHoraire(secondeHorairePriere, getHoraireDay(1, getSelectedWilayaOrCountry(SessionManager.getInstance().getGouvernorateId(getActivity()), getItemMenu(gouvernorates), Constant.Gouvernorate)));
        //
        pagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(pagerAdapter);
    }

    // demiss Dialog
     public void demissDialog() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

            }
        }, 300);
    }
// this function will intialize list of gouvernorate associated to selected country
    @SuppressLint("WrongConstant")
    public void initializeGouvernorateList(ItemMenu selectedCountry) {
        countryName.setText(selectedCountry.getText());
        indexSelectedCountry =  findSelectedCountryByName(selectedCountry.getText());
        datePrayArrayList = new DataParser().getListDate(utils.getStringFromFile(countriesArrayList.get(indexSelectedCountry).getPriereFileName()), requireActivity());
        gouvernorates = getListGouvornorat();
        SessionManager.getInstance().setIdGouvernorate(getActivity(), 1);
        gouvernorateName.setText(gouvernorates[0]);
    }

    /**
     * update HoraireFragment
     *
     * @param horaire
     * @param horairePriere
     */
    private void updateHoraire(HorairePriere horaire, HorairePriere horairePriere) {
        horaire.setFajer(horairePriere.getFajer());
        horaire.setDhohr(horairePriere.getDhohr());
        horaire.setAser(horairePriere.getAser());
        horaire.setMaghreb(horairePriere.getMaghreb());
        horaire.setIcha(horairePriere.getIcha());


    }
    private int findSelectedCountryByName(String countryName) {
            for (int i = 0; i < countriesArrayList.size(); i++) {
                if (countryName.equals(countriesArrayList.get(i).getNomPaysAr()) || countryName.equals(countriesArrayList.get(i).getNomPaysFr())){
                    return i;
                }
            }
            return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_select_country:
                dialogFilterCountries();
                filterSelectCountry.setEnabled(false);
                filterSelectGouvernorate.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        filterSelectCountry.setEnabled(true);
                        filterSelectGouvernorate.setEnabled(true);
                    }
                }, 500);
                break;
            case R.id.filter_select_zone:
                // open dialog filter
                dialogFilterGouvernorates();
                //disabled dialog button
                filterSelectGouvernorate.setEnabled(false);
                filterSelectCountry.setEnabled(false);
                // enabled dialog button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        filterSelectGouvernorate.setEnabled(true);
                        filterSelectCountry.setEnabled(true);
                    }
                }, 500);
                break;

            case R.id.next_page:
                // change date
                currentDate.setText(utils.addDate());
                // change item view
                viewPager.setCurrentItem(getItem(+1), true);
                // set
                backPage.setVisibility(View.VISIBLE);
                nextPage.setVisibility(View.INVISIBLE);
                break;

            case R.id.back_page:
                // change date
                currentDate.setText(utils.currentDate());
                // change item view
                viewPager.setCurrentItem(getItem(-1), true);
                // set
                backPage.setVisibility(View.INVISIBLE);
                nextPage.setVisibility(View.VISIBLE);
                break;
            case R.id.clear_text:
                mSearchText.setText("");
                break;
            case R.id.close_popup:
                demissDialog();
                break;


        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    @Override
    public void onStart() {

        super.onStart();
    }

    @Override
    public void onResume() {
        Application.getInstance().trackScreenView(getActivity(), getResources().getString(R.string.horaire_title));
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        //unregister receiver
        super.onPause();
    }


    public String[] getListGouvornorat() {
        //todo list empty or null
        String[] govs;
        if (datePrayArrayList == null || datePrayArrayList.isEmpty()) {
            govs = new String[24];
            for (int i = 0; i < 24; i++) {
                govs[i] = "";
            }

        } else {
            govs = new String[datePrayArrayList.get(0).getmListeGouvernorat().size()];
            for (int i = 0; i < datePrayArrayList.get(0).getmListeGouvernorat().size(); i++) {
                if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("ar"))
                    govs[i] = (datePrayArrayList.get(0).getmListeGouvernorat().get(i).getNomGouvernoratAr());
                else
                    govs[i] = (datePrayArrayList.get(0).getmListeGouvernorat().get(i).getNomGouvernoratFr());


            }
        }

        Log.e("Governorat", govs[5]);

        return govs;

    }

    public LinkedHashMap<String, String> getAllCountries() {
        //todo list empty or null
        LinkedHashMap<String, String> ctries;
        if (countriesArrayList == null || countriesArrayList.isEmpty()) {
            ctries = new LinkedHashMap<String, String>(24);
            for (int i = 0; i < 24; i++) {
                ctries.put("","");
            }

        } else {
            ctries =new LinkedHashMap<String, String>(countriesArrayList.size());
            for (int i = 0; i < countriesArrayList.size(); i++) {
                if (countriesArrayList.get(i).getNomPaysFr().equals(Constant.NATIVE_COUNTRY))
                    idDefaultCountry = i+1;
                if (SessionManager.getInstance().getCurrentLang(requireActivity()).equals("ar"))
                    ctries.put(countriesArrayList.get(i).getNomPaysAr(),countriesArrayList.get(i).getPaysIconURL());
                else
                    ctries.put(countriesArrayList.get(i).getNomPaysFr(),countriesArrayList.get(i).getPaysIconURL());

            }
        }

        Log.e("Country", ctries.keySet().toString());

        return ctries;

    }

    public HorairePriere getHoraireDay(int d, String gouvernorate) {
        HorairePriere h = new HorairePriere();
        if (datePrayArrayList == null || datePrayArrayList.isEmpty()) {
            h.setFajer("");
            h.setDhohr("");
            h.setAser("");
            h.setMaghreb("");
            h.setIcha("");
        } else
            for (int i = 0; i < datePrayArrayList.get(d).getmListeGouvernorat().size(); i++) {

                if (datePrayArrayList.get(d).getmListeGouvernorat().get(i).getNomGouvernoratFr().equals(gouvernorate) || datePrayArrayList.get(d).getmListeGouvernorat().get(i).getNomGouvernoratAr().equals(gouvernorate)) {

                    h.setFajer(datePrayArrayList.get(d).getmListeGouvernorat().get(i).getmListHoraire().get(1).getHeure());
                    h.setDhohr(datePrayArrayList.get(d).getmListeGouvernorat().get(i).getmListHoraire().get(2).getHeure());
                    h.setAser(datePrayArrayList.get(d).getmListeGouvernorat().get(i).getmListHoraire().get(3).getHeure());
                    h.setMaghreb(datePrayArrayList.get(d).getmListeGouvernorat().get(i).getmListHoraire().get(4).getHeure());
                    h.setIcha(datePrayArrayList.get(d).getmListeGouvernorat().get(i).getmListHoraire().get(5).getHeure());
                }
            }
        return h;

    }
    class SearchTextWatcher implements TextWatcher {
        public boolean searchForCountries;

        public SearchTextWatcher(boolean searchFor) {
            this.searchForCountries = searchFor;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s.toString().isEmpty()||s.toString().trim().length() == 0) {
                gouvernorateListAdapter.initData();
                clearText.setVisibility(View.GONE);
            }
            else {
                String keyWord = s.toString();
                clearText.setVisibility(View.VISIBLE);
                String[] elements = keyWord.split(" ");
                for (String element : elements) {
                    customSearchListFunction(element);
                }
            }


            //Search and update List

        }
    }
    private void customSearchListFunction(String keyWord) {
        searchedGouvernorates.clear();
        String[] elements = keyWord.split(" ");

        for (String c : gouvernorates) {
            for (String element : elements) {
                if (c.toLowerCase().contains(element.toLowerCase())) {
                    searchedGouvernorates.add(c);
                }
            }
        }
        allSearchedGouvernorates.clear();
        allSearchedGouvernorates.addAll(getItemMenu(searchedGouvernorates.toArray(new String[searchedGouvernorates.size()])));
        gouvernorateListAdapter.updateGouvernorateList(allSearchedGouvernorates);

    }
    public int findGouvernoratePositionByName(String gouvernorateName) {
        if(gouvernorates.length>0){
            int i = 0;
            for(i=0; i<gouvernorates.length; i++) {
                if(gouvernorates[i].equals(gouvernorateName)) {
                    break;
                }
            }
            return i+1;
        }
        return -1;
    }
}
