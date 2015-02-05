package com.worldventures.dreamtrips.presentation;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Collections2;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.utils.busevents.FilterBusEvent;
import com.worldventures.dreamtrips.utils.busevents.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.utils.busevents.RequestFilterDataEvent;
import com.worldventures.dreamtrips.utils.busevents.ShowInfoWindowEvent;
import com.worldventures.dreamtrips.view.fragment.FragmentMapTripInfo;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by Edward on 27.01.15.
 * presentation model to control map view
 */
@PresentationModel
public class MapFragmentPM extends BasePresentation<MapFragmentPM.View> {

    private List<Trip> data;
    private List<Trip> filteredData;
    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;
    private boolean showSoldOut;
    private List<Integer> acceptedRegions;
    private List<Activity> acceptedThemes;

    @Inject
    @Global
    EventBus eventBus;


    public MapFragmentPM(MapFragmentPM.View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        eventBus.register(this);
        eventBus.post(new RequestFilterDataEvent());
    }

    public void setData(List<Trip> data) {
        this.data = data;
    }

    public void onMapLoaded() {
        performFiltering();
    }

    public void onEvent(FilterBusEvent event) {
        if (event.isReset()) {
            resetFilters();
        } else {
            maxPrice = event.getMaxPrice();
            minNights = event.getMinNights();
            minPrice = event.getMinPrice();
            maxNights = event.getMaxNights();
            acceptedRegions = event.getAcceptedRegions();
            acceptedThemes = event.getAcceptedActivities();
            showSoldOut = event.isShowSoldOut();
        }
        performFiltering();
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
            filteredData.addAll(Collections2.filter(data, (input) ->
                    input.getPrice().getAmount() <= maxPrice
                            && input.getPrice().getAmount() >= minPrice
                            && input.getDuration() >= minNights
                            && input.getDuration() <= maxNights
                            && (showSoldOut || input.isAvailable())
                            && (acceptedThemes == null || !Collections.disjoint(acceptedThemes, input.getActivities()))
                            && (acceptedRegions == null || acceptedRegions.contains(input.getRegion().getId()))));
            reloadPins();
        }
    }

    public void resetFilters() {
        this.maxNights = Integer.MAX_VALUE;
        this.maxPrice = Double.MAX_VALUE;
        this.minPrice = 0;
        this.minNights = 0;
        this.acceptedRegions = null;
        this.acceptedThemes = null;
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
        fragmentCompass.add(State.MAP_INFO, bundle);
    }

    public void onCameraChanged() {
        if (fragmentCompass.getCurrentFragment() instanceof FragmentMapTripInfo) {
            fragmentCompass.pop();
        }
    }

    public void actionList() {
        fragmentCompass.pop();
    }

    public interface View extends BasePresentation.View {
        public void addPin(LatLng latLng, int id);

        public void clearMap();

        public void showInfoWindow(int offset);
    }
}
