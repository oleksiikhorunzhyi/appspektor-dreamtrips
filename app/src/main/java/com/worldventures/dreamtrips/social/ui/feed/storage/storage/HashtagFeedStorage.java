package com.worldventures.dreamtrips.social.ui.feed.storage.storage;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.FifoKeyValueStorage;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.HashtagFeedStorageCommand;

import java.util.List;

public class HashtagFeedStorage extends FifoKeyValueStorage<String, List<FeedItem>> implements ActionStorage<List<FeedItem>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return HashtagFeedStorageCommand.class;
   }
}
