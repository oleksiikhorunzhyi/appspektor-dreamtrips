package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;

public interface FeedEntityHolder {

   void updateFeedEntity(FeedEntity updatedFeedEntity);

   void deleteFeedEntity(FeedEntity deletedFeedEntity);
}
