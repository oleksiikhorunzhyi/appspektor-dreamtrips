package com.worldventures.dreamtrips.modules.dtl.view.cell.delegates;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ScrollingManager {

   private LinearLayoutManager layoutManager;

   public void setup(RecyclerView recyclerView) {
      this.layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
   }

   public void scrollToPosition(int position) {
      int itemCount = layoutManager.getItemCount();
      if (position >= 0 && position < itemCount && isListItemInvisible(position)) {
         layoutManager.scrollToPosition(position);
      }
   }

   private boolean isListItemInvisible(int position) {
      int firstVisible = layoutManager.findFirstVisibleItemPosition();
      int lastVisible = layoutManager.findLastVisibleItemPosition();
      return position < firstVisible || position > lastVisible;
   }
}
