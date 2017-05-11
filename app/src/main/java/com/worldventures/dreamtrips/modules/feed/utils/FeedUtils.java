package com.worldventures.dreamtrips.modules.feed.utils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.ArrayList;

public class FeedUtils {

   public static void updateFeedItemInList(ArrayList<FeedItem> feedItems, FeedEntity updatedFeedEntity) {
      Queryable.from(feedItems).forEachR(item -> {
         if (item.getItem() != null && item.getItem().equals(updatedFeedEntity)) {
            if (updatedFeedEntity.getOwner() == null) {
               updatedFeedEntity.setOwner(item.getItem().getOwner());
            }
            item.setItem(updatedFeedEntity);
         }
      });
   }
}
