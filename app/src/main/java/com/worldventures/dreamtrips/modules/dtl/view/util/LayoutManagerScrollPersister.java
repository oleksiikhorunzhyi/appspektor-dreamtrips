package com.worldventures.dreamtrips.modules.dtl.view.util;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

public class LayoutManagerScrollPersister {

   private static final String STATE_SCROLL_POSITION = "STATE_SCROLL_POSITION";
   private static final String STATE_SCROLL_OFFSET = "STATE_SCROLL_OFFSET";
   private static final String STATE_SCROLL_ITEMS_COUNT = "STATE_SCROLL_ITEMS_COUNT";

   public Parcelable saveScrollState(Parcelable parcelable, LinearLayoutManager linearLayoutManager) {
      Bundle bundle = (Bundle) parcelable;
      if (linearLayoutManager != null && linearLayoutManager.getItemCount() > 0) {
         int position = linearLayoutManager.findFirstVisibleItemPosition();
         View item = linearLayoutManager.findViewByPosition(position);
         bundle.putInt(STATE_SCROLL_ITEMS_COUNT, linearLayoutManager.getItemCount());
         bundle.putInt(STATE_SCROLL_POSITION, position);
         if (item != null) bundle.putInt(STATE_SCROLL_OFFSET, item.getTop());
      }
      return bundle;
   }

   public void restoreInstanceStateIfNeeded(Parcelable parcelable, LinearLayoutManager linearLayoutManager) {
      if (parcelable == null || linearLayoutManager == null) {
         return;
      }
      Bundle bundle = (Bundle) parcelable;
      int pos = bundle.getInt(STATE_SCROLL_POSITION);
      int offset = bundle.getInt(STATE_SCROLL_OFFSET);
      int itemsCount = bundle.getInt(STATE_SCROLL_ITEMS_COUNT);
      if (itemsCount == linearLayoutManager.getItemCount()) linearLayoutManager.scrollToPositionWithOffset(pos, offset);
   }
}