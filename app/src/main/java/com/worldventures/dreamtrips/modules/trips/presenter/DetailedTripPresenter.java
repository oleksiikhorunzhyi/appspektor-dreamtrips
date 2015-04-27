package com.worldventures.dreamtrips.modules.trips.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
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
            view.setInBucket(trip.isInBucketList());
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

    public void actionAddToBucket() {
        doRequest(new AddBucketItemCommand(new BucketBasePostItem("trip", trip.getTripId())), bucketItem -> {
            String type = bucketItem.getType();
            List<BucketItem> bucketItems = db.readBucketList(type);
            bucketItems.add(0, bucketItem);
            db.saveBucketList(bucketItems, type);
            trip.setInBucketList(true);
            view.setInBucket(true);
            view.informUser("Added to Bucket list"); // TODO show a better info
        });
    }

    public interface View extends BaseTripPresenter.View {
        void setContent(List<ContentItem> contentItems);
        void hideBookIt();
        void setInBucket(boolean inBucket);
    }
}
