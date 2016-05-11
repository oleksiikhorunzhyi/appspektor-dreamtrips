package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

public class BaseTripPresenter<V extends BaseTripPresenter.View> extends Presenter<V> {

    @Inject
    Activity activity;
    @Inject
    SnappyRepository db;
    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    FeedEntityManager feedEntityManager;

    protected TripModel trip;
    protected SweetDialogHelper sweetDialogHelper;

    public BaseTripPresenter(TripModel trip) {
        this.trip = trip;
    }

    public TripModel getTrip() {
        return trip;
    }

    public void onInjected() {
        super.onInjected();
        sweetDialogHelper = new SweetDialogHelper();
        feedEntityManager.setRequestingPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);
        initData();
    }

    protected void initData() {
        view.setup(trip);
    }

    public void addTripToBucket() {
        bucketItemManager.addBucketItemFromTrip(trip.getTripId(), bucketItem -> {
            trip.setInBucketList(true);
            view.setup(trip);
            sweetDialogHelper.notifyItemAddedToBucket(activity, bucketItem);
        }, this);
    }

    public void likeTrip() {
        if (!trip.isLiked()) {
            feedEntityManager.like(trip);
        } else {
            feedEntityManager.unlike(trip);
        }
    }

    public void onEvent(EntityLikedEvent event) {
        if (event.getFeedEntity().getUid().equals(trip.getUid())) {
            trip.syncLikeState(event.getFeedEntity());
            view.setup(trip);
            if (view.isVisibleOnScreen()) {
                sweetDialogHelper.notifyTripLiked(activity, trip);
            }
        }
    }

    private void toggleTripLike() {
    }

    public interface View extends Presenter.View {
        void setup(TripModel tripModel);
    }

}
