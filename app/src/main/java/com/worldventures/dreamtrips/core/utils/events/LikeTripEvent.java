package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.trips.model.Trip;

/**
 *  Edward on 20.01.15.
 * event when some trip was liked
 */
public class LikeTripEvent {

    Trip trip;

    public LikeTripEvent(Trip trip) {
        this.trip = trip;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
