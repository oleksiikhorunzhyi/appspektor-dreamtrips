package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class SideMarginsItemDecorator extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) != 0) {
            int spacing = (int) parent.getResources().getDimension(R.dimen.feed_spacing);

            outRect.left = spacing;
            outRect.right = spacing;
        }
    }
}
