package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.common.BucketUtility;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;

@Layout(R.layout.adapter_item_entity_details)
public class BucketFeedEntityDetailsCell extends FeedEntityDetailsCell<BucketFeedItem> {

   public BucketFeedEntityDetailsCell(View view) {
      super(view);
   }

   @Override
   protected void onMore() {
      showMoreDialog(R.menu.menu_feed_entity_edit, R.string.bucket_delete, R.string.bucket_delete_caption);
   }

   @Override
   protected void onDelete() {
      super.onDelete();
      getEventBus().post(new DeleteBucketEvent(getModelObject().getItem()));
   }

   @Override
   protected void onEdit() {
      super.onEdit();

      BucketItem bucketItem = getModelObject().getItem();
      getEventBus().post(new EditBucketEvent(bucketItem, BucketUtility.typeFromItem(bucketItem)));
   }
}
