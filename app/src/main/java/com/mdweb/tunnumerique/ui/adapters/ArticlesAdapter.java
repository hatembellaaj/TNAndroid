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
import com.mdweb.tunnumerique.data.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleVH> {

    private List<Article> list;
    private Context context;

    public ArticlesAdapter(Context ctx, List<Article> list) {
        this.context = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public ArticleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider_article, parent, false);  // <-- HERE
        return new ArticleVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleVH h, int pos) {
        Article a = list.get(pos);

        h.title.setText(a.getTitle());

        Glide.with(context)
                .load(a.getImageUrl())
                .into(h.image);
    }

    @Override
    public int getItemCount() { return list.size(); }

    public void updateList(List<Article> newList) {
        this.list = newList == null ? new ArrayList<>() : newList;
        notifyDataSetChanged();
    }

    class ArticleVH extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        private List<Article> list;

        ArticleVH(View v) {
            super(v);
            title = v.findViewById(R.id.sliderTitle);
            image = v.findViewById(R.id.sliderImage);
        }
        // inside ArticleAdapter class
        public void updateList(List<Article> newList) {
            this.list = newList == null ? new ArrayList<>() : newList;
            notifyDataSetChanged();
        }
    }
}


