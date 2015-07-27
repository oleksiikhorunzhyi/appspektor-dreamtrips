package com.worldventures.dreamtrips.modules.trips.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.trips.api.GetTripDetailsQuery;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.List;

public class TripDetailsPresenter extends BaseTripPresenter<TripDetailsPresenter.View> {

    private List<Object> filteredImages;

    public void setTrip(TripModel trip) {
        super.setTrip(trip);
        filteredImages = new ArrayList<>();
        filteredImages.addAll(trip.getFilteredImages());
        TrackingHelper.trip(String.valueOf(trip.getTripId()), getAccountUserId());
        loadTripDetails();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (trip.isSoldOut() || (!appSessionHolder.get().get().getUser().isPlatinum()
                && trip.isPlatinum())) {
            view.hideBookIt();
        }
    }

    public List<Object> getFilteredImages() {
        return filteredImages;
    }

    public void actionBookIt() {
        TrackingHelper.bookIt(String.valueOf(trip.getTripId()), getAccountUserId());
        activityRouter.openBookItActivity(trip.getTripId());
    }

    @Override
    public void onMenuPrepared() {
        if (view != null && trip != null) {
            view.setLike(trip.isLiked());
            view.setInBucket(trip.isInBucketList());
        }
    }

    public void loadTripDetails() {
        doRequest(new GetTripDetailsQuery(trip.getTripId()), tripDetails -> {
            view.setContent(tripDetails.getContent());
            TrackingHelper.tripInfo(String.valueOf(trip.getTripId()), getAccountUserId());
        });
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.setContent(null);
    }

    public void onItemClick(int position) {
        if (filteredImages.get(position) instanceof TripImage) {
            this.activityRouter.openFullScreenTrip(this.filteredImages, position);
        }
    }

    public interface View extends BaseTripPresenter.View {
        void setContent(List<ContentItem> contentItems);
        void hideBookIt();
    }
}
