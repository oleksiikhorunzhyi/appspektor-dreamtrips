package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.core.model.Trip;

/**
 * Created by 1 on 13.02.15.
 */
public class TripLikedEvent {

    Trip trip;

    public TripLikedEvent(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
