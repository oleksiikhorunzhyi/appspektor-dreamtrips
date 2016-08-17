package com.messenger.util;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

public class ScrollStatePersister {

   private static final String STATE_SCROLL_POSITION = "STATE_SCROLL_POSITION";
   private static final String STATE_SCROLL_OFFSET = "STATE_SCROLL_OFFSET";

   public Parcelable saveScrollState(Parcelable parcelable, LinearLayoutManager linearLayoutManager) {
      Bundle bundle = (Bundle) parcelable;
      if (linearLayoutManager != null && linearLayoutManager.getItemCount() > 0) {
         int position = linearLayoutManager.findFirstVisibleItemPosition();
         bundle.putInt(STATE_SCROLL_POSITION, position);
         View item = linearLayoutManager.findViewByPosition(position);
         if (item != null) bundle.putInt(STATE_SCROLL_OFFSET, item.getTop());
      }
      return bundle;
   }

   public void restoreInstanceState(Parcelable parcelable, LinearLayoutManager linearLayoutManager) {
      if (parcelable == null) {
         return;
      }
      Bundle bundle = (Bundle) parcelable;
      int pos = bundle.getInt(STATE_SCROLL_POSITION);
      int offset = bundle.getInt(STATE_SCROLL_OFFSET);
      linearLayoutManager.scrollToPositionWithOffset(pos, offset);
   }
}
