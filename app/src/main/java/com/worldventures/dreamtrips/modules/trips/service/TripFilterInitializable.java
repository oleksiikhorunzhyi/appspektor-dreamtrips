package com.worldventures.dreamtrips.modules.trips.service;

import com.worldventures.dreamtrips.modules.dtl.service.Initializable;

public class TripFilterInitializable implements Initializable {

   private final TripsInteractor tripsInteractor;

   public TripFilterInitializable(TripsInteractor tripsInteractor) {
      this.tripsInteractor = tripsInteractor;
   }

   @Override
   public void init() {
      tripsInteractor.initFilters();
   }
}
