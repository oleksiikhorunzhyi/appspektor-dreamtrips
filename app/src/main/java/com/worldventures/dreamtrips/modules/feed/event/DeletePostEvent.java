package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class DeletePostEvent {

   public FeedEntity feedEntity;

   public DeletePostEvent(FeedEntity feedItem) {
      this.feedEntity = feedItem;
   }

   public FeedEntity getEntity() {
      return feedEntity;
   }
}
