package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler;

/**
 * Created by Andres Rubiano Del Chiaro on 14/10/15.
 */

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public MarginDecoration(Context context) {
//        margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}
