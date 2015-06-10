package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.MapInfoReadyEvent;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripMapInfoFragment;

import java.util.ArrayList;
import java.util.List;

public class TripMapPresenter extends BaseTripsPresenter<TripMapPresenter.View> {

    private List<TripModel> filteredTrips = new ArrayList<>();
    private String query;
    private boolean mapReady;
    private MapInfoReadyEvent pendingMapInfoEvent;

    public TripMapPresenter() {
        super();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        fragmentCompass.setContainerId(R.id.container_info);
        fragmentCompass.disableBackStack();
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
            setFilters(event);
            performFiltering();
        }
    }

    private void performFiltering() {
        if (cachedTrips != null && cachedTrips.size() > 0) {
            List<TripModel> filteredFromCache = performFiltering(cachedTrips);
            this.filteredTrips.clear();
            this.filteredTrips.addAll(Queryable.from(filteredFromCache).filter((input) -> input.containsQuery(query)).toList());
            reloadPins();
        }
    }

    public void applySearch(String query) {
        this.query = query;
        performFiltering();
        removeInfoIfNeeded();
    }

    public void onMapLoaded() {
        mapReady = true;
        cachedTrips.clear();
        cachedTrips.addAll(db.getTrips());
        setFilters(eventBus.getStickyEvent(FilterBusEvent.class));
        performFiltering();
        checkPendingMapInfo();
    }

    private void checkPendingMapInfo() {
        if (pendingMapInfoEvent != null) {
            view.prepareInfoWindow(pendingMapInfoEvent.getOffset());
            pendingMapInfoEvent = null;
        }
    }

    private void reloadPins() {
        view.clearMap();
        for (TripModel trip : filteredTrips) {
            view.addPin(new LatLng(trip.getGeoLocation().getLat(), trip.getGeoLocation().getLng()), trip.getTripId());
        }
    }

    public void onEvent(MenuPressedEvent event) {
        removeInfoIfNeeded();
    }

    public void onMarkerClick(String id) {
        openTrip(id);
    }

    private void openTrip(String id) {
        TripModel resultTrip = Queryable
                .from(filteredTrips)
                .firstOrDefault(t -> t.getTripId().equals(id));
        Bundle bundle = new Bundle();
        bundle.putSerializable(TripMapInfoFragment.EXTRA_TRIP, resultTrip);
        fragmentCompass.replace(Route.MAP_INFO, bundle);
    }

    public void onEvent(MapInfoReadyEvent event) {
        if (!mapReady) pendingMapInfoEvent = event;
        else {
            pendingMapInfoEvent = null;
            view.prepareInfoWindow(event.getOffset());
        }
    }

    public void onMarkerInfoPositioned() {
        eventBus.post(new ShowMapInfoEvent());
    }

    public void onCameraChanged() {
        removeInfoIfNeeded();
    }

    private void removeInfoIfNeeded() {
        if (fragmentCompass.getCurrentFragment() instanceof TripMapInfoFragment) {
            fragmentCompass.remove(Route.MAP_INFO.getClazzName());
        }
    }

    public void actionList() {
        fragmentCompass.pop();
    }

    public interface View extends Presenter.View {
        void addPin(LatLng latLng, String id);

        void clearMap();

        void prepareInfoWindow(int offset);
    }
}
