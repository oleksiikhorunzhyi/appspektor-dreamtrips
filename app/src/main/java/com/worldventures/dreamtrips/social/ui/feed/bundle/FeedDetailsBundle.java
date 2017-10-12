package com.worldventures.dreamtrips.social.ui.feed.bundle;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;

public interface FeedDetailsBundle extends CommentableBundle {

   FeedItem getFeedItem();

   boolean isSlave();

   boolean shouldShowAdditionalInfo();
}
