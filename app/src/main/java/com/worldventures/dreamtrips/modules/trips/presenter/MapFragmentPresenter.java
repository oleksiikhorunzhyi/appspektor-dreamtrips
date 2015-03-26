package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowInfoWindowEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.Activity;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.Trip;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FragmentMapTripInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MapFragmentPresenter extends Presenter<MapFragmentPresenter.View> {

    @Inject
    SnappyRepository db;
    @Inject
    @Global
    EventBus eventBus;

    private List<Trip> data = new ArrayList<>();
    private List<Trip> filteredData;
    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private boolean showSoldOut;
    private List<Integer> acceptedRegions;
    private List<Activity> acceptedThemes;
    private DateFilterItem dateFilterItem = new DateFilterItem();
    private String query;

    public MapFragmentPresenter(MapFragmentPresenter.View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
    }

    public void onMapLoaded() {
        data.clear();
        try {
            data.addAll(db.getTrips());
        } catch (ExecutionException | InterruptedException e) {
            Log.e(MapFragmentPresenter.class.getSimpleName(), "", e);
        }
        setFilters(eventBus.getStickyEvent(FilterBusEvent.class));
        performFiltering();
    }

    public void setFilters(FilterBusEvent event) {
        if (event == null || event.isReset()) {
            resetFilters();
        } else {
            maxPrice = event.getMaxPrice();
            minNights = event.getMinNights();
            minPrice = event.getMinPrice();
            maxNights = event.getMaxNights();
            acceptedRegions = event.getAcceptedRegions();
            acceptedThemes = event.getAcceptedActivities();
            showSoldOut = event.isShowSoldOut();
            dateFilterItem = event.getDateFilterItem();
        }
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
            setFilters(event);
            performFiltering();
        }
    }

    public void onPause() {
        eventBus.unregister(this);
    }

    private void reloadPins() {
        view.clearMap();
        for (Trip trip : filteredData) {
            view.addPin(new LatLng(trip.getGeoLocation().getLat(),
                    trip.getGeoLocation().getLng()), trip.getId());
        }
    }

    private void performFiltering() {
        if (data != null) {
            filteredData = new ArrayList<>();
            filteredData.addAll(Queryable.from(data).filter((input) ->
                    input.getPrice().getAmount() <= maxPrice
                            && input.getAvailabilityDates().check(dateFilterItem)
                            && input.getPrice().getAmount() >= minPrice
                            && input.containsQuery(query)
                            && input.getDuration() >= minNights
                            && input.getDuration() <= maxNights
                            && (showSoldOut || input.isAvailable())
                            && (acceptedThemes == null || !Collections.disjoint(acceptedThemes, input.getActivities()))
                            && (acceptedRegions == null || acceptedRegions.contains(input.getRegion().getId()))).toList());
            reloadPins();
        }
    }

    public void applySearch(String query) {
        this.query = query;
        performFiltering();
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

    public void onEvent(InfoWindowSizeEvent event) {
        view.showInfoWindow(event.getOffset());
    }

    public void markerReady() {
        eventBus.post(new ShowInfoWindowEvent());
    }

    public void onMarkerClick(String id) {
        Trip resultTrip = null;
        int realId = Integer.valueOf(id);
        for (Trip trip : filteredData) {
            if (trip.getId() == realId) {
                resultTrip = trip;
                break;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentMapTripInfo.EXTRA_TRIP, resultTrip);
        fragmentCompass.add(Route.MAP_INFO, bundle);
    }

    public void onCameraChanged() {
        if (fragmentCompass.getCurrentFragment() instanceof FragmentMapTripInfo) {
            fragmentCompass.pop();
        }
    }

    public void actionList() {
        fragmentCompass.pop();
    }

    public interface View extends Presenter.View {
        public void addPin(LatLng latLng, int id);

        public void clearMap();

        public void showInfoWindow(int offset);
    }
}