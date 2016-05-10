package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.MapInfoReadyEvent;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.manager.TripMapManager;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripMapInfoBundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class TripMapPresenter extends BaseTripsPresenter<TripMapPresenter.View> {

    private List<TripModel> filteredTrips = new ArrayList<>();
    private boolean mapReady;
    private MapInfoReadyEvent pendingMapInfoEvent;

    @State
    String query;

    @Inject
    TripMapManager tripMapManager;

    public TripMapPresenter() {
        super();
    }

    @Override
    public void dropView() {
        super.dropView();
        //
        tripMapManager.unsubscribe();
    }

    public void onEvent(FilterBusEvent event) {
        if (event != null) {
            setFilters(event);
            performFiltering();
        }
    }

    public String getQuery() {
        return query;
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
        //
        tripMapManager.subscribe(view.getMap(), new TripMapManager.Callback() {

            @Override
            public void onMapObjectsLoaded(List<MapObjectHolder> mapObjects) {
                // TODO draw clusters and trips on map
            }

            @Override
            public void onTripsLoaded(List<TripModel> trips) {
                // TODO show trip list
            }

            @Override
            public void onClusterClicked(Cluster cluster) {
                LatLng northEast = new LatLng(cluster.getTopLeft().getLat(), cluster.getBottomRight().getLng());
                LatLng southWest = new LatLng(cluster.getBottomRight().getLat(), cluster.getTopLeft().getLng());
                LatLngBounds latLngBounds = new LatLngBounds(southWest, northEast);
                //
                view.zoomToBounds(latLngBounds);
            }
        });
    }

    private void checkPendingMapInfo() {
        if (pendingMapInfoEvent != null) {
            view.prepareInfoWindow(pendingMapInfoEvent.getOffset());
            pendingMapInfoEvent = null;
        }
    }

    private void reloadPins() {
        if (view != null) {
            view.clearMap();
            for (TripModel trip : filteredTrips) {
                view.addPin(new LatLng(trip.getGeoLocation().getLat(), trip.getGeoLocation().getLng()), trip.getTripId());
            }
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
        TripMapInfoBundle bundle = new TripMapInfoBundle(resultTrip);
        view.moveTo(Route.MAP_INFO, bundle);
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
        if (view != null)
            view.removeIfNeeded(Route.MAP_INFO);
    }

    public void actionList() {
        view.back();
    }

    public interface View extends Presenter.View {

        void addPin(LatLng latLng, String id);

        void clearMap();

        void prepareInfoWindow(int offset);

        void moveTo(Route route, TripMapInfoBundle bundle);

        void removeIfNeeded(Route route);

        void back();

        void zoomToBounds(LatLngBounds latLngBounds);

        GoogleMap getMap();
    }
}
