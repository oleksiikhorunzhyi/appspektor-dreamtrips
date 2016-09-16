package com.worldventures.dreamtrips.modules.trips.storage;

import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedCombinedStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

public class TripsStorage extends PaginatedCombinedStorage<TripModel> implements ActionStorage<List<TripModel>> {

   public TripsStorage(PaginatedMemoryStorage<TripModel> memoryStorage, PaginatedDiskStorage<TripModel> diskStorage) {
      super(memoryStorage, diskStorage);
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetTripsCommand.class;
   }
}

