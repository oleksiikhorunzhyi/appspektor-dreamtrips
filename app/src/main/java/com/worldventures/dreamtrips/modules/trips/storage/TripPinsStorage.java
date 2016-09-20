package com.worldventures.dreamtrips.modules.trips.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;
import com.worldventures.dreamtrips.modules.trips.model.Pin;

import java.util.List;

public class TripPinsStorage implements ActionStorage<List<Pin>> {

   private final SnappyRepository snappyRepository;

   public TripPinsStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetTripsLocationsCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<Pin> data) {
      snappyRepository.savePins(data);
   }

   @Override
   public List<Pin> get(@Nullable CacheBundle action) {
      return snappyRepository.getPins();
   }
}
