package com.worldventures.dreamtrips.modules.feed.storage.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.storage.storage.UserTimelineStorage;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UserTimelineStorageCommand extends FeedItemsStorageBaseCommand {

   private int userId;

   public UserTimelineStorageCommand(int userId, ListStorageOperation<FeedItem> operation) {
      super(operation);
      this.userId = userId;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheOptions cacheOptions = super.getCacheOptions();
      cacheOptions.params().put(UserTimelineStorage.BUNDLE_KEY_VALUE, String.valueOf(userId));
      return cacheOptions;
   }
}
