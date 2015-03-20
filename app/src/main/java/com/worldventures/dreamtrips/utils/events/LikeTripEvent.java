package com.worldventures.dreamtrips.utils.events;

import com.worldventures.dreamtrips.core.model.Trip;

/**
 * Created by Edward on 20.01.15.
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
