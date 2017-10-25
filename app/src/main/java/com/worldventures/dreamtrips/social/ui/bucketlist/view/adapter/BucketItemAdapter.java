package com.worldventures.dreamtrips.social.ui.bucketlist.view.adapter;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

public class BucketItemAdapter extends DraggableArrayListAdapter<BucketItem> {

   public BucketItemAdapter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public long getItemId(int position) {
      return getItem(position).getUid().hashCode();
   }
}
