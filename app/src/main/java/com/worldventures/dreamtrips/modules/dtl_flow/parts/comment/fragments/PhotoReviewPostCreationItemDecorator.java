package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class PhotoReviewPostCreationItemDecorator extends RecyclerView.ItemDecoration {

   @Override
   public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      outRect.bottom = (int) parent.getContext().getResources().getDimension(R.dimen.spacing_small);
   }
}
