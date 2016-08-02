package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SideMarginsItemDecorator extends RecyclerView.ItemDecoration {

    private int marginPercentage;
    private boolean ignoreFirstItem;

    public SideMarginsItemDecorator(@IntRange(from = 0, to = 100) int marginPercentage) {
        this(marginPercentage, false);
    }

    public SideMarginsItemDecorator(@IntRange(from = 0, to = 100) int marginPercentage,
                                    boolean ignoreFirstItem) {
        this.marginPercentage = marginPercentage;
        this.ignoreFirstItem = ignoreFirstItem;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if ((ignoreFirstItem && parent.getChildAdapterPosition(view) != 0) || !ignoreFirstItem) {
            outRect.left = getMarginInPx(parent.getContext());
            outRect.right = getMarginInPx(parent.getContext());
        }
    }

    private int getMarginInPx(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels * marginPercentage / 100;
    }
}
