package com.mdweb.tunnumerique.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {

    private Context context;
    private List<News> newsList;
    private OnNewsClickListener onNewsClickListener;
    private OnNewsActionListener onNewsActionListener; // ✅ NOUVEAU

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    // ✅ NOUVELLE INTERFACE pour Save et Share
    public interface OnNewsActionListener {
        void onNewsSave(News news);
        void onNewsShare(News news);
    }

    public NewsListAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    public void setOnNewsClickListener(OnNewsClickListener listener) {
        this.onNewsClickListener = listener;
    }

    // ✅ NOUVEAU setter pour les actions
    public void setOnNewsActionListener(OnNewsActionListener listener) {
        this.onNewsActionListener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_list, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        android.util.Log.d("NewsListAdapter", "Binding article " + position + ": " + news.getTitleNews());

        // Titre de l'article
        holder.title.setText(news.getTitleNews());

        // Catégorie
        if (news.getTypeNews() != null && !news.getTypeNews().isEmpty()) {
            String[] categories = news.getTypeNews().split(",");
            holder.category.setText(categories[0].trim().toUpperCase());
            holder.category.setVisibility(View.VISIBLE);
        } else {
            holder.category.setVisibility(View.GONE);
        }

        // Date
        if (news.getDateNews() != null && !news.getDateNews().isEmpty()) {
            holder.date.setText(formatDate(news.getDateNews()));
            holder.date.setVisibility(View.VISIBLE);
        } else {
            holder.date.setVisibility(View.GONE);
        }

        // Image
        if (news.getImageUrlNews() != null && !news.getImageUrlNews().isEmpty()) {
            try {
                Glide.with(context)
                        .load(news.getImageUrlNews())
                        .centerCrop()
                        .into(holder.image);
                holder.image.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                android.util.Log.e("NewsListAdapter", "Erreur chargement image: " + e.getMessage());
                holder.image.setVisibility(View.VISIBLE);
            }
        } else {
            holder.image.setVisibility(View.VISIBLE);
        }

        // ✅ VÉRIFIER SI L'ARTICLE EST EN FAVORIS ET METTRE À JOUR L'ICÔNE
        if (holder.bookmark != null) {
            holder.bookmark.setVisibility(View.VISIBLE);

            try {
                FavorisDataBase db = FavorisDataBase.getInstance(context);
                News existingNews = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());

                // ✅ Changer l'icône selon l'état
                if (existingNews != null) {
                    // Déjà en favoris → Icône pleine
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    android.util.Log.d("NewsListAdapter", "💾 Article EN favoris: " + news.getIdNews());
                } else {
                    // Pas en favoris → Icône vide
                    holder.bookmark.setImageResource(R.drawable.icon_save_r);
                    android.util.Log.d("NewsListAdapter", "💾 Article PAS en favoris: " + news.getIdNews());
                }

            } catch (Exception e) {
                android.util.Log.e("NewsListAdapter", "❌ Erreur vérification favoris: " + e.getMessage());
                // Par défaut, icône vide
                holder.bookmark.setImageResource(R.drawable.icon_save_r);
            }

            // ✅ CLIC SUR BOOKMARK → Callback vers l'Activity
            holder.bookmark.setOnClickListener(v -> {
                // Empêcher la propagation du clic
                v.setClickable(true);
                android.util.Log.d("NewsListAdapter", "💾 Bookmark clicked: " + news.getTitleNews());

                if (onNewsActionListener != null) {
                    onNewsActionListener.onNewsSave(news);

                    // ✅ Mettre à jour l'icône après le clic
                    try {
                        FavorisDataBase db = FavorisDataBase.getInstance(context);
                        News existingNews = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());

                        if (existingNews != null) {
                            // Est en favoris → Icône pleine
                            holder.bookmark.setImageResource(R.drawable.ic_bookmark_filled);
                        } else {
                            // N'est pas en favoris → Icône vide
                            holder.bookmark.setImageResource(R.drawable.icon_save_r);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("NewsListAdapter", "❌ Erreur mise à jour icône: " + e.getMessage());
                    }
                } else {
                    // Fallback si pas de listener
                    Toast.makeText(context, "Article sauvegardé", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ✅ CLIC SUR SHARE → Callback vers l'Activity
        if (holder.share != null) {
            holder.share.setVisibility(View.VISIBLE);
            holder.share.setOnClickListener(v -> {
                // Empêcher la propagation du clic
                v.setClickable(true);
                android.util.Log.d("NewsListAdapter", "🔗 Share clicked: " + news.getTitleNews());

                if (onNewsActionListener != null) {
                    onNewsActionListener.onNewsShare(news);
                } else {
                    // Fallback si pas de listener
                    shareArticle(news);
                }
            });
        }

        // ✅ CLIC SUR L'ARTICLE (image + titre) → Ouvrir détail
        View.OnClickListener openDetailListener = v -> {
            if (onNewsClickListener != null) {
                android.util.Log.d("NewsListAdapter", "📰 Ouverture détail: " + news.getTitleNews());
                onNewsClickListener.onNewsClick(news);
            }
        };

        // Clic sur image
        if (holder.image != null) {
            holder.image.setOnClickListener(openDetailListener);
        }

        // Clic sur titre
        if (holder.title != null) {
            holder.title.setOnClickListener(openDetailListener);
        }
    }

    /**
     * Partage l'article via Intent (fallback)
     */
    private void shareArticle(News news) {
        try {
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            String shareText = news.getTitleNews();

            if (news.getDescriptionNews() != null && !news.getDescriptionNews().isEmpty()) {
                shareText += "\n\n" + news.getDescriptionNews();
            }

            if (news.getShareUrlNews() != null && !news.getShareUrlNews().isEmpty()) {
                shareText += "\n\nLire plus: " + news.getShareUrlNews();
            }

            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Partager via"));

        } catch (Exception e) {
            android.util.Log.e("NewsListAdapter", "❌ Erreur partage: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    /**
     * Formate la date au format lisible
     */
    private String formatDate(String dateStr) {
        try {
            // Essayer de parser la date (ajustez le format selon votre JSON)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            // Si le parsing échoue, retourner la date telle quelle
            return dateStr;
        }
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView category;
        TextView date;
        ImageView bookmark; // ✅ NOUVEAU
        ImageView share;    // ✅ NOUVEAU

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.newsImage);
            title = itemView.findViewById(R.id.newsTitle);
            category = itemView.findViewById(R.id.newsCategory);
            date = itemView.findViewById(R.id.newsDate);
            bookmark = itemView.findViewById(R.id.slideBookmarkNews); // ✅ NOUVEAU
            share = itemView.findViewById(R.id.slideShareNews);       // ✅ NOUVEAU
        }
    }
}