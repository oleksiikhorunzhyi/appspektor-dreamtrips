package com.worldventures.dreamtrips.social.ui.feed.storage.command;

import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.storage.storage.UserTimelineStorage;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UserTimelineStorageCommand extends FeedItemsStorageBaseCommand {

   private final int userId;

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
