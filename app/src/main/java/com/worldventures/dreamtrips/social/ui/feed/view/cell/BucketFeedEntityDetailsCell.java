package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.common.BucketUtility;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;

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
   protected void onEdit() {
      super.onEdit();
      BucketItem bucketItem = getModelObject().getItem();
      cellDelegate.onEditBucketItem(bucketItem, BucketUtility.typeFromItem(bucketItem));
   }

   @Override
   protected void onDelete() {
      super.onDelete();
      cellDelegate.onDeleteBucketItem(getModelObject().getItem());
   }
}
