package com.worldventures.dreamtrips.modules.feed.storage.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedStorageCommand;

import java.util.List;

public class FeedStorage extends MemoryStorage<List<FeedItem>> implements ActionStorage<List<FeedItem>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FeedStorageCommand.class;
   }
}
