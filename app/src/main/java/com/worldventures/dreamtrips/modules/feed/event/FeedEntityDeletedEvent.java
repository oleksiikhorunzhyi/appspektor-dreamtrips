package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;

public class FeedEntityDeletedEvent {

   FeedEntity eventModel;

   public FeedEntityDeletedEvent(FeedEntity eventModel) {
      this.eventModel = eventModel;
   }

   public FeedEntity getEventModel() {
      return eventModel;
   }
}
