package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

/**
 *  1 on 13.02.15.
 */
public class TripLikedEvent {

    TripModel trip;

    public TripLikedEvent(TripModel trip) {
        this.trip = trip;
    }

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
    }
}
