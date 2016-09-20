package com.worldventures.dreamtrips.modules.trips.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

public class TripsByUidsStorage implements ActionStorage<List<TripModel>> {

   public static final String UIDS = "UIDS";

   private final SnappyRepository snappyRepository;

   public TripsByUidsStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetTripsByUidCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<TripModel> data) {
      snappyRepository.saveTripsDetails(data);
   }

   @Override
   public List<TripModel> get(@Nullable CacheBundle action) {
      return snappyRepository.getTripsDetailsForUids(action.get(UIDS));
   }
}
