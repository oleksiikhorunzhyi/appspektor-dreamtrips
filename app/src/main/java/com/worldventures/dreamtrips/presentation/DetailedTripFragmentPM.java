package com.worldventures.dreamtrips.presentation;

import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.ContentItem;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.TripImage;

import org.json.JSONObject;
import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.List;

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

    private Trip trip;
    private List<Object> filteredImages;
    private List<TripImage> images;

    public DetailedTripFragmentPM(View view) {
        super(view);
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        images = trip.getImages();
        filteredImages = new ArrayList<>();
        filteredImages.addAll(Collections2.filter(images, (input) ->
                input.getType().equals("RETINA")));

    }

    public void onCreate() {
        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
        view.setDuration(trip.getDuration());
        view.setRedemption(String.valueOf(trip.getRewardsLimit()));
        loadTripDetails();
    }

    public List<Object> getFilteredImages() {
        return filteredImages;
    }

    public void actionBookIt() {
        activityRouter.openBookItActivity(trip.getId());
    }

    public void loadTripDetails() {
        Callback<TripDetails> callback = new Callback<TripDetails>() {
            @Override
            public void success(TripDetails tripDetails, Response response) {
                view.setContent(tripDetails.getContent());
            }

            @Override
            public void failure(RetrofitError error) {
                view.showErrorMessage();
            }
        };
        dreamTripsApi.getDetails(trip.getId(), callback);
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

    public void onItemClick(int position) {
        if (filteredImages.get(position) instanceof TripImage) {
            this.activityRouter.openFullScreenTrip(this.filteredImages, position);
        }
    }

    public static interface View extends BasePresentation.View {
        void setContent(List<ContentItem> contentItems);
        void setName(String text);
        void setLocation(String text);
        void setPrice(String text);
        void setDates(String text);
        void setDesription(String text);
        void setDuration(int count);
        void showErrorMessage();
        void setRedemption(String count);
    }
}
