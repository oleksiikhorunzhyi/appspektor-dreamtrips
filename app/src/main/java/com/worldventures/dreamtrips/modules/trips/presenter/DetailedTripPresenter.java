package com.worldventures.dreamtrips.modules.trips.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
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

    public DetailedTripPresenter(View view) {
        super(view);
    }

    public void setTrip(TripModel trip) {
        super.setTrip(trip);
        filteredImages = new ArrayList<>();
        filteredImages.addAll(trip.getFilteredImages());
        TrackingHelper.trip(String.valueOf(trip.getId()), getUserId());
        loadTripDetails();
    }

    @Override
    public void resume() {
        super.resume();

        if (!appSessionHolder.get().get().getUser().isPlatinum() && trip.isPlatinum()) {
            view.hideBookIt();
        }
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
                view.informUser(R.string.smth_went_wrong);
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
