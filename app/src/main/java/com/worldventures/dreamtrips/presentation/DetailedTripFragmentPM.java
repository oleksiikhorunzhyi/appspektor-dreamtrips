package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;
import com.snappydb.DB;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.ContentItem;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.TripImage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.service.TripsIntentService;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.SnappyUtils;
import com.worldventures.dreamtrips.utils.busevents.TripLikedEvent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Edward on 19.01.15.
 * presentation model for DetailedTripFragment
 */
public class DetailedTripFragmentPM extends BasePresentation<DetailedTripFragmentPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;

    @Global
    @Inject
    EventBus eventBus;

    @Inject
    SnappyRepository db;

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
        AdobeTrackingHelper.trip(String.valueOf(trip.getId()));

        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
        view.setFeatured(trip.isFeatured());
        view.setDuration(trip.getDuration());
        if (trip.getRewardsLimit() > 0)
            view.setRedemption(String.valueOf(trip.getRewardsLimit()));
        else
            view.setPointsInvisible();
        view.setLike(trip.isLiked());
        loadTripDetails();
    }

    public void menuPrepared() {
        view.setLike(trip.isLiked());
    }

    public List<Object> getFilteredImages() {
        return filteredImages;
    }

    public void actionBookIt() {
        AdobeTrackingHelper.bookIt(String.valueOf(trip.getId()));
        activityRouter.openBookItActivity(trip.getId());
    }

    public void loadTripDetails() {
        Callback<TripDetails> callback = new Callback<TripDetails>() {
            @Override
            public void success(TripDetails tripDetails, Response response) {
                view.setContent(tripDetails.getContent());
                AdobeTrackingHelper.tripInfo(String.valueOf(trip.getId()));
            }

            @Override
            public void failure(RetrofitError error) {
                view.showErrorMessage();
                view.setContent(null);
            }
        };
        dreamTripsApi.getDetails(trip.getId(), callback);
    }

    public void actionLike() {
        final Callback<JsonObject> callback = new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(TripsIntentService.TRIP_EXTRA, trip);
                eventBus.post(new TripLikedEvent(trip));
                db.saveTrip(trip);
            }

            @Override
            public void failure(RetrofitError error) {
                trip.setLiked(!trip.isLiked());
                view.showErrorMessage();
            }
        };

        trip.setLiked(!trip.isLiked());
        view.setLike(trip.isLiked());

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

        void setLike(boolean like);

        void setPointsInvisible();

        void setFeatured(boolean featured);
    }
}
