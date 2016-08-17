package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class EntityLikedEvent {

   FeedEntity feedEntity;


   public EntityLikedEvent(FeedEntity feedEntity) {
      this.feedEntity = feedEntity;
   }

   public FeedEntity getFeedEntity() {
      return feedEntity;
   }
}
