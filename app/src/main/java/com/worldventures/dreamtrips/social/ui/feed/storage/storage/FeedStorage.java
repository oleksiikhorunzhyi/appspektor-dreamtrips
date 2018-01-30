package com.worldventures.dreamtrips.social.ui.feed.storage.storage;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.FeedStorageCommand;

import java.util.List;

public class FeedStorage extends MemoryStorage<List<FeedItem>> implements ActionStorage<List<FeedItem>> {

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return FeedStorageCommand.class;
   }
}
