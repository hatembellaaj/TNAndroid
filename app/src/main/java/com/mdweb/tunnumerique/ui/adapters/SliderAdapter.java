package com.mdweb.tunnumerique.ui.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.SlideItem;
import com.mdweb.tunnumerique.data.model.News;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SlideViewHolder> {

    private Context context;
    private List<SlideItem> slides;
    private String categoryName;
    private OnSlideClickListener listener;

    public interface OnSlideClickListener {
        void onSlideClick(SlideItem slideItem);
    }

    public SliderAdapter(Context context, List<SlideItem> slides, String categoryName) {
        this.context = context;
        this.slides = slides != null ? slides : new java.util.ArrayList<>();
        this.categoryName = categoryName;
    }

    public void setOnSlideClickListener(OnSlideClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        if (slides != null && position < slides.size()) {
            holder.bind(slides.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return slides.size();
    }

    class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView slideImage;
        LinearLayout slideDotsLayout;
        ImageView slideBookmark;
        ImageView slideShare;
        TextView slideCategory;
        TextView slideTitle;
        TextView slideDate;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            slideImage = itemView.findViewById(R.id.slideImage);
            slideDotsLayout = itemView.findViewById(R.id.slideDotsLayout);
            slideBookmark = itemView.findViewById(R.id.slideBookmark);
            slideShare = itemView.findViewById(R.id.slideShare);
            slideCategory = itemView.findViewById(R.id.slideCategory);
            slideTitle = itemView.findViewById(R.id.slideTitle);
            slideDate = itemView.findViewById(R.id.slideDate);
        }

        void bind(SlideItem slide, int position) {
            if (slide == null) return;

            News news = slide.getNewsObject();

            // Image
            if (slideImage != null && context != null) {
                String url = slide.getImageUrl();
                if (url != null && !url.isEmpty() && !url.equals("null")) {
                    Glide.with(context)
                            .load(url)
                            .centerCrop()
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .into(slideImage);
                } else {
                    slideImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }

            // Créer les dots pour ce slide
            setupDotsForSlide(position);

            // Catégorie
            if (slideCategory != null && categoryName != null) {
                slideCategory.setText(categoryName);
            }

            // Titre
            if (slideTitle != null) {
                slideTitle.setText(slide.getTitle() != null ? slide.getTitle() : "");
            }

            // Date
            if (slideDate != null && news != null) {
                String dateStr = news.getDateNews();
                if (dateStr != null) {
                    slideDate.setText(formatDate(dateStr));
                } else {
                    slideDate.setVisibility(View.GONE);
                }
            }

            // Clic sur le slide
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onSlideClick(slide);
            });

            // Clic sur bookmark
            if (slideBookmark != null) {
                slideBookmark.setOnClickListener(v -> {
                    // TODO: Implémenter la sauvegarde
                });
            }

            // Clic sur share
            if (slideShare != null) {
                slideShare.setOnClickListener(v -> {
                    // TODO: Implémenter le partage
                });
            }
        }

        /**
         * Crée les dots indicateurs pour ce slide
         * Les N premiers éléments sont des dots verts circulaires COLLÉS
         * Le dernier élément est une ligne verte
         */
        void setupDotsForSlide(int position) {
            if (slideDotsLayout == null) return;

            slideDotsLayout.removeAllViews();

            // Nombre de dots à afficher = position actuelle + 1
            int numberOfDots = position + 1;

            // Ajouter les dots verts AVEC ESPACEMENT
            for (int i = 0; i < numberOfDots; i++) {
                ImageView dot = new ImageView(context);
                dot.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dot_active));

                // ESPACEMENT ENTRE LES DOTS
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                // 8dp entre chaque dot
                int margin = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2,
                        context.getResources().getDisplayMetrics()
                );
                params.setMargins(margin, 0, margin, 0);

                slideDotsLayout.addView(dot, params);
            }


            // Ajouter la ligne verte à la fin (seulement si pas le dernier slide)
            if (position < slides.size() - 1) {
                ImageView line = new ImageView(context);
                line.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dot_line_green));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(3, 0, 0, 0); // 3dp de marge à gauche pour séparer des dots

                slideDotsLayout.addView(line, params);
            }
        }

        private String formatDate(String dateStr) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(dateStr);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateStr;
            }
        }
    }
}