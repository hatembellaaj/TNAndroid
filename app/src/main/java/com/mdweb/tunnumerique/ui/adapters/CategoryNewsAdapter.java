package com.mdweb.tunnumerique.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class CategoryNewsAdapter extends RecyclerView.Adapter<CategoryNewsAdapter.CategoryNewsViewHolder> {

    private Context context;
    private List<News> newsList;
    private OnNewsClickListener onNewsClickListener;
    private OnNewsActionListener onNewsActionListener;

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    public interface OnNewsActionListener {
        void onNewsSave(News news);
        void onNewsShare(News news);
    }

    public CategoryNewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    public void setOnNewsClickListener(OnNewsClickListener listener) {
        this.onNewsClickListener = listener;
    }

    public void setOnNewsActionListener(OnNewsActionListener listener) {
        this.onNewsActionListener = listener;
    }

    @NonNull
    @Override
    public CategoryNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ Utilise le nouveau layout SANS newsCategory
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_news, parent, false);
        return new CategoryNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryNewsViewHolder holder, int position) {
        News news = newsList.get(position);

        // Titre
        holder.title.setText(news.getTitleNews());

        // Date
        if (news.getDateNews() != null && !news.getDateNews().isEmpty()) {
            holder.date.setText(formatDate(news.getDateNews()));
            holder.date.setVisibility(View.VISIBLE);
        } else {
            holder.date.setVisibility(View.GONE);
        }

        // Image
        if (news.getImageUrlNews() != null && !news.getImageUrlNews().isEmpty()) {
            Glide.with(context)
                    .load(news.getImageUrlNews())
                    .centerCrop()
                    .into(holder.image);
        }

        // ── Bookmark ──────────────────────────────────────────────────
        try {
            FavorisDataBase db = FavorisDataBase.getInstance(context);
            News existing = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());
            holder.bookmark.setImageResource(
                    existing != null ? R.drawable.ic_bookmark_filled : R.drawable.icon_save_r
            );
        } catch (Exception e) {
            holder.bookmark.setImageResource(R.drawable.icon_save_r);
        }

        holder.bookmark.setOnClickListener(v -> {
            if (onNewsActionListener != null) {
                onNewsActionListener.onNewsSave(news);
                // Mettre à jour l'icône immédiatement
                try {
                    FavorisDataBase db = FavorisDataBase.getInstance(context);
                    News existing = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());
                    holder.bookmark.setImageResource(
                            existing != null ? R.drawable.ic_bookmark_filled : R.drawable.icon_save_r
                    );
                } catch (Exception ignored) {}
            }
        });

        // ── Share ─────────────────────────────────────────────────────
        holder.share.setOnClickListener(v -> {
            if (onNewsActionListener != null) {
                onNewsActionListener.onNewsShare(news);
            }
        });

        // ── Clic article (image + titre) ──────────────────────────────
        View.OnClickListener openDetail = v -> {
            if (onNewsClickListener != null) {
                onNewsClickListener.onNewsClick(news);
            }
        };
        holder.image.setOnClickListener(openDetail);
        holder.title.setOnClickListener(openDetail);
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat in  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = in.parse(dateStr);
            return out.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    static class CategoryNewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image, bookmark, share;
        TextView title, date;

        CategoryNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image    = itemView.findViewById(R.id.newsImage);
            title    = itemView.findViewById(R.id.newsTitle);
            date     = itemView.findViewById(R.id.newsDate);
            bookmark = itemView.findViewById(R.id.slideBookmarkNews);
            share    = itemView.findViewById(R.id.slideShareNews);
            // ✅ PAS de newsCategory ici
        }
    }
}