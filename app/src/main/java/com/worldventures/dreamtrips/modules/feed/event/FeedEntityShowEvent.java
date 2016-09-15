package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedEntityShowEvent {

   public final FeedItem feedItem;

   public FeedEntityShowEvent(FeedItem feedItem) {
      this.feedItem = feedItem;
   }
}
