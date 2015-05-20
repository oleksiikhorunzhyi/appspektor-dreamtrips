package com.worldventures.dreamtrips.modules.trips.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.trips.api.GetTripDetailsQuery;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.List;

public class DetailedTripPresenter extends BaseTripPresenter<DetailedTripPresenter.View> {

    private List<Object> filteredImages;

    public void setTrip(TripModel trip) {
        super.setTrip(trip);
        filteredImages = new ArrayList<>();
        filteredImages.addAll(trip.getFilteredImages());
        TrackingHelper.trip(String.valueOf(trip.getTripId()), getUserId());
        loadTripDetails();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!appSessionHolder.get().get().getUser().isPlatinum() && trip.isPlatinum()) {
            view.hideBookIt();
        }
    }

    public List<Object> getFilteredImages() {
        return filteredImages;
    }

    public void actionBookIt() {
        TrackingHelper.bookIt(String.valueOf(trip.getTripId()), getUserId());
        activityRouter.openBookItActivity(trip.getTripId());
    }

    public void menuLoaded() {
        if (trip != null) {
            view.setLike(trip.isLiked());
        }
    }

    public void loadTripDetails() {
        doRequest(new GetTripDetailsQuery(trip.getTripId()), this::onSuccess);
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.setContent(null);
    }

    private void onSuccess(TripDetails tripDetails) {
        view.setContent(tripDetails.getContent());
        TrackingHelper.tripInfo(String.valueOf(trip.getTripId()), getUserId());
    }

    public void onItemClick(int position) {
        if (filteredImages.get(position) instanceof TripImage) {
            this.activityRouter.openFullScreenTrip(this.filteredImages, position);
        }
    }

    public static interface View extends BaseTripPresenter.View {
        void setContent(List<ContentItem> contentItems);

        void hideBookIt();
    }
}
