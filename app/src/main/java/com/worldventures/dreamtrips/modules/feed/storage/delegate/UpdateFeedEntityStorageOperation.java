package com.worldventures.dreamtrips.modules.feed.storage.delegate;

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
      for (int i = 0; i < items.size(); i++) {
         FeedItem<FeedEntity> oldFeedItem = items.get(i);
         if (oldFeedItem.getItem().getUid().equals(feedEntity.getUid())) {
            FeedItem feedItem = FeedItem.create(feedEntity);
            feedItem.setAction(oldFeedItem.getAction());
            feedItem.setCreatedAt(oldFeedItem.getCreatedAt());
            feedItem.setLinks(oldFeedItem.getLinks());
            feedItem.setMetaData(oldFeedItem.getMetaData());
            feedItem.setType(oldFeedItem.getType());
            feedItem.setReadAt(oldFeedItem.getReadAt());
            feedItem.setItem(feedEntity);
            items.set(i, feedItem);
         }
      }
      return items;
   }
}
