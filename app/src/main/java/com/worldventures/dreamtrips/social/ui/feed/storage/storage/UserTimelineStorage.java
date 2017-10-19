package com.worldventures.dreamtrips.social.ui.feed.storage.storage;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.FifoKeyValueStorage;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.UserTimelineStorageCommand;

import java.util.List;

public class UserTimelineStorage extends FifoKeyValueStorage<String, List<FeedItem>> implements ActionStorage<List<FeedItem>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return UserTimelineStorageCommand.class;
   }
}
