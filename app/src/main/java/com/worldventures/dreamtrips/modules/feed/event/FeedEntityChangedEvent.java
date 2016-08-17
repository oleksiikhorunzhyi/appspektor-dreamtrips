package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class FeedEntityChangedEvent {

   FeedEntity entity;

   public FeedEntityChangedEvent(FeedEntity entity) {
      this.entity = entity;
   }

   public FeedEntity getFeedEntity() {
      return entity;
   }
}
