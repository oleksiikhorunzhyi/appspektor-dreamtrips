package com.worldventures.dreamtrips.modules.trips.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.Coordinates;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.trips.api.GetDetailedTripsAction;
import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsAction;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.util.TripPinFactory;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TripMapManager {

    private CompositeSubscription subscriptions;
    private ActionPipe<GetMapObjectsAction> mapObjectsActionPipe;
    private ActionPipe<GetDetailedTripsAction> detailedTripsActionPipe;
    private Callback tripMapCallback;
    private List<MapObjectHolder> mapObjects;
    private Context context;
    private TripFilterDataProvider tripFilterDataProvider;
    private List<Marker> existsMarkers;
    private GoogleMap googleMap;

    public TripMapManager(Janet janet, Context context, TripFilterDataProvider tripFilterDataProvider) {
        this.context = context;
        this.tripFilterDataProvider = tripFilterDataProvider;
        mapObjectsActionPipe = janet.createPipe(GetMapObjectsAction.class, Schedulers.io());
        detailedTripsActionPipe = janet.createPipe(GetDetailedTripsAction.class, Schedulers.io());
        subscriptions = new CompositeSubscription();
    }

    public void subscribe(GoogleMap googleMap, Callback tripMapCallback) {
        this.googleMap = googleMap;
        this.tripMapCallback = tripMapCallback;
        existsMarkers = new ArrayList<>();
        //
        subscriptions.add(subscribeToPinClicks(googleMap));
        //
        subscriptions.add(mapObjectsActionPipe.observe()
                .map(state -> state.action)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(getMapObjectsAction -> mapObjects = getMapObjectsAction.getMapObjects())
                .flatMap(this::convertToPinPair)
                .doOnNext(this::removeOldMarkers)
                .flatMap(this::filterExistsMarkers)
                .onErrorResumeNext(throwable -> {
                    return Observable.from(new ArrayList<>());
                })
                .subscribe(mapObjects -> {
                    if (this.tripMapCallback != null)
                        this.tripMapCallback.onMapObjectsLoaded(mapObjects);
                }, error -> {
                    Timber.e(error, error.getMessage());
                }));
        //
        subscriptions.add(detailedTripsActionPipe.observeSuccess().subscribe(getDetailedTripsAction -> {
            if (this.tripMapCallback != null)
                this.tripMapCallback.onTripsLoaded(getDetailedTripsAction.getTripList());
        }, error -> {
            Timber.e(error.getMessage());
        }));
    }

    public void unsubscribe() {
        if (subscriptions != null && subscriptions.hasSubscriptions() && subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
        //
        googleMap = null;
        tripMapCallback = null;
        existsMarkers = null;
    }

    public void addMarker(Marker marker) {
        existsMarkers.add(marker);
    }

    public void removeAlphaFromMarkers() {
        Queryable.from(existsMarkers).forEachR(existMarker -> existMarker.setAlpha(1f));
    }

    public void reloadMapObjects() {
        LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        GetMapObjectsAction action = new GetMapObjectsAction(tripFilterDataProvider.get(), latLngBounds, tripMapCallback.getQuery());
        mapObjectsActionPipe.send(action);
    }

    private Subscription subscribeToPinClicks(GoogleMap googleMap) {
        return MapObservableFactory.createMarkerClickObservable(googleMap)
                .subscribe(marker -> {
                    MapObjectHolder holder = Queryable.from(mapObjects).firstOrDefault(object -> object.getItem().getCoordinates().getLat() == marker.getPosition().latitude
                            && object.getItem().getCoordinates().getLng() == marker.getPosition().longitude);
                    if (holder == null) return;
                    //
                    switch (holder.getType()) {
                        case PIN:
                            detailedTripsActionPipe.send(new GetDetailedTripsAction(((Pin) holder.getItem()).getTripUids()));
                            addAlphaToMarkers(marker);
                            break;
                        case CLUSTER:
                            if (tripMapCallback != null)
                                tripMapCallback.onClusterClicked((Cluster) holder.getItem());
                            break;
                    }
                }, error -> {
                    Timber.e(error, error.getMessage());
                });
    }

    private void addAlphaToMarkers(Marker marker) {
        Queryable.from(existsMarkers).filter(existMarker -> !marker.equals(existMarker))
                .forEachR(existMarker -> existMarker.setAlpha(0.6f));
    }

    private Observable<List<Pair<Bitmap, MapObject>>> convertToPinPair(GetMapObjectsAction getMapObjectsAction) {
        return Observable.from(getMapObjectsAction.getMapObjects())
                .map(mapObjectHolder -> new Pair<>(TripPinFactory
                        .createPinBitmapFromMapObject(context, mapObjectHolder), mapObjectHolder.getItem()))
                .toList();
    }

    private Observable<List<Pair<Bitmap, MapObject>>> filterExistsMarkers(List<Pair<Bitmap, MapObject>> mapObjects) {
        return Observable.from(mapObjects)
                .filter(pair -> {
                    Coordinates coordinates = pair.second.getCoordinates();
                    LatLng latLng = new LatLng(coordinates.getLat(), coordinates.getLng());
                    return Queryable.from(existsMarkers).firstOrDefault(marker -> marker.getPosition().equals(latLng)) == null;
                }).toList();
    }

    private Observable<List<Pair<Bitmap, MapObject>>> removeOldMarkers(List<Pair<Bitmap, MapObject>> mapObjects) {
        List<Marker> markersToRemove = new ArrayList<>();
        Queryable.from(existsMarkers).forEachR(marker -> {
            if (Queryable.from(mapObjects).firstOrDefault(pair -> {
                Coordinates coordinates = pair.second.getCoordinates();
                LatLng latLng = new LatLng(coordinates.getLat(), coordinates.getLng());
                return marker.getPosition().equals(latLng);
            }) == null) {
                markersToRemove.add(marker);
                marker.remove();
            }
        });
        existsMarkers.removeAll(markersToRemove);
        return Observable.just(mapObjects);
    }

    public interface Callback {

        void onMapObjectsLoaded(List<Pair<Bitmap, MapObject>> mapObjects);

        void onTripsLoaded(List<TripModel> trips);

        void onClusterClicked(Cluster cluster);

        String getQuery();
    }

}
