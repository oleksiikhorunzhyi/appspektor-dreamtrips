package com.worldventures.dreamtrips.modules.trips.presenter;

import android.app.Activity;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.core.utils.events.AddToBucketEvent;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.LikeTripEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.GetTripsQuery;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType.LOCATIONS;

public class TripListPresenter extends BaseTripsPresenter<TripListPresenter.View> {

    @Inject
    Activity activity;
    @Inject
    protected Prefs prefs;

    private boolean loadFromApi;
    private boolean loadWithStatus;

    private DreamSpiceAdapterController<TripModel> adapterController;
    private BucketHelper bucketHelper;

    public TripListPresenter() {
        bucketHelper = new BucketHelper();
    }

    @Override
    public void onInjected() {
        adapterController = new DreamSpiceAdapterController<TripModel>() {
            @Override
            public SpiceRequest<ArrayList<TripModel>> getReloadRequest() {
                return new GetTripsQuery(db, prefs, loadFromApi || cacheEmpty());
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
        };
        adapterController.setSpiceManager(dreamSpiceManager);
    }

    public void takeView(View view) {
        super.takeView(view);
        loadWithStatus = true;
        adapterController.setAdapter(view.getAdapter());
        adapterController.reload();
        TrackingHelper.dreamTrips(getUserId());
    }

    public void loadFromApi() {
        loadFromApi = true;
        loadWithStatus = true;
        adapterController.reload();
    }

    @Override
    public void dropView() {
        adapterController.setAdapter(null);
        super.dropView();
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

    public void onEvent(LikeTripEvent event) {
        onItemLike(event.getTrip());
    }

    public void onItemLike(TripModel trip) {
        DreamTripsRequest<JsonObject> request = trip.isLiked() ?
                new LikeTripCommand(trip.getLikeId()) :
                new UnlikeTripCommand(trip.getLikeId());

        doRequest(request, (object) -> onSuccess(trip), (spiceException) -> {
            trip.setLiked(!trip.isLiked());
            onFailure(trip);
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // New bucket item
    ///////////////////////////////////////////////////////////////////////////

    public void onEvent(AddToBucketEvent event) {
        onAddToBucket(event.getTrip());
    }

    public void onAddToBucket(TripModel trip) {
        DreamTripsRequest<BucketItem> request;
        if (trip.isInBucketList()) {
            request = new AddBucketItemCommand(new BucketBasePostItem("trip", trip.getTripId()));
            doRequest(request, item -> {
                onSuccess(trip);
                bucketHelper.saveBucketItem(db, item, LOCATIONS.name(), true);
                bucketHelper.notifyItemAddedToBucket(activity, item);

            }, (spiceException) -> {
                trip.setInBucketList(!trip.isInBucketList());
                onFailure(trip);
            });
        } else {
            trip.setInBucketList(!trip.isInBucketList());
            onFailure(trip);
        }
    }

    private void onSuccess(TripModel trip) {
        db.saveTrip(trip);
    }

    private void onFailure(TripModel trip) {
        view.dataSetChanged();
        view.showErrorMessage();
    }

    public void actionMap() {
        fragmentCompass.replace(Route.MAP);
    }

    public void onItemClick(TripModel trip) {
        activityRouter.openTripDetails(trip);
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