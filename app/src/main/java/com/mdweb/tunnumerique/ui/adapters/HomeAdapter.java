package com.mdweb.tunnumerique.ui.adapters;
// HomeAdapter.java

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.HomeItem;
import com.mdweb.tunnumerique.data.model.HomeSection;

import java.util.List;

/**
 * HomeAdapter : gère plusieurs viewTypes dans UN seul RecyclerView
 * - TYPE_SLIDER : header ViewPager2 (slider)
 * - TYPE_SECTION_TITLE : simple TextView titre
 * - TYPE_HORIZONTAL_LIST : RecyclerView horizontal (ArticleAdapter)
 * - TYPE_VERTICAL_LIST : RecyclerView vertical (VerticalNewsAdapter interne)
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeItem> items;

    public HomeAdapter(List<HomeItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_section, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.sectionTitle.setText(items.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
        }
    }
}
