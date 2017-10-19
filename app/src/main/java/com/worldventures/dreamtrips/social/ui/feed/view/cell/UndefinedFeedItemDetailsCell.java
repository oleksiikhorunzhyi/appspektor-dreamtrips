package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.FeedItemDetailsCell;

@Layout(R.layout.adapter_item_feed_undefined_event)
public class UndefinedFeedItemDetailsCell extends FeedItemDetailsCell<TripFeedItem, BaseFeedCell.FeedCellDelegate<TripFeedItem>> {

   public UndefinedFeedItemDetailsCell(View view) {
      super(view);
   }

   @Override
   public void prepareForReuse() {

   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
   }

   @Override
   protected void onDelete() {

   }

   @Override
   protected void onEdit() {

   }

   @Override
   protected void onMore() {

   }
}
