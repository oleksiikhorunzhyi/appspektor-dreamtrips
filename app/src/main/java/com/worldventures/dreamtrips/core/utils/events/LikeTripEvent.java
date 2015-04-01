package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

public class LikeTripEvent {

    protected TripModel trip;

    public LikeTripEvent(TripModel trip) {
        this.trip = trip;
    }

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
    }
}
