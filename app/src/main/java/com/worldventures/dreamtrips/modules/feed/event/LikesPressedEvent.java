package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class LikesPressedEvent {

   FeedEntity model;

   public LikesPressedEvent(FeedEntity model) {
      this.model = model;
   }

   public FeedEntity getModel() {
      return model;
   }
}
