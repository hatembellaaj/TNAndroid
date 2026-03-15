package com.mdweb.tunnumerique.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.ui.adapters.CategorySliderAdapter;
import com.mdweb.tunnumerique.data.model.CategorySlider;
import com.mdweb.tunnumerique.data.model.SlideItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView homeRecyclerView;
    private CategorySliderAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialisation de la vue
        homeRecyclerView = view.findViewById(R.id.homeRecyclerView);

        // Configuration du RecyclerView
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Créer les données des catégories
        List<CategorySlider> categories = createCategoriesData();

        // Configurer l'adapter
        categoryAdapter = new CategorySliderAdapter(getContext(), categories);
        categoryAdapter.setOnSlideClickListener(slide -> {
            Toast.makeText(getContext(), "Article: " + slide.getTitle(), Toast.LENGTH_SHORT).show();
            // Ici vous pouvez ouvrir l'article détaillé
        });
        homeRecyclerView.setAdapter(categoryAdapter);
    }

    private List<CategorySlider> createCategoriesData() {
        List<CategorySlider> categories = new ArrayList<>();

        // Catégorie 1: À la Une
        List<SlideItem> aLaUne = new ArrayList<>();
        aLaUne.add(new SlideItem("https://picsum.photos/600/400?random=10", "Actualité brûlante du jour"));
        aLaUne.add(new SlideItem("https://picsum.photos/600/400?random=11", "Événement majeur en cours"));
        aLaUne.add(new SlideItem("https://picsum.photos/600/400?random=12", "Dernière minute"));
        categories.add(new CategorySlider("🔴 À la Une", aLaUne));

        // Catégorie 2: Sport
        List<SlideItem> sport = new ArrayList<>();
        sport.add(new SlideItem("https://picsum.photos/600/400?random=20", "Match du jour"));
        sport.add(new SlideItem("https://picsum.photos/600/400?random=21", "Résultats et classement"));
        sport.add(new SlideItem("https://picsum.photos/600/400?random=22", "Interview exclusive"));
        categories.add(new CategorySlider("⚽ Sport", sport));

        // Catégorie 3: Monde
        List<SlideItem> monde = new ArrayList<>();
        monde.add(new SlideItem("https://picsum.photos/600/400?random=30", "Actualité internationale"));
        monde.add(new SlideItem("https://picsum.photos/600/400?random=31", "Événements mondiaux"));
        monde.add(new SlideItem("https://picsum.photos/600/400?random=32", "Diplomatie mondiale"));
        categories.add(new CategorySlider("🌍 Monde", monde));

        // Catégorie 4: Technologie
        List<SlideItem> tech = new ArrayList<>();
        tech.add(new SlideItem("https://picsum.photos/600/400?random=40", "Innovations tech"));
        tech.add(new SlideItem("https://picsum.photos/600/400?random=41", "Nouveaux gadgets"));
        tech.add(new SlideItem("https://picsum.photos/600/400?random=42", "IA et futur"));
        categories.add(new CategorySlider("💻 Technologie", tech));

        return categories;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Plus besoin de nettoyer les handlers du slider principal
    }
}