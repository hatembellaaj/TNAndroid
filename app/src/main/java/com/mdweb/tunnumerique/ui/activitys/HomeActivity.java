package com.mdweb.tunnumerique.ui.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Article;
import com.mdweb.tunnumerique.data.model.HomeItem;
import com.mdweb.tunnumerique.data.model.HomeSection;
import com.mdweb.tunnumerique.data.model.SectionHome;
import com.mdweb.tunnumerique.data.model.SliderItem;
import com.mdweb.tunnumerique.ui.adapters.HomeAdapter;
import com.mdweb.tunnumerique.ui.adapters.SectionAdapter;
import com.mdweb.tunnumerique.ui.adapters.SliderAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 sliderViewPager;
    private LinearLayout dotsLayout;
    private RecyclerView homeRecyclerView;

    private SliderAdapter sliderAdapter;
    private List<SliderItem> sliderItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeRecyclerView = findViewById(R.id.homeRecyclerView);

        setupSlider();
        setupRecycler();
    }

    private void setupSlider() {
        sliderItems.add(new SliderItem("Article 1", "https://picsum.photos/800/400"));
        sliderItems.add(new SliderItem("Article 2", "https://picsum.photos/800/401"));
        sliderItems.add(new SliderItem("Article 3", "https://picsum.photos/800/402"));

      //  sliderAdapter = new SliderAdapter(sliderItems);
      //  sliderViewPager.setAdapter(sliderAdapter);

        setupDots();
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }
        });
    }

    private void setupDots() {
        ImageView[] dots = new ImageView[sliderItems.size()];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.dot_unselected);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);

            dotsLayout.addView(dots[i], params);
        }

        selectDot(0);
    }

    private void selectDot(int index) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);

            if (i == index)
                dot.setImageResource(R.drawable.dot_selected);
            else
                dot.setImageResource(R.drawable.dot_unselected);
        }
    }

    // -------------------------
    // 🔹 LA PARTIE QUI MANQUAIT
    // -------------------------
    private void setupRecycler() {
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        homeRecyclerView.setAdapter(new HomeAdapter(getFakeHomeSections()));
    }

    private List<HomeItem> getFakeHomeSections() {
        List<HomeItem> list = new ArrayList<>();
        list.add(new HomeItem("À la Une"));
        list.add(new HomeItem("News"));
        list.add(new HomeItem("Monde"));
        list.add(new HomeItem("Sport"));
        list.add(new HomeItem("Économie"));
        return list;
    }
}
