package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.InfoWindowSizeEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowInfoWindowEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.fragment.FragmentMapTripInfo;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentPresenter extends BaseDreamTripsPresenter<MapFragmentPresenter.View> {

    private List<TripModel> trips = new ArrayList<>();
    private List<TripModel> filteredTrips = new ArrayList<>();
    private String query;

    private boolean popped = false;

    public MapFragmentPresenter(MapFragmentPresenter.View view) {
        super(view);
    }

    public void onMapLoaded() {
        trips.clear();
        trips.addAll(db.getTrips());
        setFilters(eventBus.getStickyEvent(FilterBusEvent.class));
        performFiltering();
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
        for (TripModel trip : filteredTrips) {
            view.addPin(new LatLng(trip.getGeoLocation().getLat(), trip.getGeoLocation().getLng()), trip.getTripId());
        }
    }

    private void performFiltering() {
        if (trips != null) {
            ArrayList<TripModel> filterdTrips = performFiltering(trips);
            filteredTrips.clear();
            filteredTrips.addAll(Queryable.from(filterdTrips).filter((input) -> input.containsQuery(query)).toList());
            reloadPins();
        }
    }

    public void applySearch(String query) {
        this.query = query;
        performFiltering();
    }

    public void onEvent(InfoWindowSizeEvent event) {
        view.showInfoWindow(event.getOffset());
    }

    public void markerReady() {
        eventBus.post(new ShowInfoWindowEvent());
    }

    public void onMarkerClick(String id) {
        TripModel resultTrip = null;
        for (TripModel trip : filteredTrips) {
            if (trip.getTripId().equals(id)) {
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
        if (!popped) {
            fragmentCompass.pop();
            popped = true;
        }
    }

    public interface View extends Presenter.View {
        void addPin(LatLng latLng, String id);

        void clearMap();

        void showInfoWindow(int offset);
    }
}
