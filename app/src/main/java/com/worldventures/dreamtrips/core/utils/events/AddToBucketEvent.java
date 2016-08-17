package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class AddToBucketEvent {

   protected TripModel trip;

   public AddToBucketEvent(TripModel trip) {
      this.trip = trip;
   }

   public TripModel getTrip() {
      return trip;
   }

   public void setTrip(TripModel trip) {
      this.trip = trip;
   }
}
