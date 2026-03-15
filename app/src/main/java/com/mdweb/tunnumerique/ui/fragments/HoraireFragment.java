package com.mdweb.tunnumerique.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.HorairePriere;
import com.mdweb.tunnumerique.tools.application.Application;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HoraireFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HoraireFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "param";
    private HorairePriere horairePriere;


    public HoraireFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param horairePriere Parameter 1.
     * @return A new instance of fragment HoraireFragment.
     */
    public static HoraireFragment newInstance(HorairePriere horairePriere) {

        HoraireFragment fragment = new HoraireFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, horairePriere);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            horairePriere = (HorairePriere) getArguments().getSerializable(ARG_PARAM);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.getInstance().trackScreenView(getActivity(),getResources().getString(R.string.horaire_title));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_horaire, container, false);
        // initialize view
        initView(rootView);
        return rootView;
    }

    /**
     * initialize views
     *
     * @param rootView layout inflater
     */
    private void initView(View rootView) {
        // find views
        TextView fajer = (TextView) rootView.findViewById(R.id.fajer);
        TextView dhohor = (TextView) rootView.findViewById(R.id.dhohor);
        TextView aser = (TextView) rootView.findViewById(R.id.aser);
        TextView maghreb = (TextView) rootView.findViewById(R.id.maghreb);
        TextView icha = (TextView) rootView.findViewById(R.id.icha);
        //
        fajer.setText(splitHoraire(horairePriere.getFajer()));
        Log.e("FajrHo",horairePriere.getFajer());
        dhohor.setText(splitHoraire(horairePriere.getDhohr()));
        aser.setText(splitHoraire(horairePriere.getAser()));
        maghreb.setText(splitHoraire(horairePriere.getMaghreb()));
        icha.setText(splitHoraire(horairePriere.getIcha()));
    }

    /**
     * @param horaire horaire format hh:mm:aa
     * @return horaire format hh:mm
     */
    public String splitHoraire(String horaire) {
        String[] parts = horaire.split(":");
        String horairePriere = "";
        if (parts.length > 1)
            horairePriere = parts[0] + ":" + parts[1];
        return horairePriere;
    }
}
