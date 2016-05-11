package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.events.AddToBucketEvent;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.LikeTripPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsQuery;
import com.worldventures.dreamtrips.modules.trips.event.TripItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.TripQueryData;
import com.worldventures.dreamtrips.util.TripsFilterData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class TripListPresenter extends Presenter<TripListPresenter.View> {

    public static final int PER_PAGE = 20;
    @Inject
    Activity activity;
    @Inject
    Prefs prefs;
    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    FeedEntityManager entityManager;
    @Inject
    SnappyRepository db;
    @State
    String query;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);
        loadWithStatus = true;
        reload();
    }

    public void reload() {
        page = 1;
        noMoreItems = false;
        if (loadWithStatus) {
            view.startLoading();
        }
        //
        doRequest(createGetTripsQuery(), items -> {
            loading = false;
            //
            view.finishLoading();
            //
            view.getAdapter().clear();
            view.getAdapter().addItems(items);
            view.getAdapter().notifyDataSetChanged();
        });
    }

    public void loadMore() {
        page++;
        doRequest(createGetTripsQuery(), items -> {
            loading = false;
            view.getAdapter().addItems(items);
            view.getAdapter().notifyDataSetChanged();
            noMoreItems = items.isEmpty();
        });
    }

    protected GetTripsQuery createGetTripsQuery() {
        FilterBusEvent filterBusEvent = eventBus.getStickyEvent(FilterBusEvent.class);
        TripsFilterData tripsFilterData;
        if (filterBusEvent == null || filterBusEvent.getTripsFilterData() == null) {
            tripsFilterData = TripsFilterData.createDefault(db);
        } else {
            tripsFilterData = filterBusEvent.getTripsFilterData();
        }
        TripQueryData b = new TripQueryData();
        b.setPage(page);
        b.setPerPage(PER_PAGE);
        b.setQuery(query);
        if (tripsFilterData != null) {
            b.setDurationMin(tripsFilterData.getMinNights());
            b.setDurationMax(tripsFilterData.getMaxNights());
            b.setPriceMin(tripsFilterData.getMinPrice());
            b.setPriceMax(tripsFilterData.getMaxPrice());
            b.setStartDate(tripsFilterData.getStartDate());
            b.setEndDate(tripsFilterData.getEndDate());
            b.setRegions(getAcceptedRegions(tripsFilterData));
            b.setActivities(getAcceptedActivities(tripsFilterData));
            b.setSoldOut(tripsFilterData.isShowSoldOut());
            b.setRecent(tripsFilterData.isShowRecentlyAdded());
            b.setLiked(tripsFilterData.isShowFavorites());
        }
        return new GetTripsQuery(b);
    }

    private List<Integer> getAcceptedRegions(TripsFilterData tripsFilterData) {
        return Queryable.from(tripsFilterData.getAcceptedRegions())
                .map(BaseEntity::getId).toList();
    }

    private ArrayList<Integer> getAcceptedActivities(TripsFilterData tripsFilterData) {
        return new ArrayList<>(Queryable.from(tripsFilterData.getAcceptedActivities())
                .map(BaseEntity::getId)
                .toList());
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
            bucketItemManager.addBucketItemFromTrip(trip.getTripId(), bucketItem -> {
                sweetDialogHelper.notifyItemAddedToBucket(activity, bucketItem);
            }, spiceException -> {
                trip.setInBucketList(!trip.isInBucketList());
                handleError(spiceException);
            });
        } else {
            trip.setInBucketList(!trip.isInBucketList());
            onFailure();
        }
    }

    public void onEvent(TripItemAnalyticEvent event) {
        TrackingHelper.actionItemDreamtrips(event.getActionAttribute(), event.getTripId());
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

    public interface View extends Presenter.View {

        void dataSetChanged();

        void showErrorMessage();

        void startLoading();

        void finishLoading();

        IRoboSpiceAdapter<TripModel> getAdapter();

        void itemLiked(FeedEntity feedEntity);
    }

}