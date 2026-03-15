package com.mdweb.tunnumerique.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mdweb.tunnumerique.R;

import java.util.List;

public class NavigationMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NavigationMenuAdapter";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CATEGORIES_CONTAINER = 1;
    private static final int TYPE_SEPARATOR = 2;
    private static final int TYPE_BOTTOM_ITEMS = 3;

    private List<MenuItem> menuItems;
    private OnMenuItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem item, int position);
    }

    public static class MenuItem {
        private String title;
        private String id;
        private boolean isHeader;

        public MenuItem(String title, String id) {
            this.title = title;
            this.id = id;
            this.isHeader = false;
        }

        public MenuItem(String title, String id, boolean isHeader) {
            this.title = title;
            this.id = id;
            this.isHeader = isHeader;
        }

        public String getTitle() { return title; }
        public String getId() { return id; }
        public boolean isHeader() { return isHeader; }
    }

    public NavigationMenuAdapter(List<MenuItem> menuItems, OnMenuItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        else if (position == 1) return TYPE_CATEGORIES_CONTAINER;
        else if (position == 2) return TYPE_SEPARATOR;
        else return TYPE_BOTTOM_ITEMS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("ADAPTER_LIFECYCLE", "onCreateViewHolder type: " + viewType);

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_header_alaune, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_CATEGORIES_CONTAINER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_categories_container, parent, false);
            return new CategoriesContainerViewHolder(view);
        } else if (viewType == TYPE_SEPARATOR) {
            View view = new View(parent.getContext());
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(0xFFE0E0E0);
            return new SeparatorViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_bottom_items_container, parent, false);
            return new BottomItemsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.e("ADAPTER_LIFECYCLE", "════════════════════════════════");
        Log.e("ADAPTER_LIFECYCLE", "onBindViewHolder position: " + position);
        Log.e("ADAPTER_LIFECYCLE", "Holder type: " + holder.getClass().getSimpleName());
        Log.e("ADAPTER_LIFECYCLE", "════════════════════════════════");

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(menuItems, selectedPosition, listener);
        } else if (holder instanceof CategoriesContainerViewHolder) {
            ((CategoriesContainerViewHolder) holder).bind(menuItems, selectedPosition, listener);
        } else if (holder instanceof BottomItemsViewHolder) {
            ((BottomItemsViewHolder) holder).bind(menuItems, listener);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.isEmpty() ? 0 : 4;
    }

    // ═══════════════════════════════════════════════════════════════════
    // HEADER "À LA UNE"
    // ═══════════════════════════════════════════════════════════════════

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.headerTitlee);
        }

        void bind(List<MenuItem> items, int selectedPos, OnMenuItemClickListener listener) {
            MenuItem headerItem = null;
            for (MenuItem item : items) {
                if (item.isHeader()) {
                    headerItem = item;
                    break;
                }
            }
            if (headerItem == null) return;

            MenuItem finalHeaderItem = headerItem;

            int p32 = dp(itemView.getContext(), 32);
            int p24 = dp(itemView.getContext(), 24);
            itemView.setPadding(p32, p24, p32, p24);

            title.setTextSize(16);

            if (selectedPos == 0) {
                title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green_color_tittle_nv));
                title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
                itemView.setBackgroundColor(0xFFF5F5F5);
            } else {
                title.setTextColor(0xFF333333);
                title.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
                itemView.setBackgroundColor(0xFFF5F5F5);
            }

            itemView.setOnClickListener(v -> {
                Log.d(TAG, "✅ Clic sur À LA UNE");
                Toast.makeText(v.getContext(), "CLIC: À LA UNE", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onMenuItemClick(finalHeaderItem, 0);
            });
        }
    }

    static class SeparatorViewHolder extends RecyclerView.ViewHolder {
        SeparatorViewHolder(@NonNull View itemView) { super(itemView); }
    }

    // ═══════════════════════════════════════════════════════════════════
    // CATÉGORIES
    // ═══════════════════════════════════════════════════════════════════

    static class CategoriesContainerViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;

        CategoriesContainerViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.categoriesContainer);
            container.setBackgroundColor(0xFFF5F5F5);

            // ✅ FIX : Ne pas voler les clics des enfants
            container.setClickable(false);
            container.setFocusable(false);
        }

        void bind(List<MenuItem> items, int selectedPos, OnMenuItemClickListener listener) {
            container.removeAllViews();

            Log.e("ADAPTER_BIND", "🔥 BIND CATÉGORIES");

            for (int i = 0; i < items.size(); i++) {
                MenuItem item = items.get(i);

                if (item.isHeader() || item.getId().equals("about") || item.getId().equals("settings")) {
                    continue;
                }

                final int realPosition = i;

                // ✅ CRÉER LE TEXTVIEW
                TextView tv = new TextView(container.getContext());
                tv.setText(capitalizeFirstLetter(item.getTitle()));
                tv.setPadding(dp(container.getContext(), 32), dp(container.getContext(), 18),
                        dp(container.getContext(), 32), dp(container.getContext(), 18));
                tv.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tv.setTextSize(16);
                tv.setTextColor(0xFF222222);
                tv.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));

                // ✅ RENDRE CLIQUABLE
                tv.setClickable(true);
                tv.setFocusable(true);
                tv.setEnabled(true);

                // ✅ CLIC - VERSION ULTRA SIMPLE
                final MenuItem finalItem = item;
                tv.setOnTouchListener((v, event) -> {
                    if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                        Log.e("TOUCH_TEST", "🎯 TOUCH: " + finalItem.getTitle());
                        Toast.makeText(v.getContext(), "TOUCH: " + finalItem.getTitle(), Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            v.post(() -> listener.onMenuItemClick(finalItem, realPosition));
                        }
                        return true;
                    }
                    return false;
                });

                container.addView(tv);

                // Séparateur
                View div = new View(container.getContext());
                div.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1));
                div.setBackgroundColor(0xFFDDDDDD);
                container.addView(div);
            }
        }

        private String capitalizeFirstLetter(String text) {
            if (text == null || text.isEmpty()) return text;
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // BOTTOM ITEMS
    // ═══════════════════════════════════════════════════════════════════

    static class BottomItemsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;

        BottomItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.bottomItemsContainer);
        }

        void bind(List<MenuItem> items, OnMenuItemClickListener listener) {
            container.removeAllViews();

            boolean firstItem = true;

            for (int i = 0; i < items.size(); i++) {
                MenuItem item = items.get(i);

                if (item.getId().equals("about") || item.getId().equals("settings")) {
                    if (!firstItem) {
                        View divider = new View(container.getContext());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        divider.setBackgroundColor(0xFFE0E0E0);
                        container.addView(divider);
                    }

                    final int realPosition = i;

                    TextView tv = new TextView(container.getContext());
                    tv.setText(item.getTitle());

                    int p32 = dp(container.getContext(), 32);
                    int p18 = dp(container.getContext(), 18);
                    tv.setPadding(p32, p18, p32, p18);

                    tv.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    tv.setTextSize(15);
                    tv.setTextColor(0xFFAAAAAA);
                    tv.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

                    tv.setOnClickListener(v -> {
                        Log.d(TAG, "✅ Clic bottom: [" + item.getTitle() + "] position=" + realPosition);
                        Toast.makeText(v.getContext(), "CLIC: " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        if (listener != null) {
                            listener.onMenuItemClick(item, realPosition);
                        } else {
                            Log.e(TAG, "❌ Listener est NULL !");
                        }
                    });

                    int[] attrs = {android.R.attr.selectableItemBackground};
                    @SuppressLint("ResourceType") android.content.res.TypedArray ta = container.getContext().obtainStyledAttributes(attrs);
                    tv.setBackgroundResource(ta.getResourceId(0, 0));
                    ta.recycle();

                    container.addView(tv);
                    firstItem = false;
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPER
    // ═══════════════════════════════════════════════════════════════════

    private static int dp(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}