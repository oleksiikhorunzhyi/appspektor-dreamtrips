package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.AddToBucketEvent;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.LikeTripPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsQuery;
import com.worldventures.dreamtrips.modules.trips.event.TripItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class TripListPresenter extends BaseTripsPresenter<TripListPresenter.View> {

    @Inject
    Activity activity;
    @Inject
    Prefs prefs;
    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    FeedEntityManager entityManager;
    private boolean loadFromApi;
    private boolean loadWithStatus;

    private DreamSpiceAdapterController<TripModel> adapterController;
    private SweetDialogHelper sweetDialogHelper;

    public TripListPresenter() {
        sweetDialogHelper = new SweetDialogHelper();
    }

    @Override
    public void onInjected() {
        adapterController = new DreamSpiceAdapterController<TripModel>() {
            @Override
            public SpiceRequest<ArrayList<TripModel>> getReloadRequest() {
                boolean makeApiCall = loadFromApi || cacheEmpty() || resetCacheOnStart();
                return new GetTripsQuery(db, prefs, makeApiCall);
            }

            @Override
            public void onStart(LoadType loadType) {
                super.onStart(loadType);
                if (loadWithStatus || cacheEmpty()) {
                    view.startLoading();
                }
            }

            @Override
            protected void onRefresh(ArrayList<TripModel> tripModels) {
                super.onRefresh(performFiltering(tripModels));
                cachedTrips.clear();
                cachedTrips.addAll(tripModels);
            }

            @Override
            public void onFinish(LoadType type, List<TripModel> items, SpiceException spiceException) {
                if (view == null) return;
                loadFromApi = false;
                view.finishLoading();
                if (spiceException != null) {
                    handleError(spiceException);
                } else if (shouldUpdate()) {
                    loadWithStatus = false;
                    loadFromApi = true;
                    reload();
                }
            }

            private Boolean cacheEmpty() {
                return db.isEmpty(SnappyRepository.TRIP_KEY);
            }

            private boolean shouldUpdate() {
                long current = Calendar.getInstance().getTimeInMillis();
                return current - prefs.getLong(Prefs.LAST_SYNC) > DreamTripsRequest.DELTA_TRIP;
            }

            /** one-time fix to deal with deploy issue */
            private static final long RESET_POINT = 1443501000;

            private boolean resetCacheOnStart() {
                return prefs.getLong(Prefs.LAST_SYNC) / 1000 < RESET_POINT;
            }
        };
        adapterController.setSpiceManager(dreamSpiceManager);
        entityManager.setDreamSpiceManager(dreamSpiceManager);
    }

    public void takeView(View view) {
        super.takeView(view);
        adapterController.setAdapter(view.getAdapter());
        TrackingHelper.dreamTrips(getAccountUserId());
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);
        loadWithStatus = true;
        loadFromApi = false;
        adapterController.reload();
    }

    @Override
    public void dropView() {
        adapterController.setAdapter(null);
        super.dropView();
    }

    public void loadFromApi() {
        loadFromApi = true;
        loadWithStatus = true;
        adapterController.reload();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Filter
    ///////////////////////////////////////////////////////////////////////////

    public void onEvent(FilterBusEvent event) {
        if (!cachedTrips.isEmpty()) {
            setFilters(event);
            view.setFilteredItems(performFiltering(cachedTrips));
        }
    }

    @Override
    public void resetFilters() {
        super.resetFilters();
        view.clearSearch();
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
        TripModel trip = Queryable.from(cachedTrips).firstOrDefault(element -> element.getUid().equals(event.getFeedEntity().getUid()));
        if (trip != null) {
            trip.syncLikeState(event.getFeedEntity());
            onSuccess(trip);
            view.dataSetChanged();
            if (view.isVisibleOnScreen()) {
                sweetDialogHelper.notifyTripLiked(activity, trip);
            }
        }
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
                onSuccess(trip);
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

    private void onSuccess(TripModel trip) {
        db.saveTrip(trip);
    }

    private void onFailure() {
        view.dataSetChanged();
        view.showErrorMessage();
    }

    public void actionMap() {
        fragmentCompass.replace(Route.MAP);
    }

    public interface View extends Presenter.View {
        void dataSetChanged();

        void showErrorMessage();

        void startLoading();

        void finishLoading();

        void clearSearch();

        void setFilteredItems(List<TripModel> items);

        IRoboSpiceAdapter<TripModel> getAdapter();
    }

}