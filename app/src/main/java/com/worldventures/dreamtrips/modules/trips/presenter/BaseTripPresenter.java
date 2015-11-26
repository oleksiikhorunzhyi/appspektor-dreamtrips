package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
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
            eventBus.post(new EntityLikedEvent(trip.getUid(), trip.isLiked()));
            onSuccessTripAction();
        }, (error) -> {
            toggleTripLike();
            handleError(error);
        });
    }

    private void toggleTripLike() {
        trip.setLiked(!trip.isLiked());
        view.setup(trip);
    }

    private void onSuccessTripAction() {
        db.saveTrip(trip);
    }

    public interface View extends Presenter.View {
        void setup(TripModel tripModel);
    }

}
