package com.mdweb.tunnumerique.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.tools.mdwebNetworkingLib.jsonRequest.LocalFilesManager;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.tools.style.ShareSN;
import com.mdweb.tunnumerique.ui.activitys.DetailArticleFragment;
import com.mdweb.tunnumerique.ui.activitys.DetailsVideoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class FavorisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_ITEM = 1;
    private static final int VIEW_PUB  = 2;

    private Context             context;
    private ArrayList<News>     newsList;
    private Utils               utils;
    private LinearLayoutManager mLinearLayoutManager;
    private LocalFilesManager   locallyFiles;
    private Handler             handler;
    private int                 type;

    // ────────────────────────────────────────────────
    //  Constructor
    // ────────────────────────────────────────────────

    public FavorisAdapter(Context context, int type) {
        this.context  = context;
        this.type     = type;
        this.newsList = new ArrayList<>();
        this.utils    = new Utils(context);
        this.handler  = new Handler();
    }

    // ────────────────────────────────────────────────
    //  ViewHolder creation
    // ────────────────────────────────────────────────

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_PUB) {
            // Réutilise le même item pub que l'ancien adapter
            return new PubViewHolder(inflater.inflate(R.layout.item_pub, parent, false));
        } else {
            // Nouveau XML item favoris (image + titre + date + save + share)
            return new FavorisViewHolder(inflater.inflate(R.layout.item_favoris, parent, false));
        }
    }

    // ────────────────────────────────────────────────
    //  Bind
    // ────────────────────────────────────────────────

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FavorisViewHolder) {
            final FavorisViewHolder vh   = (FavorisViewHolder) holder;
            final News              news = newsList.get(position);

            // ── Titre
            vh.tvArticleTitle.setText(news.getTitleNews());

            // ── Date
            vh.tvArticleDate.setText(news.getDateNews());

            // ── Image (UniversalImageLoader — même logique que ArticleAdapter)
            ImageLoader.getInstance().displayImage(
                    news.getImageUrlNews(),
                    vh.ivArticleImage,
                    utils.getImageLoaderOptionNews()
            );

            // ── Icône Save : on/off selon SQLite
            refreshSaveIcon(vh, news);

            // ── Click item → ouvrir détail
            vh.itemView.setOnClickListener(v -> {
                Intent intent;
                if (news.getArtOrPubOrVid() == Constant.isVideo) {
                    intent = new Intent(context, DetailsVideoActivity.class);
                    intent.putExtra("Article", news);
                    intent.putExtra("type", type);
                } else {
                    intent = new Intent(context, DetailArticleFragment.class);
                    String jsonNewsList = new Gson().toJson(newsList);
                    locallyFiles = new LocalFilesManager(context);
                    locallyFiles.saveLocallyFile("Monfile", jsonNewsList);
                    intent.putExtra("type", type);
                    intent.putExtra("position", position - 1);
                }
                context.startActivity(intent);
            });

            // ── Click Share
            vh.ivShare.setOnClickListener(v -> {
                vh.ivShare.setEnabled(false);
                ShareSN shareSN = new ShareSN((Activity) context);
                shareSN.share(news.getShareUrlNews(), news.getTitleNews());
                handler.postDelayed(() -> vh.ivShare.setEnabled(true), 500);
            });

            // ── Click Save (favori toggle)
            vh.ivSave.setOnClickListener(v -> {
                if (FavorisDataBase.getInstance(context)
                        .getNews(news.getIdNews(), news.getArtOrPubOrVid()) != null) {

                    // Déjà en favori → supprimer
                    FavorisDataBase.getInstance(context)
                            .deleteNews(news.getIdNews(), news.getArtOrPubOrVid());

                    // Retirer de la liste (on est dans FavorisActivity)
                    removeItem(holder.getAdapterPosition());

                } else {
                    // Pas encore favori → ajouter
                    news.setNewsLng(SessionManager.getInstance().getCurrentLang(context));
                    FavorisDataBase.getInstance(context).addNews(news);
                    refreshSaveIcon(vh, news);
                }
            });

        } else if (holder instanceof PubViewHolder) {
            // Pub Huawei — même logique que ArticleAdapter
            // (BannerView déjà dans le layout item_pub)
        }
    }

    // ────────────────────────────────────────────────
    //  Helpers
    // ────────────────────────────────────────────────

    /**
     * Met à jour l'icône save selon l'état SQLite.
     */
    private void refreshSaveIcon(FavorisViewHolder vh, News news) {
        boolean isSaved = FavorisDataBase.getInstance(context)
                .getNews(news.getIdNews(), news.getArtOrPubOrVid()) != null;

        if (isSaved) {
            vh.ivSave.setImageResource(R.drawable.icon_save_r);
            vh.ivSave.setColorFilter(
                    ContextCompat.getColor(context, R.color.green_color));
        } else {
            vh.ivSave.setImageResource(R.drawable.icon_save_r);
            vh.ivSave.setColorFilter(
                    ContextCompat.getColor(context, R.color.tint_color_fav));
        }
    }

    // ────────────────────────────────────────────────
    //  ViewHolders
    // ────────────────────────────────────────────────

    /**
     * ViewHolder pour l'item favoris (nouveau XML).
     */
    static class FavorisViewHolder extends RecyclerView.ViewHolder {
        ImageView ivArticleImage;
        TextView  tvArticleTitle;
        TextView  tvArticleDate;
        ImageView ivSave;
        ImageView ivShare;

        FavorisViewHolder(View itemView) {
            super(itemView);
            ivArticleImage  = itemView.findViewById(R.id.ivArticleImage);
            tvArticleTitle  = itemView.findViewById(R.id.tvArticleTitle);
            tvArticleDate   = itemView.findViewById(R.id.tvArticleDate);
            ivSave          = itemView.findViewById(R.id.ivSave);
            ivShare         = itemView.findViewById(R.id.ivShare);
        }
    }

    /**
     * ViewHolder pour la publicité (réutilise item_pub existant).
     */
    static class PubViewHolder extends RecyclerView.ViewHolder {
        PubViewHolder(View itemView) {
            super(itemView);
        }
    }

    // ────────────────────────────────────────────────
    //  Data methods
    // ────────────────────────────────────────────────

    public void addAll(List<News> lst) {
        newsList.clear();
        newsList.addAll(lst);
        notifyDataSetChanged();
    }

    public void setFilter(List<News> news) {
        newsList = new ArrayList<>();
        newsList.addAll(news);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        newsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, newsList.size());
    }

    public void setLinearLayoutManager(LinearLayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
    }

    // ────────────────────────────────────────────────
    //  RecyclerView overrides
    // ────────────────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        News news = newsList.get(position);
        if (news != null && news.getArtOrPubOrVid() == Constant.isPublicity)
            return VIEW_PUB;
        return VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}