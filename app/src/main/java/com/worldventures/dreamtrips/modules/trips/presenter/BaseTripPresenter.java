package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class BaseTripPresenter<V extends BaseTripPresenter.View> extends Presenter<V> {
    @Inject
    Activity activity;

    @Inject
    SnappyRepository db;

    @Inject
    BucketInteractor bucketInteractor;

    @Inject
    FeedEntityManager feedEntityManager;

    protected TripModel trip;

    private SweetDialogHelper sweetDialogHelper;

    private CompositeSubscription subscriptions;

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
        initData();
    }

    @Override
    public void takeView(V view) {
        super.takeView(view);

        subscriptions = new CompositeSubscription();
    }

    @Override
    public void dropView() {
        super.dropView();

        subscriptions.unsubscribe();
    }

    private void initData() {
        view.setup(trip);
    }

    public void addTripToBucket() {
        subscriptions.add(bucketInteractor.createPipe()
                .createObservableResult(new CreateBucketItemHttpAction(ImmutableBucketBodyImpl.builder()
                        .type("trip")
                        .id(trip.getTripId())
                        .build()))
                .map(CreateBucketItemHttpAction::getResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bucketItem -> {
                    trip.setInBucketList(true);
                    view.setup(trip);

                    sweetDialogHelper.notifyItemAddedToBucket(activity, bucketItem);
                }, this::handleError));
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

    public interface View extends Presenter.View {
        void setup(TripModel tripModel);
    }
}