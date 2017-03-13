package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public interface FeedEntityHolder {

   void updateFeedEntity(FeedEntity updatedFeedEntity);

   void deleteFeedEntity(FeedEntity deletedFeedEntity);
}
