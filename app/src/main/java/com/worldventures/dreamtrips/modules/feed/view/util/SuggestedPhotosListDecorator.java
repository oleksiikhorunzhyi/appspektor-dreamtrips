package com.worldventures.dreamtrips.modules.feed.view.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class SuggestedPhotosListDecorator extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = getDimen(parent, R.dimen.suggested_cell_spacing);
        } else if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.right = getDimen(parent, R.dimen.suggested_cell_spacing);
            outRect.left = getDimen(parent, R.dimen.suggested_cell_spacing_between_elements);
        } else {
            outRect.left = getDimen(parent, R.dimen.suggested_cell_spacing_between_elements);
        }
    }

    private int getDimen(View view, int resource) {
        return (int) view.getContext().getResources().getDimension(resource);
    }
}
