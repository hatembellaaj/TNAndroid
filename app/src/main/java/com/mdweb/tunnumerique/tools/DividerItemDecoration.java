package com.mdweb.tunnumerique.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int dividerHeight;
    private final int dividerColor;
    private final int spacingTop;    // Espacement AVANT le trait
    private final int spacingBottom; // Espacement APRÈS le trait
    private final Paint paint;

    // Constructeur avec espacement personnalisé
    public DividerItemDecoration(int dividerHeight, int dividerColor, int spacingTop, int spacingBottom) {
        this.dividerHeight = dividerHeight;
        this.dividerColor = dividerColor;
        this.spacingTop = spacingTop;
        this.spacingBottom = spacingBottom;
        this.paint = new Paint();
        this.paint.setColor(dividerColor);
    }

    // Constructeur simplifié (espacement par défaut)
    public DividerItemDecoration(int dividerHeight, int dividerColor) {
        this(dividerHeight, dividerColor, 0, 0);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);

            // Dessiner le trait avec espacement en haut
            int top = child.getBottom() + spacingTop;
            int bottom = top + dividerHeight;

            c.drawRect(left, top, right, bottom, paint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // Espacement total = spacingTop + dividerHeight + spacingBottom
        outRect.bottom = spacingTop + dividerHeight + spacingBottom;
    }
}