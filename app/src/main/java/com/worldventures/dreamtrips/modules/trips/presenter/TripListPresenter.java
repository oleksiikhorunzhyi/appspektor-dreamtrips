package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.events.AddToBucketEvent;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.LikeTripPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.modules.trips.event.TripItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.trips.manager.TripFilterDataProvider;
import com.worldventures.dreamtrips.modules.trips.model.ImmutableTripQueryData;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.TripQueryData;
import com.worldventures.dreamtrips.modules.trips.model.TripsFilterDataAnalyticsWrapper;
import com.worldventures.dreamtrips.util.TripsFilterData;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class TripListPresenter extends Presenter<TripListPresenter.View> {

    public static final int PER_PAGE = 20;

    @Inject Activity activity;
    @Inject FeedEntityManager entityManager;
    @Inject BucketInteractor bucketInteractor;
    @Inject TripFilterDataProvider tripFilterDataProvider;
    @Inject TripsInteractor tripsInteractor;

    @State String query;

    private boolean loadWithStatus;

    private SweetDialogHelper sweetDialogHelper;

    private boolean loading;
    private int page = 1;
    private boolean noMoreItems = false;

    public TripListPresenter() {
        sweetDialogHelper = new SweetDialogHelper();
    }

    @Override
    public void onInjected() {
        entityManager.setRequestingPresenter(this);
    }

    public void takeView(View view) {
        super.takeView(view);
        TrackingHelper.dreamTrips(getAccountUserId());
        subscribeToReloadTrips();
        subscribeToLoadNextTripsPage();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadWithStatus = true;
        reload();
    }

    public void reload() {
        if(view == null) return;

        page = 1;
        noMoreItems = false;
        if (loadWithStatus) {
            view.startLoading();
        }

        tripsInteractor.reloadTripsActionPipe()
                .send(new GetTripsCommand.ReloadTripsCommand(createGetTripsQuery()));
    }

    public void loadMore() {
        page++;
        tripsInteractor.loadNextTripsActionPipe()
                .send(new GetTripsCommand.LoadNextTripsCommand(createGetTripsQuery()));
    }

    private void subscribeToReloadTrips() {
        tripsInteractor.reloadTripsActionPipe()
                .observe()
                .compose(bindView())
                .compose(new IoToMainComposer<>())
                .subscribe(new ActionStateSubscriber<GetTripsCommand.ReloadTripsCommand>().onSuccess(getTripsCommand -> {
                    loading = false;
                    if (view != null) {
                        view.finishLoading();
                        view.getAdapter().clear();
                        view.getAdapter().addItems(getTripsCommand.getResult());
                        view.getAdapter().notifyDataSetChanged();
                    }
                }).onFail((getTripsCommand, throwable) -> {
                    view.finishLoading();
                    view.informUser(getTripsCommand.getErrorMessage());
                }));
    }

    private void subscribeToLoadNextTripsPage() {
        tripsInteractor.loadNextTripsActionPipe()
                .observe()
                .compose(bindView())
                .compose(new IoToMainComposer<>())
                .subscribe(new ActionStateSubscriber<GetTripsCommand.LoadNextTripsCommand>().onSuccess(getTripsCommand -> {
                    loading = false;
                    view.getAdapter().addItems(getTripsCommand.getResult());
                    view.getAdapter().notifyDataSetChanged();
                    noMoreItems = getTripsCommand.getResult().isEmpty();
                }).onFail((getTripsCommand, throwable) -> {
                    view.finishLoading();
                    view.informUser(getTripsCommand.getErrorMessage());
                }));
    }

    protected TripQueryData createGetTripsQuery() {
        TripsFilterData tripsFilterData = tripFilterDataProvider.get();
        ImmutableTripQueryData.Builder queryBuilder = ImmutableTripQueryData.builder()
                .page(page)
                .perPage(PER_PAGE)
                .query(query);
        if (tripsFilterData != null) {
            queryBuilder.durationMin(tripsFilterData.getMinNights())
                    .durationMax(tripsFilterData.getMaxNights())
                    .priceMin(tripsFilterData.getMinPrice())
                    .priceMax(tripsFilterData.getMaxPrice())
                    .startDate(tripsFilterData.getStartDateFormatted())
                    .endDate(tripsFilterData.getEndDateFormatted())
                    .regions(tripsFilterData.getAcceptedRegionsIds())
                    .activities(tripsFilterData.getAcceptedActivitiesIds())
                    .isSoldOut(tripsFilterData.isShowSoldOut())
                    .isRecent(tripsFilterData.isShowRecentlyAdded())
                    .isLiked(tripsFilterData.isShowFavorites());
        }
        return queryBuilder.build();
    }

    @Override
    public void handleError(SpiceException error) {
        view.finishLoading();
        super.handleError(error);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Filter
    ///////////////////////////////////////////////////////////////////////////

    public void onEvent(FilterBusEvent event) {
        reload();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Like
    ///////////////////////////////////////////////////////////////////////////

    public void onEvent(LikeTripPressedEvent event) {
        if (!event.getTrip().isLiked()) {
            entityManager.like(event.getTrip());
        } else {
            entityManager.unlike(event.getTrip());
        }
    }

    public void onEvent(EntityLikedEvent event) {
        view.itemLiked(event.getFeedEntity());
    }

    ///////////////////////////////////////////////////////////////////////////
    // New bucket item
    ///////////////////////////////////////////////////////////////////////////

    public void onEvent(AddToBucketEvent event) {
        onAddToBucket(event.getTrip());
    }

    public void onAddToBucket(TripModel trip) {
        if (trip.isInBucketList()) {
            view.bind(bucketInteractor.createPipe()
                    .createObservableResult(new CreateBucketItemHttpAction(ImmutableBucketBodyImpl.builder()
                            .type("trip")
                            .id(trip.getTripId())
                            .build()))
                    .map(CreateBucketItemHttpAction::getResponse)
                    .observeOn(AndroidSchedulers.mainThread()))
                    .subscribe(bucketItem -> {
                        sweetDialogHelper.notifyItemAddedToBucket(activity, bucketItem);
                    }, throwable -> {
                        trip.setInBucketList(!trip.isInBucketList());
                        handleError(throwable);
                    });
        } else {
            trip.setInBucketList(!trip.isInBucketList());
            onFailure();
        }
    }

    public void onEvent(TripItemAnalyticEvent event) {
        if (event.getActionAttribute().equals(TrackingHelper.ATTRIBUTE_VIEW)) {
            TrackingHelper.viewTripDetails(event.getTripId(), event.getTripName(),
                    view.isSearchOpened() ? query : "", new TripsFilterDataAnalyticsWrapper(tripFilterDataProvider.get()));
        } else {
            TrackingHelper.actionItemDreamtrips(event.getActionAttribute(), event.getTripId(), event.getTripName());
        }
    }

    private void onFailure() {
        view.dataSetChanged();
        view.showErrorMessage();
    }

    public void search(String s) {
        query = s;
        reload();
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL)) {
            if (!loading && !noMoreItems && lastVisible == totalItemCount - 1) {
                loading = true;
                loadMore();
            }
        }
    }

    public String getQuery() {
        return query;
    }

    public interface View extends RxView {

        void dataSetChanged();

        void showErrorMessage();

        void startLoading();

        void finishLoading();

        IRoboSpiceAdapter<TripModel> getAdapter();

        void itemLiked(FeedEntity feedEntity);

        boolean isSearchOpened();
    }
}