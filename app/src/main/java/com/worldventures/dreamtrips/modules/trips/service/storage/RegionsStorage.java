package com.worldventures.dreamtrips.modules.trips.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.List;

public class RegionsStorage implements ActionStorage<List<RegionModel>> {

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
}
