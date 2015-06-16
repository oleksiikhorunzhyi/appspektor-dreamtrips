package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import javax.inject.Inject;

public class BaseTripPresenter<V extends BaseTripPresenter.View> extends Presenter<V> {

    @Inject
    protected Activity activity;
    @Inject
    protected SnappyRepository db;
    protected TripModel trip;

    BucketHelper bucketHelper;

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        bucketHelper = new BucketHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void dropView() {
        super.dropView();
        activity = null;
    }

    protected void initData() {
        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
        view.setDuration(trip.getDuration());
        String reward = trip.getRewardsLimit(appSessionHolder.get().get().getUser());

        if (!TextUtils.isEmpty(reward) && !"0".equals(reward)) {
            view.setRedemption(String.valueOf(reward));
        } else {
            view.setPointsInvisible();
        }

        if (trip.isFeatured()) {
            view.setFeatured();
        }
        // like and inBucket takes place when menu is loaded
    }

    public void addTripToBucket() {
        doRequest(new AddBucketItemCommand(new BucketBasePostItem("trip", trip.getTripId())), bucketItem -> {
            String type = bucketItem.getType();
            List<BucketItem> bucketItems = db.readBucketList(type);
            bucketItems.add(0, bucketItem);
            db.saveBucketList(bucketItems, type);
            trip.setInBucketList(true);
            view.setInBucket(true);
            onSuccessTripAction();
            bucketHelper.notifyItemAddedToBucket(activity, bucketItem);
        });
    }

    public void likeTrip() {
        toggleTripLike();

        DreamTripsRequest<JsonObject> request = trip.isLiked() ?
                new LikeTripCommand(trip.getLikeId()) :
                new UnlikeTripCommand(trip.getLikeId());

        doRequest(request, object -> onSuccessTripAction(), (error) -> {
            toggleTripLike();
            handleError(error);
        });
    }

    private void toggleTripLike() {
        trip.setLiked(!trip.isLiked());
        view.setLike(trip.isLiked());
    }

    private void onSuccessTripAction() {
        db.saveTrip(trip);
    }

    public interface View extends Presenter.View {
        void setName(String text);

        void setLocation(String text);

        void setPrice(String text);

        void setDates(String text);

        void setDesription(String text);

        void setDuration(int count);

        void setRedemption(String count);

        void setLike(boolean like);

        void setInBucket(boolean inBucket);

        void setPointsInvisible();

        void setFeatured();
    }

}
