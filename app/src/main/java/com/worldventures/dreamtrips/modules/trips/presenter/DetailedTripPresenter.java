package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.GetTripDetailsQuery;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DetailedTripPresenter extends Presenter<DetailedTripPresenter.View> {

    @Inject
    protected SnappyRepository db;

    private TripModel trip;
    private List<Object> filteredImages;

    public DetailedTripPresenter(View view) {
        super(view);
    }

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
        filteredImages = new ArrayList<>();
        filteredImages.addAll(trip.getFilteredImages());
    }

    public void onCreate() {
        TrackingHelper.trip(String.valueOf(trip.getId()), getUserId());

        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
        view.setDuration(trip.getDuration());

        if (trip.getRewardsLimit() > 0) {
            view.setRedemption(String.valueOf(trip.getRewardsLimit()));
        } else {
            view.setPointsInvisible();
        }

        if (trip.isFeatured()) {
            view.setFeatured();
        }

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
        TrackingHelper.bookIt(String.valueOf(trip.getId()), getUserId());
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
                TrackingHelper.tripInfo(String.valueOf(trip.getId()), getUserId());
            }
        };

        dreamSpiceManager.execute(new GetTripDetailsQuery(trip.getId()), callback);
    }

    public void actionLike() {

        trip.setLiked(!trip.isLiked());
        view.setLike(trip.isLiked());

        RequestListener<JsonObject> callback = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                trip.setLiked(!trip.isLiked());
                view.showErrorMessage();
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                eventBus.post(new TripLikedEvent(trip));
                db.saveTrip(trip);
            }
        };

        if (trip.isLiked()) {
            dreamSpiceManager.execute(new LikeTripCommand(trip.getId()), callback);
        } else {
            dreamSpiceManager.execute(new UnlikeTripCommand(trip.getId()), callback);
        }
    }

    public void onItemClick(int position) {
        if (filteredImages.get(position) instanceof TripImage) {
            this.activityRouter.openFullScreenTrip(this.filteredImages, position);
        }
    }

    public static interface View extends Presenter.View {
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

        void setFeatured();
    }
}
