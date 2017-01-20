package com.worldventures.dreamtrips.modules.feed.storage.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.FifoKeyValueStorage;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.storage.command.HashtagFeedStorageCommand;
import com.worldventures.dreamtrips.modules.feed.storage.command.UserTimelineStorageCommand;

import java.util.List;

public class HashtagFeedStorage extends FifoKeyValueStorage<String, List<FeedItem>> implements ActionStorage<List<FeedItem>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return HashtagFeedStorageCommand.class;
   }
}
