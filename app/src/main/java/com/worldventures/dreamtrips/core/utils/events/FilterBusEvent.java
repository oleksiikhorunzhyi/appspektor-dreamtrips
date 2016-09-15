package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.util.TripsFilterData;


public class FilterBusEvent {
   TripsFilterData tripsFilterData;

   public FilterBusEvent(TripsFilterData tripsFilterData) {
      this.tripsFilterData = tripsFilterData;
   }

   public TripsFilterData getTripsFilterData() {
      return tripsFilterData;
   }
}
