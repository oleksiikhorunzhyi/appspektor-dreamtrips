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
import com.worldventures.dreamtrips.modules.trips.api.GetDetailedTripsHttpAction;
import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsHttpAction;
import com.worldventures.dreamtrips.modules.trips.manager.TripFilterDataProvider;
import com.worldventures.dreamtrips.modules.trips.manager.TripMapInteractor;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripClusterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TripMapPresenter extends Presenter<TripMapPresenter.View> {

    @State
    String query;

    @Inject
    TripMapInteractor tripMapInteractor;
    @Inject
    TripFilterDataProvider tripFilterDataProvider;

    private List<MapObjectHolder> mapObjects;
    private List<Marker> existsMarkers;

    private CompositeSubscription compositeSubscription;

    public TripMapPresenter() {
        mapObjects = new ArrayList<>();
        existsMarkers = new ArrayList<>();
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void dropView() {
        super.dropView();
        //
        if (compositeSubscription.hasSubscriptions() && !compositeSubscription.isUnsubscribed())
            compositeSubscription.unsubscribe();
    }

    public void onEvent(FilterBusEvent event) {
        reloadMapObjects();
    }

    public void onEvent(MenuPressedEvent event) {
        removeInfoIfNeeded();
    }

    public void onCameraChanged() {
        removeInfoIfNeeded();
    }

    public String getQuery() {
        return query;
    }

    public void applySearch(String query) {
        this.query = query;
        //
        removeInfoIfNeeded();
        reloadMapObjects();
    }

    public void onMapLoaded() {
        reloadMapObjects();
    }

    public void addMarker(Marker marker) {
        this.existsMarkers.add(marker);
    }

    public void onMarkerClicked(Marker marker) {
        MapObjectHolder holder = Queryable.from(mapObjects).firstOrDefault(object -> object.getItem().getCoordinates().getLat() == marker.getPosition().latitude
                && object.getItem().getCoordinates().getLng() == marker.getPosition().longitude);
        if (holder == null) return;
        //
        switch (holder.getType()) {
            case PIN:
                view.setSelectedLocation(marker.getPosition());
                //
                List<String> tripUids = ((Pin) holder.getItem()).getTripUids();
                view.scrollCameraToPin(tripUids.size());
                updateExistsMarkers(view.getMarkers());
                //
                view.showInfoContainer();
                view.updateContainerParams(tripUids.size());
                //
                addAlphaToMarkers(marker);
                //
                loadTrips(tripUids);
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

    private void loadTrips(List<String> tripUids) {
        compositeSubscription.add(tripMapInteractor.detailedTripsPipe()
                .createObservableResult(new GetDetailedTripsHttpAction(tripUids))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDetailedTripsAction -> {
                    onTripsLoaded(getDetailedTripsAction.getTripList());
                }, error -> {
                    Timber.e(error.getMessage());
                }));
    }

    public void reloadMapObjects() {
        //local clustering
        if (view != null) {
            compositeSubscription.add(tripMapInteractor.mapObjectsPipe()
                    .createObservableResult(new GetMapObjectsHttpAction(tripFilterDataProvider.get(),
                            view.getMap().getProjection().getVisibleRegion().latLngBounds, query))
                    .doOnNext(getMapObjectsAction -> updateMapObjectsList(getMapObjectsAction.getMapObjects()))
                    .flatMap(getMapObjectsAction -> Observable.just(getMapObjectsAction.getMapObjects())
                            .flatMap(mapObjectHolders -> Observable.from(mapObjectHolders).map(TripClusterItem::new).toList()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTripMapObjectsLoaded,
                            error -> {
                                Timber.e(error, error.getMessage());
                            }));
        }
        //
    }

    private void cancelLatestTripAction() {
        tripMapInteractor.detailedTripsPipe().cancelLatest();
    }

    //local clustering
    public void updateExistsMarkers(List<Marker> markers) {
        existsMarkers.clear();
        existsMarkers.addAll(markers);
    }
    //

    public void removeInfoIfNeeded() {
        if (view != null) {
            view.removeTripsPopupInfo();
            removeAlphaFromMarkers();
            cancelLatestTripAction();
        }
    }

    private void updateMapObjectsList(List<MapObjectHolder> mapObjectsHolders) {
        mapObjects.clear();
        mapObjects.addAll(mapObjectsHolders);
    }

    private void onMapObjectsLoaded(List<MarkerOptions> options) {
        Queryable.from(options).forEachR(option -> view.addMarker(option));
        //
        updateMarkersAlphaIfNeeded();
    }

    //local clustering
    private void onTripMapObjectsLoaded(List<TripClusterItem> tripClusterItems) {
        if (view != null) {
            view.clearItems();
            view.addItems(tripClusterItems);
        }
    }
    //

    private void onTripsLoaded(List<TripModel> trips) {
        if (view != null) view.moveTo(trips);
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

    public interface View extends Presenter.View {

        void addMarker(MarkerOptions options);

        void moveTo(List<TripModel> tripList);

        void removeTripsPopupInfo();

        void zoomToBounds(LatLngBounds latLngBounds);

        void setSelectedLocation(LatLng latLng);

        GoogleMap getMap();

        void updateContainerParams(int tripCount);

        void scrollCameraToPin(int size);

        void showInfoContainer();

        // local clustering
        void addItems(List<TripClusterItem> tripClusterItems);

        void clearItems();

        List<Marker> getMarkers();
        //
    }
}
