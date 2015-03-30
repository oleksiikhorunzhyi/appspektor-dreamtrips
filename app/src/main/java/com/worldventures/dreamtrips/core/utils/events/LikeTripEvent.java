package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

/**
 *  Edward on 20.01.15.
 * event when some trip was liked
 */
public class LikeTripEvent {

    TripModel trip;

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
