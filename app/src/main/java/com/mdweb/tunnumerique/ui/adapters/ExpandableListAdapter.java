package com.mdweb.tunnumerique.ui.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.ItemMenu;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.Utils;
import com.mdweb.tunnumerique.ui.activitys.FilterArticleActivity;
import com.mdweb.tunnumerique.ui.fragments.ActualiteFragment;
import com.mdweb.tunnumerique.ui.fragments.ArticleFavoriFragment;
import com.mdweb.tunnumerique.ui.fragments.HorairePriereFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int positionSelected = 0x0;
    private List<ItemMenu> data;
    private Context context;
    private ActualiteFragment actualiteFragment;
    private ArticleFavoriFragment articlefavoriFragment;
    private HorairePriereFragment horairePriereFragment;
    List<ItemMenu> dataInit;
    private int type;
    // private Fragment fragment;
    private boolean isListCountries = false;
    private Utils utils;
    private FilterArticleActivity filterArticleActivity;
    private boolean isFragment = false;
    private boolean isFavoriFragment = false;
    ExpandableListAdapter expandableListAdapter;

    private long mLastClickTime = 0;


    public ExpandableListAdapter(Context context, List<ItemMenu> data, ActualiteFragment actualiteFragment, boolean isFragment) {
        this.data = data;
        this.context = context;
        this.actualiteFragment = actualiteFragment;
        utils = new Utils(context);
        this.isFragment = isFragment;
        expandableListAdapter = this;
    }

    public ExpandableListAdapter(Context context, List<ItemMenu> data, ArticleFavoriFragment articlefavoriFragment, boolean isFragment, boolean isFavoriFragment) {
        this.data = data;
        this.context = context;
        this.articlefavoriFragment = articlefavoriFragment;
        utils = new Utils(context);
        this.isFragment = isFragment;
        this.isFavoriFragment = isFavoriFragment;
    }

    public ExpandableListAdapter(Context context, List<ItemMenu> data, HorairePriereFragment horairePriereFragment, int type, boolean isListCountries) {

        this.data = data;
        getDataInit();
       // Log.e("GovSelected", data.get(6).getText());
        this.context = context;
        this.utils = new Utils(context);
        this.horairePriereFragment = horairePriereFragment;
        this.isListCountries = isListCountries;
        this.type = type;
        positionSelected = isListCountries? SessionManager.getInstance().getCountryId(context,this.horairePriereFragment.idDefaultCountry):SessionManager.getInstance().getGouvernorateId(context);
        expandableListAdapter = this;
    }

    public ExpandableListAdapter(Context context, List<ItemMenu> data, FilterArticleActivity filterArticleActivity) {
        this.data = data;
        this.context = context;
        utils = new Utils(context);
        this.filterArticleActivity = filterArticleActivity;
        expandableListAdapter = this;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;

        LayoutInflater inflaterChild = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflaterChild.inflate(R.layout.item_menu_child, parent, false);
        viewHolder = new ListChildViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemMenu item = data.get(position);

        final ListChildViewHolder childViewHolder = (ListChildViewHolder) holder;
        childViewHolder.title.setText(data.get(position).getText());

        if (type == 1) {
            //  childViewHolder.icon.setImageResource(data.get(position).getIcon());
            int selectedItemIndex = isListCountries ? item.getId() : horairePriereFragment.findGouvernoratePositionByName(item.getText());
            if (selectedItemIndex == positionSelected) {

                childViewHolder.iconShape.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                childViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                //  childViewHolder.icon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent),android.graphics.PorterDuff.Mode.MULTIPLY);

            } else {
                childViewHolder.iconShape.setColorFilter(ContextCompat.getColor(context, R.color.white_color));
                childViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.black_color));
            }
            if(isListCountries) {
                ImageLoader.getInstance().displayImage(data.get(position).getUrlIconEnable(), childViewHolder.icon, utils.getImageLoaderOptionPub());
            }
            else {
                childViewHolder.icon.setVisibility(View.GONE);
            }
        } else {
            if (item.getId() == positionSelected) {
                //ImageLoader.getInstance().displayImage(data.get(position).getUrlIconEnable(), childViewHolder.icon, utils.getImageLoaderOption());
                //  childViewHolder.icon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent),android.graphics.PorterDuff.Mode.MULTIPLY);
                childViewHolder.icon.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));

                childViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                childViewHolder.iconShape.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));

            } else {
                //ImageLoader.getInstance().displayImage(data.get(position).getUrlIcon(), childViewHolder.icon, utils.getImageLoaderOption());
                childViewHolder.title.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                   childViewHolder.icon.setColorFilter(ContextCompat.getColor(context, R.color.black_color));

                childViewHolder.iconShape.setColorFilter(ContextCompat.getColor(context, R.color.white_color));

            }
        }

        if (position == data.size() - 1) {
            ((ListChildViewHolder) holder).divider.setVisibility(View.GONE);
        } else {
            ((ListChildViewHolder) holder).divider.setVisibility(View.VISIBLE);
        }

        childViewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                positionSelected = item.getId();
                expandableListAdapter.notifyDataSetChanged();

                if (type == 1) {
                    int idItem = 1;
                    if(isListCountries) {
                        if (SessionManager.getInstance().getCountryId(context,horairePriereFragment.idDefaultCountry) != item.getId()) {
                            SessionManager.getInstance().setIdCountry(context, positionSelected);
                            horairePriereFragment.initializeGouvernorateList(item);
                        }
                    }
                    else {
                        positionSelected = horairePriereFragment.findGouvernoratePositionByName(item.getText());
                        SessionManager.getInstance().setIdGouvernorate(context, positionSelected);
                        idItem = positionSelected;
                    }
                    horairePriereFragment.displayView(idItem);
                    horairePriereFragment.demissDialog();
                } else if (isFragment && !isFavoriFragment)
                    actualiteFragment.displayView(positionSelected);
                else
                    filterArticleActivity.displayView(positionSelected);
                if (isFavoriFragment)
                    articlefavoriFragment.displayView(positionSelected);


            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ItemMenu getItem(int position) {
        Iterator<ItemMenu> it = data.listIterator();
        while (it.hasNext()) {
            ItemMenu itemMenu = it.next();
            if (itemMenu.getId() == position)
                return itemMenu;

        }
        return null;
    }

    public List<ItemMenu> getDataInit() {
        dataInit = new ArrayList<ItemMenu>();
        dataInit.addAll(data);
        return dataInit;
    }

    public void clear_keyWors() {
        data.clear();
        notifyDataSetChanged();
    }

    public void initData() {
        data.clear();
        data.addAll(dataInit);
        notifyDataSetChanged();
    }
    public void updateGouvernorateList(List<ItemMenu> newlist) {
        data = newlist;
        this.notifyDataSetChanged();
    }


    /**
     * ViewHolder for Child item
     */
    private static class ListChildViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView icon;
        public ImageView iconShape;
        public View divider;
        RelativeLayout relativeLayout;

        ListChildViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_menu);
            icon = (ImageView) itemView.findViewById(R.id.icon_menu);
            iconShape = (ImageView) itemView.findViewById(R.id.icon_shape);
            divider = (View) itemView.findViewById(R.id.devider);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.container_child);

        }
    }

}