package com.worldventures.dreamtrips.modules.trips.storage;

import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedDiskStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import rx.functions.Action1;
import rx.functions.Func0;

public class TripsDiskStorage extends PaginatedDiskStorage<TripModel> {
   private SnappyRepository db;

   public TripsDiskStorage(SnappyRepository db) {
      this.db = db;
   }

   @Override
   public Func0<List<TripModel>> getRestoreAction() {
      return db::getTrips;
   }

   @Override
   public Action1<List<TripModel>> getSaveAction() {
      return db::saveTrips;
   }
}
