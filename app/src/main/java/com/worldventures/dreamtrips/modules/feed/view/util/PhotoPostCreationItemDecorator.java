package com.worldventures.dreamtrips.modules.feed.view.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class PhotoPostCreationItemDecorator extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = (int) parent.getContext().getResources().getDimension(R.dimen.spacing_small);
    }
}
