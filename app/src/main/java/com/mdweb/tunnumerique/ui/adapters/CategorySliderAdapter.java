package com.mdweb.tunnumerique.ui.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.CategorySlider;

import java.util.List;

public class CategorySliderAdapter extends RecyclerView.Adapter<CategorySliderAdapter.ViewHolder> {

    private List<CategorySlider> categories;
    private Context context;
    private SliderAdapter.OnSlideClickListener slideClickListener;

    public CategorySliderAdapter(Context context, List<CategorySlider> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setOnSlideClickListener(SliderAdapter.OnSlideClickListener listener) {
        this.slideClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_slider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (categories != null && position < categories.size()) {
            holder.bind(categories.get(position), position, getItemCount());
        }
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        ViewPager2 sliderViewPager;
        LinearLayout dotsLayout;
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable autoScrollRunnable;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            sliderViewPager = itemView.findViewById(R.id.itemSliderViewPager);
        }

        void bind(CategorySlider category, int position, int totalItems) {
            if (category == null || category.getSlides() == null || category.getSlides().isEmpty()) {
                return;
            }

            // NE PLUS MODIFIER LE PADDING - Laisser le XML gérer ça
            // Le paddingTop est maintenant défini dans item_category_slider.xml

            String categoryName = category.getTitle();
            categoryTitle.setText(categoryName);

            // IMPORTANT: Passer le nom de la catégorie au SliderAdapter
            SliderAdapter adapter = new SliderAdapter(context, category.getSlides(), categoryName);
            if (slideClickListener != null) {
                adapter.setOnSlideClickListener(slideClickListener);
            }
            sliderViewPager.setAdapter(adapter);

            sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                }
            });

            setupAutoScroll(adapter);

            // CACHER LE TRAIT POUR LE DERNIER ÉLÉMENT DE CATÉGORIE
            View categoryDivider = itemView.findViewById(R.id.categoryDivider);
            if (categoryDivider != null) {
                // Vérifier si c'est le dernier élément
                boolean isLastCategory = (position == totalItems - 1);
                categoryDivider.setVisibility(isLastCategory ? View.GONE : View.VISIBLE);
            }
        }

        void setupAutoScroll(SliderAdapter adapter) {
            autoScrollRunnable = () -> {
                if (adapter.getItemCount() > 0) {
                    int current = sliderViewPager.getCurrentItem();
                    sliderViewPager.setCurrentItem(
                            current < adapter.getItemCount() - 1 ? current + 1 : 0, true);
                    handler.postDelayed(autoScrollRunnable, 3000);
                }
            };
            handler.postDelayed(autoScrollRunnable, 3000);
        }

        void cleanup() {
            if (handler != null && autoScrollRunnable != null) {
                handler.removeCallbacks(autoScrollRunnable);
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanup();
    }
}