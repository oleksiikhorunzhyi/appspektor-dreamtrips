package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedItemAddedEvent {
   FeedItem<FeedEntity> feedItem;

   public FeedItemAddedEvent(FeedItem<FeedEntity> feedItem) {
      this.feedItem = feedItem;
   }

   public FeedItem<FeedEntity> getFeedItem() {
      return feedItem;
   }
}
