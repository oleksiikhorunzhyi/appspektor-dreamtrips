package com.worldventures.dreamtrips.social.ui.feed.view.cell.util;

import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.social.ui.feed.view.cell.ResizeableCell;


public class ResizeCellScrollListener extends RecyclerView.OnScrollListener {

   private LinearLayoutManager layoutManager;
   private Pair<Integer, Integer> checkedRange = new Pair<>(-1, -1);

   public void onConfigChanged() {
      checkedRange = new Pair<>(-1, -1);
   }

   public ResizeCellScrollListener(LinearLayoutManager layoutManager) {
      this.layoutManager = layoutManager;
   }

   @Override
   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
      int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

      for (int position = firstVisibleItemPosition; position <= lastVisibleItemPosition; position++) {
         View view = layoutManager.findViewByPosition(position);
         RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);

         if (holder instanceof ResizeableCell && (position < checkedRange.first || position > checkedRange.second)) {
            ((ResizeableCell) holder).checkSize();
         }
      }

      checkedRange = new Pair(firstVisibleItemPosition, lastVisibleItemPosition);
   }
}
