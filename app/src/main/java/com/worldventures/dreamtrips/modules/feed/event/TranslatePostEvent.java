package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;

public class TranslatePostEvent {

   private PostFeedItem postFeedItem;

   public TranslatePostEvent(PostFeedItem postFeedItem) {
      this.postFeedItem = postFeedItem;
   }

   public PostFeedItem getPostFeedItem() {
      return postFeedItem;
   }
}
