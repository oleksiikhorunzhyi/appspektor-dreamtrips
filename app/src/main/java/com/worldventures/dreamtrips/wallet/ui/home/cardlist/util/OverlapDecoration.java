package com.worldventures.dreamtrips.wallet.ui.home.cardlist.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class OverlapDecoration extends RecyclerView.ItemDecoration {

    private int vertOverlap = 0;

    public OverlapDecoration(int vertOverlap) {
        if (vertOverlap >= 0) {
            throw new IllegalArgumentException("vertOverlap should be negative");
        }
        this.vertOverlap = vertOverlap;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        if (itemPosition > 0) {
            outRect.set(0, vertOverlap, 0, 0);
        }
    }
}