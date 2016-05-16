package com.worldventures.dreamtrips.modules.trips.presenter;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.events.FilterBusEvent;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.manager.TripMapManager;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class TripMapPresenter extends Presenter<TripMapPresenter.View> {

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
        tripMapManager.reloadMapObjects();
    }

    public String getQuery() {
        return query;
    }


    public void applySearch(String query) {
        this.query = query;
        removeInfoIfNeeded();
        tripMapManager.reloadMapObjects();
    }

    public void onMapLoaded() {
        //
        tripMapManager.subscribe(view.getMap(), new TripMapManager.Callback() {

            @Override
            public void onMapObjectsLoaded(List<Pair<Bitmap, MapObject>> mapObjects) {
                Queryable.from(mapObjects).forEachR(pair -> tripMapManager.addMarker(view.addPin(pair.first, pair.second)));
            }

            @Override
            public void onTripsLoaded(List<TripModel> trips) {
                view.moveTo(trips);
            }

            @Override
            public void onClusterClicked(Cluster cluster) {
                LatLng northEast = new LatLng(cluster.getTopLeft().getLat(), cluster.getBottomRight().getLng());
                LatLng southWest = new LatLng(cluster.getBottomRight().getLat(), cluster.getTopLeft().getLng());
                LatLngBounds latLngBounds = new LatLngBounds(southWest, northEast);
                //
                view.zoomToBounds(latLngBounds);
            }

            @Override
            public String getQuery() {
                return query;
            }

        });
    }

    public void onEvent(MenuPressedEvent event) {
        removeInfoIfNeeded();
    }

    public void onCameraChanged() {
        removeInfoIfNeeded();
    }

    private void removeInfoIfNeeded() {
        if (view != null) {
            view.removeTripsPopupInfo();
            tripMapManager.removeAlphaFromMarkers();
        }
    }

    public void reloadMapObjects() {
        tripMapManager.reloadMapObjects();
    }

    public interface View extends Presenter.View {

        Marker addPin(Bitmap pinBitmap, MapObject mapObject);

        void moveTo(List<TripModel> bundle);

        void removeTripsPopupInfo();

        void zoomToBounds(LatLngBounds latLngBounds);

        GoogleMap getMap();
    }
}
