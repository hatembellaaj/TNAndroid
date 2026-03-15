package com.mdweb.tunnumerique.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.banner.BannerView;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.Blague;
import com.mdweb.tunnumerique.tools.shared.Constant;
import com.mdweb.tunnumerique.tools.style.ShareSN;
import com.mdweb.tunnumerique.ui.activitys.DetailsBlagueActivity;
import com.mdweb.tunnumerique.ui.activitys.MainActivity;

import java.util.List;

public class BlagueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PUB = 2;

    private List<Blague> blagueList;
    Context context;

    public BlagueAdapter(List<Blague> blagueList, Context context ) {
        this.blagueList = blagueList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_PUB)
            return new PublicityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pub, parent, false));
        else {
            return new BlagueHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blague, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Blague currentBlague = blagueList.get(position);

        if (holder instanceof BlagueHolder) {
            if (currentBlague.getDescriptionBlagues() != null && !currentBlague.getDescriptionBlagues().isEmpty()) {
                ((BlagueHolder) holder).titleBlague.setText(currentBlague.getDescriptionBlagues());
            } else {
                ((BlagueHolder) holder).titleBlague.setText(currentBlague.getTitleBlague());
            }
            //   ((BlagueHolder) holder).noteText.setText(currentBlague.getNoteBlagues());
            ((BlagueHolder) holder).blagueLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BlagueHolder) holder).showDetails(position);
                }
            });
            ((BlagueHolder) holder).readBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BlagueHolder) holder).showDetails(position);
                }
            });


        } else if (holder instanceof PublicityViewHolder) {
                ((PublicityViewHolder) holder).huaweiBannerView.setVisibility(View.VISIBLE);
                // Create an ad request to load an ad.
                AdParam adParam = new AdParam.Builder().build();
                ((PublicityViewHolder) holder).huaweiBannerView.loadAd(adParam);
        }


    }

    @Override
    public int getItemCount() {
        return blagueList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (blagueList.get(position) != null && blagueList.get(position).getArtOrPubOrVid() == Constant.isPublicity) {
            return VIEW_PUB;
        } else
            return VIEW_ITEM;
    }

}

class BlagueHolder extends RecyclerView.ViewHolder {
    TextView titleBlague;
    //TextView noteText;
    ConstraintLayout blagueLayout;
    Context ctx;
    LinearLayout readBtn;

    public BlagueHolder(View itemView) {
        super(itemView);
        ctx = itemView.getContext();
        titleBlague = itemView.findViewById(R.id.title_blague);
        blagueLayout = itemView.findViewById(R.id.layout_blagues);
        readBtn = itemView.findViewById(R.id.read);
        titleBlague.setTypeface(Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/din-next_-ar-regular.otf"));
        //noteText = itemView.findViewById(R.id.note);


    }

    public void showDetails(int position) {
        Intent i = new Intent(ctx, DetailsBlagueActivity.class);
        i.putExtra("position", position);

        ((MainActivity)ctx).startActivityForResult(i,MainActivity.STARTER_CODE);

    }

    public void share(Blague blague) {
        ShareSN shareSN = new ShareSN((Activity) ctx);
        shareSN.share(blague.getBlagueUrl(), blague.getTitleBlague());


    }

}

class PublicityViewHolder extends RecyclerView.ViewHolder {
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

