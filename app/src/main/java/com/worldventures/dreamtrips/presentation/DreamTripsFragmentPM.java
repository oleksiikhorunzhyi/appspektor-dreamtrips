package com.worldventures.dreamtrips.presentation;

import android.content.Context;

import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.DateFilterItem;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.busevents.FilterBusEvent;
import com.worldventures.dreamtrips.utils.busevents.RequestFilterDataEvent;
import com.worldventures.dreamtrips.utils.busevents.TripLikedEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by Edward on 19.01.15.
 * presentation model for fragment with list of the trips
 */
public class DreamTripsFragmentPM extends BasePresentation<DreamTripsFragmentPM.View> {

    @Inject
    Prefs prefs;

    @Inject
    @Global
    EventBus eventBus;

    @Inject
    SnappyRepository db;

    private boolean loadFromApi;

    /**
     * filters
     */
    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private boolean showSoldOut;
    private DateFilterItem dateFilterItem = new DateFilterItem();
    private List<Integer> acceptedRegions;
    private List<Activity> acceptedThemes;

    private RoboSpiceAdapterController<Trip> adapterController = new RoboSpiceAdapterController<Trip>() {
        @Override
        public SpiceRequest<ArrayList<Trip>> getRefreshRequest() {
            return new DreamTripsRequest.GetTripsRequest(db, prefs, loadFromApi) {
                @Override
                public ArrayList<Trip> loadDataFromNetwork() throws Exception {
                    return performFiltering(super.loadDataFromNetwork());
                }
            };
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<Trip> items, SpiceException spiceException) {
            loadFromApi = false;
            view.finishLoading(items);
        }
    };


    public DreamTripsFragmentPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        eventBus.registerSticky(this);
       // onEvent(eventBus.getStickyEvent(FilterBusEvent.class));
    }

    @Override
    public void resume() {
        if (view.getAdapter().getCount() == 0) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
    }

    public void onPause() {
        eventBus.unregister(this);
    }

    public void reload() {
        loadFromApi = true;
        adapterController.reload();
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
            if (event.isReset()) {
                resetFilters();
            } else {
                maxPrice = event.getMaxPrice();
                minNights = event.getMinNights();
                minPrice = event.getMinPrice();
                maxNights = event.getMaxNights();
                dateFilterItem = event.getDateFilterItem();
                acceptedRegions = event.getAcceptedRegions();
                acceptedThemes = event.getAcceptedActivities();
                showSoldOut = event.isShowSoldOut();
            }
            adapterController.reload();
        }
    }


    private ArrayList<Trip> performFiltering(List<Trip> data) {
        ArrayList<Trip> filteredTrips = new ArrayList<>();
        filteredTrips.addAll(Collections2.filter(data, (input) ->
                input.getPrice().getAmount() <= maxPrice
                        && input.getAvailabilityDates().check(dateFilterItem)
                        && input.getPrice().getAmount() >= minPrice
                        && input.getDuration() >= minNights
                        && input.getDuration() <= maxNights
                        && (showSoldOut || input.isAvailable())
                        && (acceptedThemes == null || !Collections.disjoint(acceptedThemes, input.getActivities()))
                        && (acceptedRegions == null || acceptedRegions.contains(input.getRegion().getId()))));
        return filteredTrips;
    }

    public void resetFilters() {
        this.maxNights = Integer.MAX_VALUE;
        this.maxPrice = Double.MAX_VALUE;
        this.minPrice = 0;
        this.minNights = 0;
        this.acceptedRegions = null;
        this.acceptedThemes = null;
        dateFilterItem.reset();
    }

    public void onItemLike(Trip trip) {
        RequestListener<JsonObject> callback = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                trip.setLiked(!trip.isLiked());
                view.dataSetChanged();
                view.showErrorMessage();
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                db.saveTrip(trip);
            }
        };
        if (trip.isLiked()) {
            dreamSpiceManager.execute(new DreamTripsRequest.LikeTrip(trip.getId()), callback);
        } else {
            dreamSpiceManager.execute(new DreamTripsRequest.UnlikeTrip(trip.getId()), callback);
        }
    }

    public void actionSearch(String query) {
    }

    public void actionMap() {
        fragmentCompass.replace(State.MAP, null);
    }

    public void onItemClick(Trip trip) {
        activityRouter.openTripDetails(trip);
    }


    public static interface View extends BasePresentation.View {
        void dataSetChanged();

        void showErrorMessage();

        void startLoading();

        void finishLoading(List<Trip> items);

        IRoboSpiceAdapter<Trip> getAdapter();
    }

}