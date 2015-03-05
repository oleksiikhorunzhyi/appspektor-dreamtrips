package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;

import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.DateFilterItem;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.FileUtils;
import com.worldventures.dreamtrips.utils.busevents.FilterBusEvent;
import com.worldventures.dreamtrips.utils.busevents.RequestFilterDataEvent;
import com.worldventures.dreamtrips.utils.busevents.TripLikedEvent;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.fragment.MapFragment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by Edward on 19.01.15.
 * presentation model for fragment with list of the trips
 */
public class DreamTripsFragmentPM extends BasePresentation<DreamTripsFragmentPM.View> {

    private static final long DELTA = 30 * 60 * 1000;

    @Inject
    DreamTripsApi dreamTripsApi;


    @Inject
    Prefs prefs;

    @Inject
    Context context;

    @Inject
    @Global
    EventBus eventBus;

    @Inject
    SnappyRepository db;

    @Inject
    LoaderFactory loaderFactory;
    private CollectionController<Trip> tripsController;

    private boolean loadFromApi;

    private ArrayList<Trip> data = new ArrayList<>();

    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private boolean showSoldOut;
    private DateFilterItem dateFilterItem = new DateFilterItem();
    private List<Integer> acceptedRegions;
    private List<Activity> acceptedThemes;

    public DreamTripsFragmentPM(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();

        this.tripsController = loaderFactory.create(0, (context, params) -> {
            try {
                if (needUpdate() || loadFromApi) {
                    this.loadFromApi = false;
                    this.data.clear();
                    this.data.addAll(this.loadTrips());
                    db.saveTrips(this.data);
                    prefs.put(Prefs.LAST_SYNC, Calendar.getInstance().getTimeInMillis());
                } else {
                    this.data.clear();
                        this.data.addAll(db.getTrips());

                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<Trip> filteredData = performFiltering(data);

            return filteredData;
        });

        eventBus.registerSticky(this);
        onEvent(eventBus.getStickyEvent(FilterBusEvent.class));
    }

    public void onPause() {
        eventBus.unregister(this);
    }


    public void requestFiltering() {
        eventBus.post(new RequestFilterDataEvent());
    }

    public CollectionController<Trip> getTripsController() {
        return tripsController;
    }

    public void reload() {
        loadFromApi = true;
        tripsController.reload();
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
            tripsController.reload();
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

    public void onEvent(TripLikedEvent trip) {
        for (Trip temp : data) {
            if (temp.getId() == trip.getTrip().getId()) {
                temp.setLiked(trip.getTrip().isLiked());
            }
        }
        view.dataSetChanged();
    }

    public void onItemLike(Trip trip) {
        final Callback<JsonObject> callback = new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                db.saveTrip(trip);
            }

            @Override
            public void failure(RetrofitError error) {
                trip.setLiked(!trip.isLiked());
                view.dataSetChanged();
                view.showErrorMessage();
            }
        };

        if (trip.isLiked()) {
            dreamTripsApi.likeTrip(trip.getId(), callback);
        } else {
            dreamTripsApi.unlikeTrio(trip.getId(), callback);
        }

    }

    public void actionSearch(String query) {
    }

    public void actionMap() {
        fragmentCompass.replace(State.MAP, null);
    }

    public List<Trip> loadTrips() {
        return dreamTripsApi.getTrips();
    }

    public void onItemClick(Trip trip) {
        activityRouter.openTripDetails(trip);
    }

    public boolean needUpdate() throws ExecutionException, InterruptedException {
        long current = Calendar.getInstance().getTimeInMillis();
        return current - prefs.getLong(Prefs.LAST_SYNC) > DELTA && db.isEmpty(SnappyRepository.TRIP_KEY);
    }

    public static interface View extends BasePresentation.View {
        void dataSetChanged();

        void showErrorMessage();
    }

}