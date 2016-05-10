package com.worldventures.dreamtrips.modules.trips.manager;

import com.google.android.gms.maps.GoogleMap;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.trips.api.GetDetailedTripsAction;
import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsAction;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TripMapManager {

    private CompositeSubscription subscriptions;
    private ActionPipe<GetMapObjectsAction> mapObjectsActionPipe;
    private ActionPipe<GetDetailedTripsAction> detailedTripsActionPipe;
    private Callback tripMapCallback;
    private List<MapObjectHolder> mapObjects;

    public TripMapManager(Janet janet) {
        mapObjectsActionPipe = janet.createPipe(GetMapObjectsAction.class, Schedulers.io());
        detailedTripsActionPipe = janet.createPipe(GetDetailedTripsAction.class, Schedulers.io());
        subscriptions = new CompositeSubscription();
    }

    public void subscribe(GoogleMap googleMap, Callback tripMapCallback) {
        this.tripMapCallback = tripMapCallback;
        //
        subscriptions.add(subscribeToCameraChanges(googleMap));
        subscriptions.add(subscribeToPinClicks(googleMap));
        //
        subscriptions.add(mapObjectsActionPipe.observeSuccess().subscribe(getMapObjectsAction -> {
            if (this.tripMapCallback != null) {
                mapObjects = getMapObjectsAction.getMapObjects();
                this.tripMapCallback.onMapObjectsLoaded(mapObjects);
            }
        }, error -> {
            Timber.e(error.getMessage());
        }));
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
        tripMapCallback = null;
    }

    private Subscription subscribeToCameraChanges(GoogleMap googleMap) {
        return MapObservableFactory.createCameraChangeObservable(googleMap)
                .subscribe(cameraPosition -> {
                    mapObjectsActionPipe.send(new GetMapObjectsAction(googleMap.getProjection().getVisibleRegion().latLngBounds));
                }, error -> {
                    Timber.e(error.getMessage());
                });
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
                            break;
                        case CLUSTER:
                            if (tripMapCallback != null)
                                tripMapCallback.onClusterClicked((Cluster) holder.getItem());
                            break;
                    }
                }, error -> {
                    Timber.e(error.getMessage());
                });
    }

    public interface Callback {

        void onMapObjectsLoaded(List<MapObjectHolder> mapObjects);

        void onTripsLoaded(List<TripModel> trips);

        void onClusterClicked(Cluster cluster);
    }
}
