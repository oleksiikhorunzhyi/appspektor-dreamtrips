package com.worldventures.dreamtrips.social.ui.feed.storage.command;

import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.storage.storage.HashtagFeedStorage;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class HashtagFeedStorageCommand extends FeedItemsStorageBaseCommand {

   private final String hashtag;

   public HashtagFeedStorageCommand(String hashtag, ListStorageOperation<FeedItem> operation) {
      super(operation);
      this.hashtag = hashtag;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheOptions cacheOptions = super.getCacheOptions();
      cacheOptions.params().put(HashtagFeedStorage.BUNDLE_KEY_VALUE, hashtag);
      return cacheOptions;
   }
}
