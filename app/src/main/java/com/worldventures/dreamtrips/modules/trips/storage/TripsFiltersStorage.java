package com.worldventures.dreamtrips.modules.trips.storage;

import android.support.annotation.Nullable;

import com.worldventures.janet.cache.CacheBundle;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.command.TripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters;

public class TripsFiltersStorage implements ActionStorage<CachedTripFilters> {

   private final SnappyRepository snappyRepository;

   public TripsFiltersStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void save(@Nullable CacheBundle params, CachedTripFilters data) {
      snappyRepository.saveTripFilters(data);
   }

   @Override
   public CachedTripFilters get(@Nullable CacheBundle action) {
      return snappyRepository.getTripFilters();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return TripsFilterDataCommand.class;
   }
}
