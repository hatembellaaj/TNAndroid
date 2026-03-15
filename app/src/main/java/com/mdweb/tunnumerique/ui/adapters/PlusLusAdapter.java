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

public class PlusLusAdapter extends RecyclerView.Adapter<PlusLusAdapter.ArticleViewHolder> {

    private Context context;
    private List<News> newsList;
    private OnArticleClickListener onArticleClickListener;
    private OnArticleActionListener onArticleActionListener;

    // ── Interface clic ───────────────────────────────────────────
    public interface OnArticleClickListener {
        void onArticleClick(News news, int position);
    }

    // ── Interface Save & Share ───────────────────────────────────
    public interface OnArticleActionListener {
        void onArticleSave(News news);
        void onArticleShare(News news);
    }

    public PlusLusAdapter(Context context, List<News> newsList) {
        this.context   = context;
        this.newsList  = newsList;
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.onArticleClickListener = listener;
    }

    public void setOnArticleActionListener(OnArticleActionListener listener) {
        this.onArticleActionListener = listener;
    }

    // ── ViewHolder ───────────────────────────────────────────────
    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView ivArticleImage;
        TextView  tvRank;
        TextView  tvArticleTitle;
        TextView  tvArticleDate;
        ImageView ivSave;
        ImageView ivShare;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImage);
            tvArticleTitle = itemView.findViewById(R.id.tvArticleTitle);
            tvArticleDate  = itemView.findViewById(R.id.tvArticleDate);
            ivSave         = itemView.findViewById(R.id.ivSave);
            ivShare        = itemView.findViewById(R.id.ivShare);
        }
    }

    // ── Création ─────────────────────────────────────────────────
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_article_plus_lus, parent, false);
        return new ArticleViewHolder(view);
    }

    // ── Binding ──────────────────────────────────────────────────
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        News news = newsList.get(position);

        android.util.Log.d("PlusLusAdapter", "Binding article " + position + ": " + news.getTitleNews());

        // Rang (1-based)

        // Titre
        holder.tvArticleTitle.setText(news.getTitleNews() != null ? news.getTitleNews() : "");

        // Date
        if (news.getDateNews() != null && !news.getDateNews().isEmpty()) {
            holder.tvArticleDate.setText(formatDate(news.getDateNews()));
            holder.tvArticleDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvArticleDate.setVisibility(View.GONE);
        }

        // Image
        if (news.getImageUrlNews() != null && !news.getImageUrlNews().isEmpty()) {
            try {
                Glide.with(context)
                        .load(news.getImageUrlNews())
                        .centerCrop()
                        .into(holder.ivArticleImage);
                holder.ivArticleImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                android.util.Log.e("PlusLusAdapter", "Erreur chargement image: " + e.getMessage());
                holder.ivArticleImage.setVisibility(View.VISIBLE);
            }
        } else {
            holder.ivArticleImage.setVisibility(View.VISIBLE);
        }

        // ── Save : icône selon état favoris ──────────────────────
        if (holder.ivSave != null) {
            holder.ivSave.setVisibility(View.VISIBLE);

            try {
                FavorisDataBase db = FavorisDataBase.getInstance(context);
                News existingNews  = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());

                if (existingNews != null) {
                    holder.ivSave.setImageResource(R.drawable.ic_bookmark_filled);
                    android.util.Log.d("PlusLusAdapter", "💾 Article EN favoris: " + news.getIdNews());
                } else {
                    holder.ivSave.setImageResource(R.drawable.icon_save_r);
                    android.util.Log.d("PlusLusAdapter", "💾 Article PAS en favoris: " + news.getIdNews());
                }
            } catch (Exception e) {
                android.util.Log.e("PlusLusAdapter", "❌ Erreur vérification favoris: " + e.getMessage());
                holder.ivSave.setImageResource(R.drawable.icon_save_r);
            }

            holder.ivSave.setOnClickListener(v -> {
                v.setClickable(true);
                android.util.Log.d("PlusLusAdapter", "💾 Save clicked: " + news.getTitleNews());

                if (onArticleActionListener != null) {
                    onArticleActionListener.onArticleSave(news);

                    // Mettre à jour l'icône après clic
                    try {
                        FavorisDataBase db = FavorisDataBase.getInstance(context);
                        News existingNews  = db.getNews(news.getIdNews(), news.getArtOrPubOrVid());

                        if (existingNews != null) {
                            holder.ivSave.setImageResource(R.drawable.ic_bookmark_filled);
                        } else {
                            holder.ivSave.setImageResource(R.drawable.icon_save_r);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("PlusLusAdapter", "❌ Erreur mise à jour icône: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(context, "Article sauvegardé", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ── Share ────────────────────────────────────────────────
        if (holder.ivShare != null) {
            holder.ivShare.setVisibility(View.VISIBLE);
            holder.ivShare.setOnClickListener(v -> {
                v.setClickable(true);
                android.util.Log.d("PlusLusAdapter", "🔗 Share clicked: " + news.getTitleNews());

                if (onArticleActionListener != null) {
                    onArticleActionListener.onArticleShare(news);
                } else {
                    shareArticle(news);
                }
            });
        }

        // ── Clic item → détail ───────────────────────────────────
        View.OnClickListener openDetailListener = v -> {
            if (onArticleClickListener != null) {
                android.util.Log.d("PlusLusAdapter", "📰 Ouverture détail: " + news.getTitleNews());
                onArticleClickListener.onArticleClick(news, holder.getAdapterPosition());
            }
        };

        if (holder.ivArticleImage != null) {
            holder.ivArticleImage.setOnClickListener(openDetailListener);
        }
        if (holder.tvArticleTitle != null) {
            holder.tvArticleTitle.setOnClickListener(openDetailListener);
        }
        holder.itemView.setOnClickListener(openDetailListener);
    }

    // ── Partage fallback ─────────────────────────────────────────
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
            android.util.Log.e("PlusLusAdapter", "❌ Erreur partage: " + e.getMessage());
        }
    }

    // ── Format date ──────────────────────────────────────────────
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    // ── Mise à jour des données ───────────────────────────────────
    public void updateData(List<News> newData) {
        newsList.clear();
        newsList.addAll(newData);
        notifyDataSetChanged();
    }
}