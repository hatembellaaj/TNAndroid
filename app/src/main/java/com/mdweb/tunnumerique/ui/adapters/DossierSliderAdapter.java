package com.mdweb.tunnumerique.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.data.model.News;
import com.mdweb.tunnumerique.data.sqlite.FavorisDataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DossierSliderAdapter extends RecyclerView.Adapter<DossierSliderAdapter.DossierViewHolder> {

    private Context context;
    private List<News> dossiersList;
    private OnDossierClickListener onDossierClickListener;
    private OnDossierActionListener onDossierActionListener;
    private int currentPosition = 0;

    public interface OnDossierClickListener {
        void onDossierClick(News dossier);
    }

    public interface OnDossierActionListener {
        void onDossierSave(News dossier);
        void onDossierShare(News dossier);
    }

    public DossierSliderAdapter(Context context, List<News> dossiersList) {
        this.context = context;
        this.dossiersList = dossiersList;
    }

    public void setOnDossierClickListener(OnDossierClickListener listener) {
        this.onDossierClickListener = listener;
    }

    public void setOnDossierActionListener(OnDossierActionListener listener) {
        this.onDossierActionListener = listener;
    }

    @NonNull
    @Override
    public DossierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dossier_slider, parent, false);
        return new DossierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DossierViewHolder holder, int position) {
        News dossier = dossiersList.get(position);

        android.util.Log.d("DossierSliderAdapter", "Position: " + position + " - Titre: " + dossier.getTitleNews());

        // Titre du dossier
        if (holder.title != null) {
            holder.title.setText(dossier.getTitleNews());
            holder.title.setVisibility(View.VISIBLE);
        }

        // ✅ DATE formatée "9 Novembre 2025"
        if (holder.date != null) {
            String rawDate = dossier.getDateNews(); // ← adaptez si le getter est différent
            if (rawDate != null && !rawDate.isEmpty()) {
                holder.date.setText(formatDate(rawDate));
                holder.date.setVisibility(View.VISIBLE);
            } else {
                holder.date.setVisibility(View.GONE);
            }
        }

        // Charger l'image avec Glide
        if (holder.image != null && dossier.getImageUrlNews() != null) {
            try {
                Glide.with(context)
                        .load(dossier.getImageUrlNews())
                        .centerCrop()
                        .into(holder.image);
            } catch (Exception e) {
                android.util.Log.e("DossierSliderAdapter", "❌ Erreur image: " + e.getMessage());
            }
        }

        // Configurer les dots
        if (holder.dotsIndicator != null) {
            setupDotsInCard(holder.dotsIndicator, dossiersList.size());
        }

        // CLIC SUR IMAGE ET TITRE → Ouvrir détail
        View.OnClickListener openDetailListener = v -> {
            if (onDossierClickListener != null) {
                android.util.Log.d("DossierSliderAdapter", "📰 Ouverture détail: " + dossier.getTitleNews());
                onDossierClickListener.onDossierClick(dossier);
            }
        };

        if (holder.image != null) {
            holder.image.setOnClickListener(openDetailListener);
        }

        if (holder.title != null) {
            holder.title.setOnClickListener(openDetailListener);
        }

        // VÉRIFIER SI LE DOSSIER EST EN FAVORIS ET METTRE À JOUR L'ICÔNE
        if (holder.bookmark != null) {
            holder.bookmark.setVisibility(View.VISIBLE);

            try {
                FavorisDataBase db = FavorisDataBase.getInstance(context);
                News existingNews = db.getNews(dossier.getIdNews(), dossier.getArtOrPubOrVid());

                if (existingNews != null) {
                    holder.bookmark.setImageResource(R.drawable.ic_bookmark_filled);
                } else {
                    holder.bookmark.setImageResource(R.drawable.icon_save_r);
                }

            } catch (Exception e) {
                android.util.Log.e("DossierSliderAdapter", "❌ Erreur vérification favoris: " + e.getMessage());
                holder.bookmark.setImageResource(R.drawable.icon_save_r);
            }

            holder.bookmark.setOnClickListener(v -> {
                android.util.Log.d("DossierSliderAdapter", "💾 Bookmark clicked: " + dossier.getTitleNews());

                if (onDossierActionListener != null) {
                    onDossierActionListener.onDossierSave(dossier);

                    try {
                        FavorisDataBase db = FavorisDataBase.getInstance(context);
                        News existingNews = db.getNews(dossier.getIdNews(), dossier.getArtOrPubOrVid());

                        if (existingNews != null) {
                            holder.bookmark.setImageResource(R.drawable.ic_bookmark_filled);
                        } else {
                            holder.bookmark.setImageResource(R.drawable.icon_save_r);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("DossierSliderAdapter", "❌ Erreur mise à jour icône: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(context, "Article sauvegardé", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // CLIC SUR SHARE
        if (holder.share != null) {
            holder.share.setVisibility(View.VISIBLE);
            holder.share.setOnClickListener(v -> {
                android.util.Log.d("DossierSliderAdapter", "🔗 Share clicked: " + dossier.getTitleNews());

                if (onDossierActionListener != null) {
                    onDossierActionListener.onDossierShare(dossier);
                } else {
                    shareArticle(dossier);
                }
            });
        }
    }

    /**
     * ✅ Formate la date en "9 Novembre 2025"
     * Supporte les formats courants : yyyy-MM-dd, yyyy-MM-dd'T'HH:mm:ss, dd/MM/yyyy
     */
    private String formatDate(String rawDate) {
        // Formats d'entrée possibles à essayer dans l'ordre
        String[] inputFormats = {
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "dd-MM-yyyy"
        };

        // Format de sortie : "9 Novembre 2025" en français
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.FRENCH);

        for (String format : inputFormats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
                inputFormat.setLenient(false);
                Date date = inputFormat.parse(rawDate);
                if (date != null) {
                    // Capitalize première lettre du mois
                    String result = outputFormat.format(date);
                    return result.substring(0, 1).toUpperCase() + result.substring(1);
                }
            } catch (ParseException ignored) {
                // Essayer le prochain format
            }
        }

        // Si aucun format ne correspond, retourner la date brute
        android.util.Log.w("DossierSliderAdapter", "⚠️ Format de date non reconnu: " + rawDate);
        return rawDate;
    }

    /**
     * Partage l'article via Intent (fallback)
     */
    private void shareArticle(News dossier) {
        try {
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            String shareText = dossier.getTitleNews() + "\n\n" +
                    dossier.getDescriptionNews();

            if (dossier.getShareUrlNews() != null && !dossier.getShareUrlNews().isEmpty()) {
                shareText += "\n\nLire plus: " + dossier.getShareUrlNews();
            }

            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Partager via"));

        } catch (Exception e) {
            android.util.Log.e("DossierSliderAdapter", "❌ Erreur partage: " + e.getMessage());
        }
    }

    /**
     * Configure les dots dans chaque card
     */
    private void setupDotsInCard(LinearLayout dotsContainer, int count) {
        dotsContainer.setVisibility(View.VISIBLE);
        dotsContainer.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(context);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(12, 12);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);

            if (i == currentPosition) {
                dot.setImageResource(R.drawable.dot_active);
            } else {
                dot.setImageResource(R.drawable.dot_inactive);
            }

            dotsContainer.addView(dot);
        }
    }

    /**
     * Met à jour la position actuelle et rafraîchit TOUS les dots
     */
    public void updateDots(int position) {
        if (currentPosition == position) return;
        currentPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dossiersList != null ? dossiersList.size() : 0;
    }

    static class DossierViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView date; // ✅ Ajouté
        ImageView bookmark;
        ImageView share;
        LinearLayout dotsIndicator;

        public DossierViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.dossierImage);
            title = itemView.findViewById(R.id.dossierTitle);
            date = itemView.findViewById(R.id.dossierDate); // ✅ Ajouté
            bookmark = itemView.findViewById(R.id.dossierBookmark);
            share = itemView.findViewById(R.id.dossierShare);
            dotsIndicator = itemView.findViewById(R.id.dotsIndicatorInCard);
        }
    }
}