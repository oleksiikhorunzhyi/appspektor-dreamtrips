package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.manager.TripMapManager;
import com.worldventures.dreamtrips.modules.trips.manager.functions.ExistsMarkerFilterer;
import com.worldventures.dreamtrips.modules.trips.manager.functions.MapObjectConverter;
import com.worldventures.dreamtrips.modules.trips.manager.functions.MarkerOptionsConverter;
import com.worldventures.dreamtrips.modules.trips.manager.functions.RemoveOldMarkersAction;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripClusterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripMapDetailsAnchor;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class TripMapPresenter extends Presenter<TripMapPresenter.View> implements TripMapManager.Callback {

    @State
    String query;

    @Inject
    TripMapManager tripMapManager;

    private List<MapObjectHolder> mapObjects;
    private List<Marker> existsMarkers;

    public TripMapPresenter() {
        mapObjects = new ArrayList<>();
        existsMarkers = new ArrayList<>();
    }

    @Override
    public void dropView() {
        super.dropView();
        //
        tripMapManager.unsubscribe();
    }

    public void onEvent(FilterBusEvent event) {
        reloadMapObjects();
    }

    public String getQuery() {
        return query;
    }

    public void applySearch(String query) {
        this.query = query;
        removeInfoIfNeeded();
        reloadMapObjects();
    }

    public void onMapLoaded() {
        //
        tripMapManager.subscribe(this);
        reloadMapObjects();
    }

    public void addMarker(Marker marker) {
        this.existsMarkers.add(marker);
    }

    private void addAlphaToMarkers(Marker marker) {
        Queryable.from(existsMarkers).filter(existMarker -> !marker.equals(existMarker))
                .forEachR(existMarker -> existMarker.setAlpha(0.6f));
    }

    private void removeAlphaFromMarkers() {
        Queryable.from(existsMarkers).forEachR(existMarker -> existMarker.setAlpha(1f));
    }

    private void updateMarkersAlphaIfNeeded() {
        if (Queryable.from(existsMarkers).firstOrDefault(marker -> marker.getAlpha() == 0.6f) != null) {
            Marker checkedMarker = Queryable.from(existsMarkers).firstOrDefault(marker -> marker.getAlpha() == 1f);
            if (checkedMarker != null) addAlphaToMarkers(checkedMarker);
        }
    }

    public void onMarkerClicked(Marker marker) {
        MapObjectHolder holder = Queryable.from(mapObjects).firstOrDefault(object -> object.getItem().getCoordinates().getLat() == marker.getPosition().latitude
                && object.getItem().getCoordinates().getLng() == marker.getPosition().longitude);
        if (holder == null) return;
        //
        switch (holder.getType()) {
            case PIN:
                view.setSelectedLocation(marker.getPosition());
                List<String> tripUids = ((Pin) holder.getItem()).getTripUids();
                view.scrollCameraToPin(tripUids.size());
                tripMapManager.loadTrips(tripUids);
                updateExistsMarkers(view.getMarkers());
                addAlphaToMarkers(marker);
                break;
            case CLUSTER:
                Cluster cluster = (Cluster) holder.getItem();
                LatLng northEast = new LatLng(cluster.getTopLeft().getLat(), cluster.getBottomRight().getLng());
                LatLng southWest = new LatLng(cluster.getBottomRight().getLat(), cluster.getTopLeft().getLng());
                LatLngBounds latLngBounds = new LatLngBounds(southWest, northEast);
                //
                view.zoomToBounds(latLngBounds);
                break;
        }
    }

    //local clustering
    public void updateExistsMarkers(List<Marker> markers) {
        existsMarkers.clear();
        existsMarkers.addAll(markers);
    }
    //

    public void onEvent(MenuPressedEvent event) {
        removeInfoIfNeeded();
    }

    public void onCameraChanged() {
        removeInfoIfNeeded();
    }

    public void removeInfoIfNeeded() {
        if (view != null) {
            view.removeTripsPopupInfo();
            removeAlphaFromMarkers();
        }
    }

    public void reloadMapObjects() {
        tripMapManager.reloadMapObjects(view.getMap().getProjection().getVisibleRegion().latLngBounds, query);
    }

    //////////////////////////////
    /// TripMapManager callbacks
    //////////////////////////////

    @Override
    public void updateMapObjectsList(List<MapObjectHolder> mapObjectsHolders) {
        mapObjects.clear();
        mapObjects.addAll(mapObjectsHolders);
    }

    @Override
    public MapObjectConverter getMapObjectConverter() {
        return new MapObjectConverter(context);
    }

    @Override
    public ExistsMarkerFilterer getExistsMarkerFilterer() {
        return new ExistsMarkerFilterer(existsMarkers);
    }

    @Override
    public RemoveOldMarkersAction getRemoveAction() {
        return new RemoveOldMarkersAction(existsMarkers);
    }

    @Override
    public MarkerOptionsConverter getMarkerOptionsConverter() {
        return new MarkerOptionsConverter();
    }

    @Override
    public void onMapObjectsLoaded(List<MarkerOptions> options) {
        Queryable.from(options).forEachR(option -> view.addMarker(option));
        //
        updateMarkersAlphaIfNeeded();
    }

    //local clustering
    @Override
    public void onTripMapObjectsLoaded(List<TripClusterItem> tripClusterItems) {
        view.clearItems();
        view.addItems(tripClusterItems);
    }
    //

    @Override
    public void onTripsLoaded(List<TripModel> trips) {
        TripMapDetailsAnchor anchor = null;
        if (view.isTabletLandscape())
            anchor = view.updateContainerParams(trips.size());
        //
        view.moveTo(trips, anchor);
    }

    public interface View extends Presenter.View {

        void addMarker(MarkerOptions options);

        void moveTo(List<TripModel> tripList, TripMapDetailsAnchor anchor);

        void removeTripsPopupInfo();

        void zoomToBounds(LatLngBounds latLngBounds);

        void setSelectedLocation(LatLng latLng);

        GoogleMap getMap();

        TripMapDetailsAnchor updateContainerParams(int tripCount);

        void scrollCameraToPin(int size);

        // local clustering
        void addItems(List<TripClusterItem> tripClusterItems);

        void clearItems();

        List<Marker> getMarkers();
        //
    }
}
