package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.modules.trips.api.GetTripDetailsQuery;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.Trip;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by Edward on 19.01.15.
 * presentation model for DetailedTripFragment
 */
public class DetailedTripPresenter extends BasePresenter<DetailedTripPresenter.View> {

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

    public DetailedTripPresenter(View view) {
        super(view);
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        images = trip.getImages();
        filteredImages = new ArrayList<>();
        filteredImages.addAll(trip.getFilteredImages());

    }

    public void onCreate() {
        AdobeTrackingHelper.trip(String.valueOf(trip.getId()), getUserId());

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
        AdobeTrackingHelper.bookIt(String.valueOf(trip.getId()), getUserId());
        activityRouter.openBookItActivity(trip.getId());
    }

    public void loadTripDetails() {

        RequestListener<TripDetails> callback = new RequestListener<TripDetails>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.showErrorMessage();
                view.setContent(null);
            }

            @Override
            public void onRequestSuccess(TripDetails tripDetails) {
                view.setContent(tripDetails.getContent());
                AdobeTrackingHelper.tripInfo(String.valueOf(trip.getId()), getUserId());
            }
        };
        dreamSpiceManager.execute(new GetTripDetailsQuery(trip.getId()), callback);
    }

    public void actionLike() {

        trip.setLiked(!trip.isLiked());
        view.setLike(trip.isLiked());

        RequestListener<JsonObject> callback2 = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                trip.setLiked(!trip.isLiked());
                view.showErrorMessage();
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                Bundle bundle = new Bundle();
                eventBus.post(new TripLikedEvent(trip));
                db.saveTrip(trip);
            }
        };
        if (trip.isLiked()) {
            dreamSpiceManager.execute(new LikeTripCommand(trip.getId()), callback2);
        } else {
            dreamSpiceManager.execute(new UnlikeTripCommand(trip.getId()), callback2);
        }
    }

    public void onItemClick(int position) {
        if (filteredImages.get(position) instanceof TripImage) {
            this.activityRouter.openFullScreenTrip(this.filteredImages, position);
        }
    }

    public static interface View extends BasePresenter.View {
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
