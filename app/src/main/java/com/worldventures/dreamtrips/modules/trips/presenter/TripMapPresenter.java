package com.worldventures.dreamtrips.modules.trips.presenter;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.MapInfoReadyEvent;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.ShowMapInfoEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.manager.TripMapManager;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripMapListBundle;

import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class TripMapPresenter extends Presenter<TripMapPresenter.View> {

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
//            setFilters(event);
            performFiltering();
        }
    }

    public String getQuery() {
        return query;
    }

    private void performFiltering() {
//        if (cachedTrips != null && cachedTrips.size() > 0) {
//            List<TripModel> filteredFromCache = performFiltering(cachedTrips);
//            this.filteredTrips.clear();
//            this.filteredTrips.addAll(Queryable.from(filteredFromCache).filter((input) -> input.containsQuery(query)).toList());
//            reloadPins();
//        }
    }

    public void applySearch(String query) {
        this.query = query;
        performFiltering();
        removeInfoIfNeeded();
    }

    public void onMapLoaded() {
        mapReady = true;
//        setFilters(eventBus.getStickyEvent(FilterBusEvent.class));
        performFiltering();
        checkPendingMapInfo();
        //
        tripMapManager.subscribe(view.getMap(), new TripMapManager.Callback() {

            @Override
            public void onMapObjectsLoaded(List<Pair<Bitmap, MapObject>> mapObjects) {
                Queryable.from(mapObjects).forEachR(pair -> tripMapManager.addMarker(view.addPin(pair.first, pair.second)));
            }

            @Override
            public void onTripsLoaded(List<TripModel> trips) {
                view.moveTo(Route.MAP_INFO, new TripMapListBundle(trips));
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

    public void onEvent(MenuPressedEvent event) {
        removeInfoIfNeeded();
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

        Marker addPin(Bitmap pinBitmap, MapObject mapObject);

        void clearMap();

        void prepareInfoWindow(int offset);

        void moveTo(Route route, TripMapListBundle bundle);

        void removeIfNeeded(Route route);

        void back();

        void zoomToBounds(LatLngBounds latLngBounds);

        GoogleMap getMap();
    }
}
