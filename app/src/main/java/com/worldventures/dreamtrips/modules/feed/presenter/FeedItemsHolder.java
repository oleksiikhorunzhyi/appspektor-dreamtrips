package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public interface FeedItemsHolder {

   void addFeedItem(FeedItem feedItem);

   void updateFeedEntity(FeedEntity updatedFeedEntity);

   void deleteFeedEntity(String uid);

   void refreshFeedItems();
}
