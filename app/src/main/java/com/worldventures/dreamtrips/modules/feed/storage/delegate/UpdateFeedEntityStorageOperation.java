package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.List;

public class UpdateFeedEntityStorageOperation implements ListStorageOperation<FeedItem<FeedEntity>> {

   private FeedEntity feedEntity;

   public UpdateFeedEntityStorageOperation(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   @Override
   public List<FeedItem<FeedEntity>> perform(List<FeedItem<FeedEntity>> items) {
      FeedItem<FeedEntity> itemToUpdate = Queryable.from(items)
            .firstOrDefault(item -> item.getItem().getUid().equals(feedEntity.getUid()));

      if (itemToUpdate != null) itemToUpdate.setItem(feedEntity);

      return items;
   }
}
