package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import android.content.Context;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;

public class IgnoreFirstItemAdapter extends BaseDelegateAdapter {

   public IgnoreFirstItemAdapter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void clear() {
      Object item = null;
      if (!items.isEmpty()) {
         item = getItem(0);
      }
      super.clear();
      if (item != null) {
         addItem(item);
      }
   }
}
