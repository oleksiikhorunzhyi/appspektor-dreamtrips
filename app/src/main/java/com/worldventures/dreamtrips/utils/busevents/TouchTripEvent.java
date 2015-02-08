package com.worldventures.dreamtrips.utils.busevents;

import com.worldventures.dreamtrips.core.model.Trip;

public class TouchTripEvent {
    Trip trip;

    public TouchTripEvent(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
