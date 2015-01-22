package com.worldventures.dreamtrips.presentation;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Trip;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        view.loadPhoto("");
        view.setDuration(trip.getDuration());
    }

    public void actionLike() {
        final Callback<JsonObject> callback = new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                trip.setLiked(!trip.isLiked());
                view.showErrorMessage();
            }
        };

        if (trip.isLiked()) {
            dreamTripsApi.likeTrip(trip.getId(), callback);
        } else {
            dreamTripsApi.unlikeTrio(trip.getId(), callback);
        }
    }


    public static interface View extends BasePresentation.View {

        void setName(String text);
        void setLocation(String text);
        void setPrice(String text);
        void setDates(String text);
        void setDesription(String text);
        void loadPhoto(String url);
        void setDuration(int count);
        void showErrorMessage();
    }
}
