package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class FeedEntityCommentedEvent {

   FeedEntity entity;

   public FeedEntityCommentedEvent(FeedEntity entity) {
      this.entity = entity;
   }

   public FeedEntity getFeedEntity() {
      return entity;
   }
}
