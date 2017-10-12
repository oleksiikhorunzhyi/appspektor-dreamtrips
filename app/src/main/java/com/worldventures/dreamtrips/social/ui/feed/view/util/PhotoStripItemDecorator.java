package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class PhotoStripItemDecorator extends RecyclerView.ItemDecoration {

   @Override
   public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      int position = parent.getChildAdapterPosition(view);
      if (position == 0) {
         outRect.left = 0;
      } else if (position == parent.getAdapter().getItemCount() - 1) {
         outRect.right = 0;
         outRect.left = getDimen(parent, R.dimen.photo_strip_space_between_items);
      } else {
         outRect.left = getDimen(parent, R.dimen.photo_strip_space_between_items);
      }
   }

   private int getDimen(View view, int resource) {
      return (int) view.getContext().getResources().getDimension(resource);
   }
}