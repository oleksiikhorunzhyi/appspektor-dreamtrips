package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Trip;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

/**
 * Created by Edward on 19.01.15.
 * presentation model for DetailedTripFragment
 */
@PresentationModel
public class DetailedTripFragmentPM extends BasePresentation<DetailedTripFragmentPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    Trip trip;

    public DetailedTripFragmentPM(View view) {
        super(view);
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void onCreate() {
        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
    }


    public static interface View extends BasePresentation.View {

        void setName(String text);
        void setLocation(String text);
        void setPrice(String text);
        void setDates(String text);
        void setDesription(String text);
        void loadPhoto(String url);
    }
}
