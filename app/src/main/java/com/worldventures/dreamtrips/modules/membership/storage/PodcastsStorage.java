package com.worldventures.dreamtrips.modules.membership.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.membership.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.List;

public class PodcastsStorage implements ActionStorage<List<Podcast>> {

   private final SnappyRepository snappyRepository;
   private final MemoryStorage<List<Podcast>> memoryStorage;

   public PodcastsStorage(SnappyRepository snappyRepository, MemoryStorage<List<Podcast>> memoryStorage) {
      this.snappyRepository = snappyRepository;
      this.memoryStorage = memoryStorage;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<Podcast> data) {
      memoryStorage.save(params, data);
      snappyRepository.savePodcasts(data);
   }

   @Override
   public List<Podcast> get(@Nullable CacheBundle bundle) {
      if (memoryStorage.get(bundle) != null) {
         return memoryStorage.get(bundle);
      } else {
         return snappyRepository.getPodcasts();
      }
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetPodcastsCommand.class;
   }
}
