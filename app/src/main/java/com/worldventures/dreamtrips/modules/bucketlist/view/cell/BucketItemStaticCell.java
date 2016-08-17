package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_item_bucket_cell)
public class BucketItemStaticCell extends BucketItemCell {

   public BucketItemStaticCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      swipeLayout.setSwipeEnabled(false);
   }
}
