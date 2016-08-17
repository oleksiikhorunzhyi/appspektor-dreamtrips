package com.worldventures.dreamtrips.modules.feed.bundle;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public interface FeedDetailsBundle extends CommentableBundle {

    FeedItem getFeedItem();

    boolean isSlave();

    boolean shouldShowAdditionalInfo();
}
