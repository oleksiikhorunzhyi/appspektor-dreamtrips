package com.worldventures.dreamtrips.modules.trips.storage;

import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.janet.cache.storage.CombinedListStorage;
import com.worldventures.janet.cache.storage.Storage;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

public class TripsStorage extends CombinedListStorage<TripModel> implements ActionStorage<List<TripModel>> {

   public TripsStorage(Storage<List<TripModel>> memoryStorage, Storage<List<TripModel>> diskStorage) {
      super(memoryStorage, diskStorage);
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetTripsCommand.class;
   }
}

