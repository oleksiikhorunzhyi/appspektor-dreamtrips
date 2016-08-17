package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class DeletePhotoEvent {

   public Photo feedItem;

   public DeletePhotoEvent(Photo feedItem) {
      this.feedItem = feedItem;
   }

   public Photo getEntity() {
      return feedItem;
   }
}
