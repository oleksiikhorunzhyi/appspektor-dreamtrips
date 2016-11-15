package com.worldventures.dreamtrips.modules.trips.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.ClearableStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import java.util.List;

public class ActivitiesStorage implements ActionStorage<List<ActivityModel>>, ClearableStorage {

   private final MemoryStorage<List<ActivityModel>> memoryStorage;

   public ActivitiesStorage(MemoryStorage<List<ActivityModel>> memoryStorage) {
      this.memoryStorage = memoryStorage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetActivitiesCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<ActivityModel> data) {
      memoryStorage.save(params, data);
   }

   @Override
   public List<ActivityModel> get(@Nullable CacheBundle action) {
      return memoryStorage.get(action);
   }

   @Override
   public void clearMemory() {
      memoryStorage.clearMemory();
   }
}
