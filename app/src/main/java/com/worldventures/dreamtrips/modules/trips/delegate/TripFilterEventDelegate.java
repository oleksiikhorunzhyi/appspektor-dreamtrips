package com.worldventures.dreamtrips.modules.trips.delegate;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.delegate.EventDelegate;
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters;
import com.worldventures.dreamtrips.util.TripsFilterData;

import rx.Observable;

public class TripFilterEventDelegate extends EventDelegate<TripsFilterData> {

   private SnappyRepository snappyRepository;

   private TripsFilterData tripsFilterData;

   public TripFilterEventDelegate(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public void post(TripsFilterData tripsFilterData) {
      super.post(tripsFilterData);
      this.tripsFilterData = tripsFilterData;
   }

   public Observable<TripsFilterData> last() {
      if (tripsFilterData != null) return Observable.just(tripsFilterData);
      tripsFilterData = new TripsFilterData();
      CachedTripFilters cachedTripFilters = snappyRepository.getTripFilters();
      if (cachedTripFilters != null) {
         tripsFilterData.setAllRegions(cachedTripFilters.getRegions());
         tripsFilterData.setAllParentActivities(cachedTripFilters.getActivities());
      }
      return Observable.just(tripsFilterData);
   }
}
