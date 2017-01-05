package com.worldventures.dreamtrips.modules.membership.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedCombinedStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.modules.membership.service.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.List;

public class PodcastsStorage extends PaginatedCombinedStorage<Podcast> implements ActionStorage<List<Podcast>> {

   public PodcastsStorage(PaginatedMemoryStorage<Podcast> memoryStorage, PaginatedDiskStorage<Podcast> diskStorage) {
      super(memoryStorage, diskStorage);
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetPodcastsCommand.class;
   }
}
