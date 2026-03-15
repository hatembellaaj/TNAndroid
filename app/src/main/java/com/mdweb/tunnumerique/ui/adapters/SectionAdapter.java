package com.mdweb.tunnumerique.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Article;
import com.mdweb.tunnumerique.data.model.HomeSection;
import com.mdweb.tunnumerique.data.model.SectionHome;

import java.util.List;

/**
 * SectionAdapter: affiche chaque section (titre + RecyclerView horizontal)
 */
public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    private final Context context;
    private final List<HomeSection> sections;
    // Pool partagé pour optimiser les recyclages des RecyclerViews enfants
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public SectionAdapter(Context context, List<HomeSection> sections) {
        this.context = context;
        this.sections = sections;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_home_section, parent, false);
        return new SectionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        HomeSection section = sections.get(position);
        holder.title.setText(section.getTitle() != null ? section.getTitle() : "");

        // Setup RecyclerView horizontal (child)
        RecyclerView childRecycler = holder.recycler;
        // Only set layout manager once to avoid resetting scroll position
        if (childRecycler.getLayoutManager() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            childRecycler.setLayoutManager(layoutManager);
            // improve performance for fixed-size children
            childRecycler.setHasFixedSize(true);
            childRecycler.setItemViewCacheSize(10);
            childRecycler.setRecycledViewPool(viewPool);
        }

        // Adapter for child recycler
        List<Article> articles = section.getArticles();
        if (articles == null) articles = java.util.Collections.emptyList();

        // If already the same adapter instance, just update data if needed
        if (childRecycler.getAdapter() instanceof ArticleAdapter) {
            ArticlesAdapter existing = (ArticlesAdapter) childRecycler.getAdapter();
            existing.updateList(articles);
        } else {
            ArticlesAdapter adapter = new ArticlesAdapter(context, articles);
            childRecycler.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return sections == null ? 0 : sections.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RecyclerView recycler;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.sectionTitle);
            recycler = itemView.findViewById(R.id.sectionRecycler);
        }
    }
}
