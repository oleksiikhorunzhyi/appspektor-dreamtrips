package com.worldventures.dreamtrips.social.ui.membership.storage;

import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.storage.ActionStorage;
import com.worldventures.core.janet.cache.storage.CombinedListStorage;
import com.worldventures.core.janet.cache.storage.Storage;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand;

import java.util.List;

public class PodcastsStorage extends CombinedListStorage<Podcast> implements ActionStorage<List<Podcast>> {

   public PodcastsStorage(Storage<List<Podcast>> memoryStorage, Storage<List<Podcast>> diskStorage) {
      super(memoryStorage, diskStorage);
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetPodcastsCommand.class;
   }
}
