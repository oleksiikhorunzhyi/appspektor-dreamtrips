package com.worldventures.dreamtrips;

import com.worldventures.dreamtrips.modules.trips.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class Mock {
    //For feature
    public static List<Trip> getTrips() {
        List<Trip> result = new ArrayList<>();
        result.add(getTrip());
        result.add(getTrip());
        return result;
    }

    public static Trip getTrip() {
        Trip trip = new Trip();
        trip.setName("Europe");
        trip.setDescription("Dream trip");
        return trip;
    }
}
