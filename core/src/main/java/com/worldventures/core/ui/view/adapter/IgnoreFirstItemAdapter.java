package com.worldventures.core.ui.view.adapter;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.view.custom.horizontal_photo_view.model.AddPhotoModel;

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
      if (item instanceof AddPhotoModel) {
         addItem(item);
      }
   }
}
