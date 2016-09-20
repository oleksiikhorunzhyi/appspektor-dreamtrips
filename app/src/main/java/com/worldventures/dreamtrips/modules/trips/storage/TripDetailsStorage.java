package com.worldventures.dreamtrips.modules.trips.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class TripDetailsStorage implements ActionStorage<TripModel> {

   public static final String UID = "UID";

   private final SnappyRepository snappyRepository;

   public TripDetailsStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetTripDetailsCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, TripModel data) {
      snappyRepository.saveTripDetails(data);
   }

   @Override
   public TripModel get(@Nullable CacheBundle action) {
      return snappyRepository.getTripDetail(action.get(UID));
   }
}
