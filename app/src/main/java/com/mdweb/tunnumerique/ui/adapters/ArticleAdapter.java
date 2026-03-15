package com.mdweb.tunnumerique.ui.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.banner.BannerView;
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
import com.mdweb.tunnumerique.ui.activitys.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_VIDEO = 3;
    private final int VIEW_PROG = 0;
    private final int VIEW_PUB = 2;


    private Context context;
    private ArrayList<News> newsList;
    private String newsOrPub;
    private Utils utils;
    private LocalFilesManager locallyFiles;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isFavorisArticle;
    private int type;
    private String cat;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private Handler handler;

    public ArticleAdapter(Context context, boolean isFavorisArticle, int type) {

        this.isFavorisArticle = isFavorisArticle;
        newsList = new ArrayList<>();
        this.context = context;
        this.type = type;
        utils = new Utils(context);
        handler = new Handler();
    }

    public ArticleAdapter(Context context, boolean isFavorisArticle, int type, String cat) {
        this.cat = cat;
        this.isFavorisArticle = isFavorisArticle;
        newsList = new ArrayList<>();
        this.context = context;
        this.type = type;
        utils = new Utils(context);
        handler = new Handler();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == VIEW_PUB)
            return new PublicityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pub, parent, false));
        if (viewType == VIEW_VIDEO)
            return new ArticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));

        else if (viewType == VIEW_ITEM) {
            return new ArticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ArticleViewHolder) {
//
            final News news = newsList.get(position);
            if (news.getArtOrPubOrVid() == Constant.isVideo) {
                ((ArticleViewHolder) holder).iconVideo.setVisibility(View.VISIBLE);
            } else
                ((ArticleViewHolder) holder).iconVideo.setVisibility(View.GONE);

            ((ArticleViewHolder) holder).titleArticle.setText(news.getTitleNews());
            ((ArticleViewHolder) holder).authorArticle.setText(news.getAuthorNameNews());
            //  ((ArticleViewHolder) holder).titleArticle.setTypeface(Typeface.createFromAsset(context.getAssets(), "Roboto/Roboto-Regular.ttf"));


            ((ArticleViewHolder) holder).dateArticle.setText(news.getDateNews());

            ImageLoader.getInstance().displayImage(news.getImageUrlNews(), ((ArticleViewHolder) holder).imageArticle, utils.getImageLoaderOptionNews());

            // set image favorite
            if (FavorisDataBase.getInstance(context).getNews(news.getIdNews(), news.getArtOrPubOrVid()) != null) {
                //  Log.e("BaseLngNEws", FavorisDataBase.getInstance(context).  getNews(news.getIdNews(), news.getArtOrPubOrVid(), SessionManager.getInstance().getCurrentLang(context)).getNewsLng());
                ((ArticleViewHolder) holder).favorisImage.setImageResource(R.drawable.ic_like_on);
                ((ArticleViewHolder) holder).favorisImage.setColorFilter(ContextCompat.getColor(context, R.color.green_color));
            } else {
                ((ArticleViewHolder) holder).favorisImage.setImageResource(R.drawable.ic_like_off);
                ((ArticleViewHolder) holder).favorisImage.setColorFilter(ContextCompat.getColor(context, R.color.tint_color_fav));
            }

            // detail article
            ((ArticleViewHolder) holder).itemNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                        intent.putExtra("cat", cat);
                        intent.putExtra("position", position - 1);
                        int x = position - 1;

                    }
                    if (cat != null) {
                        context.startActivity(intent);
                    } else {
                        ((MainActivity) context).startActivityForResult(intent, MainActivity.STARTER_CODE);
                    }


                }


            });

            // share article
            ((ArticleViewHolder) holder).shareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ArticleViewHolder) holder).shareImage.setEnabled(false);
                    //create share object
                    ShareSN shareSN = new ShareSN((Activity) context);
                    shareSN.share(news.getShareUrlNews(), news.getTitleNews());


                    // enabled share button
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((ArticleViewHolder) holder).shareImage.setEnabled(true);
                        }
                    }, 500);
                }
            });


            //
            ((ArticleViewHolder) holder).favorisImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FavorisDataBase.getInstance(context).getNews(news.getIdNews(), news.getArtOrPubOrVid()) != null) {
                        ((ArticleViewHolder) holder).favorisImage.setImageResource(R.drawable.ic_like_off);
                        ((ArticleViewHolder) holder).favorisImage.setColorFilter(ContextCompat.getColor(context, R.color.tint_color_fav));

                        FavorisDataBase.getInstance(context).deleteNews(news.getIdNews(), news.getArtOrPubOrVid());
                        if (isFavorisArticle) {
                            removeItem(holder.getAdapterPosition());
                        }

                    } else {
                        news.setNewsLng(SessionManager.getInstance().getCurrentLang(context));
                        FavorisDataBase.getInstance(context).addNews(news);
                        ((ArticleViewHolder) holder).favorisImage.setImageResource(R.drawable.ic_like_on);
                        ((ArticleViewHolder) holder).favorisImage.setColorFilter(ContextCompat.getColor(context, R.color.green_color));


                    }


                }
            });

        } else if (holder instanceof PublicityViewHolder) {
                ((PublicityViewHolder) holder).huaweiBannerView.setVisibility(View.VISIBLE);
                // Create an ad request to load an ad.
                AdParam adParam = new AdParam.Builder().build();
                ((PublicityViewHolder) holder).huaweiBannerView.setAdListener(new AdListener(){
                    @Override
                    public void onAdFailed(int i) {
                        Log.e("TestAdsHu",i+"");
                        super.onAdFailed(i);
                    }

                    @Override
                    public void onAdLoaded() {
                        Log.e("TestAdsHu","loaded");
                        super.onAdLoaded();
                    }
                });
                ((PublicityViewHolder) holder).huaweiBannerView.loadAd(adParam);


//            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice("7D695D3AC4FB519C1FA3709B3B549A1A")
//                    .addTestDevice("892885D9B6655419C43ED99C42E27E13")
//                    .addTestDevice("25959EC314C3C28FB67A6747490544F3")
//                    .build();

        }


    }


    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;

        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
            pBar.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.green_color), PorterDuff.Mode.MULTIPLY);

        }
    }


    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageArticle;
        private ImageView iconVideo;
        private TextView titleArticle;
        private TextView authorArticle;
        private TextView dateArticle;
        private TextView categ;/////////////////
        private ImageView shareImage;
        private ImageView favorisImage;
        private LinearLayout itemNews;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            imageArticle = (ImageView) itemView.findViewById(R.id.image_article);
            iconVideo = (ImageView) itemView.findViewById(R.id.icon_video);
            shareImage = (ImageView) itemView.findViewById(R.id.image_share_article);
            favorisImage = (ImageView) itemView.findViewById(R.id.image_favoris_article);
//            pubImage = (ImageView) itemView.findViewById(R.id.image_header_theme);
            titleArticle = (TextView) itemView.findViewById(R.id.title_article);
            authorArticle = (TextView) itemView.findViewById(R.id.auteur_article);
            dateArticle = (TextView) itemView.findViewById(R.id.date_article);
            categ = (TextView) itemView.findViewById(R.id.cat_article);
            itemNews = (LinearLayout) itemView.findViewById(R.id.item_news);

        }
    }

    public class PublicityViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagePub;
        BannerView huaweiBannerView;
        private LinearLayout contentPub;

        public PublicityViewHolder(View itemView) {
            super(itemView);
            imagePub = (ImageView) itemView.findViewById(R.id.pub_image);
//            webViewPub = (WebView) itemView.findViewById(R.id.web_view_pub);
            contentPub = (LinearLayout) itemView.findViewById(R.id.content_item_pub);
            huaweiBannerView = itemView.findViewById(R.id.hw_banner_view);
        }
    }

    public void setFilter(List<News> news) {
        newsList = new ArrayList<>();
        newsList.addAll(news);
        notifyDataSetChanged();
    }


    public void addAll(List<News> lst) {
        newsList.clear();
        newsList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<News> lst) {
        newsList.addAll(lst);
        notifyItemRangeChanged(0, newsList.size());
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }


    @Override
    public int getItemViewType(int position) {
        if (newsList.get(position) != null && newsList.get(position).getArtOrPubOrVid() == Constant.isPublicity) {
            return VIEW_PUB;
        } else if (newsList.get(position) != null && newsList.get(position).getArtOrPubOrVid() == Constant.isVideo) {
            return VIEW_VIDEO;
        } else
            return newsList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    /**
     * remove article from list
     *
     * @param position the position of article to be removed
     */
    public void removeItem(int position) {

        newsList.remove(position);
        notifyItemRangeRemoved(position, 1);

    }


}
