package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class CommentIconClickedEvent {

   FeedItem feedItem;

   public CommentIconClickedEvent(FeedItem feedItem) {
      this.feedItem = feedItem;
   }

   public FeedItem getFeedItem() {
      return feedItem;
   }
}
