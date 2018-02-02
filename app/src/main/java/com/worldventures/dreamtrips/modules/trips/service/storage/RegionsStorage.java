package com.worldventures.dreamtrips.modules.trips.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.ClearableStorage;
import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.List;

public class RegionsStorage implements ActionStorage<List<RegionModel>>, ClearableStorage {

   private final MemoryStorage<List<RegionModel>> memoryStorage;

   public RegionsStorage(MemoryStorage<List<RegionModel>> memoryStorage) {
      this.memoryStorage = memoryStorage;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetRegionsCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<RegionModel> data) {
      memoryStorage.save(params, data);
   }

   @Override
   public List<RegionModel> get(@Nullable CacheBundle action) {
      return memoryStorage.get(action);
   }

   @Override
   public void clearMemory() {
      memoryStorage.clearMemory();
   }
}
