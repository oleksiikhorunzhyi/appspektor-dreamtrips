package com.worldventures.dreamtrips.modules.feed.storage.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.storage.command.AccountTimelineStorageCommand;

import java.util.List;

public class AccountTimelineStorage extends MemoryStorage<List<FeedItem>> implements ActionStorage<List<FeedItem>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return AccountTimelineStorageCommand.class;
   }
}
