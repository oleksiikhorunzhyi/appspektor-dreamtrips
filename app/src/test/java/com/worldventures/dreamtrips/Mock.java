package com.worldventures.dreamtrips;

import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.List;

public class Mock {
    //For feature
    public static List<TripModel> getTrips() {
        List<TripModel> result = new ArrayList<>();
        result.add(getTrip());
        result.add(getTrip());
        return result;
    }

    public static TripModel getTrip() {
        TripModel trip = new TripModel();
        trip.setName("Europe");
        trip.setDescription("Dream trip");
        return trip;
    }
}
