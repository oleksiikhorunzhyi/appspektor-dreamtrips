package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

public class BaseTripPresenter<V extends BaseTripPresenter.View> extends Presenter<V> {

    @Inject
    Activity activity;
    @Inject
    SnappyRepository db;
    @Inject
    BucketItemManager bucketItemManager;

    protected TripModel trip;

    SweetDialogHelper sweetDialogHelper;

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        sweetDialogHelper = new SweetDialogHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);
        initData();
    }


    protected void initData() {
        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
        view.setDuration(trip.getDuration());

        if (trip.isSoldOut())
            view.setSoldOut();

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
        bucketItemManager.addBucketItemFromTrip(trip.getTripId(), bucketItem -> {
            trip.setInBucketList(true);
            view.setInBucket(true);
            onSuccessTripAction();
            sweetDialogHelper.notifyItemAddedToBucket(activity, bucketItem);
        }, this);
    }

    public void likeTrip() {
        toggleTripLike();

        DreamTripsRequest<Void> request = trip.isLiked() ?
                new LikeEntityCommand(trip.getUid()) :
                new UnlikeEntityCommand(trip.getUid());

        doRequest(request, object -> {
            sweetDialogHelper.notifyTripLiked(activity, trip);
            onSuccessTripAction();
        }, (error) -> {
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

        void setSoldOut();
    }

}
