package com.worldventures.dreamtrips.modules.trips.manager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.worldventures.dreamtrips.modules.trips.api.GetDetailedTripsAction;
import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsAction;
import com.worldventures.dreamtrips.modules.trips.manager.functions.ExistsMarkerFilterer;
import com.worldventures.dreamtrips.modules.trips.manager.functions.MapObjectConverter;
import com.worldventures.dreamtrips.modules.trips.manager.functions.MarkerOptionsConverter;
import com.worldventures.dreamtrips.modules.trips.manager.functions.RemoveOldMarkersAction;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.TripClusterItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TripMapManager {

    private CompositeSubscription subscriptions;
    private ActionPipe<GetMapObjectsAction> mapObjectsActionPipe;
    private ActionPipe<GetDetailedTripsAction> detailedTripsActionPipe;
    private TripFilterDataProvider tripFilterDataProvider;
    private Gson gson;
    private Context context;

    public TripMapManager(Janet janet, TripFilterDataProvider tripFilterDataProvider, Context context, Gson gson) {
        this.tripFilterDataProvider = tripFilterDataProvider;
        mapObjectsActionPipe = janet.createPipe(GetMapObjectsAction.class, Schedulers.io());
        detailedTripsActionPipe = janet.createPipe(GetDetailedTripsAction.class, Schedulers.io());
        this.context = context;
        this.gson = gson;
    }

    public void subscribe(Callback tripMapCallback) {
        subscriptions = new CompositeSubscription();
        //
//        subscriptions.add(getMapObjectsListObservable(tripMapCallback)
//                .subscribe(tripMapCallback::onMapObjectsLoaded,
//                        error -> {
//                            Timber.e(error, error.getMessage());
//                        }));
        //local clustering
        subscriptions.add(getTripMapObjectsListObservable(tripMapCallback)
                .subscribe(tripMapCallback::onTripMapObjectsLoaded,
                        error -> {
                            Timber.e(error, error.getMessage());
                        }));
        //
        subscriptions.add(detailedTripsActionPipe.observeSuccess()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getDetailedTripsAction -> {
                    tripMapCallback.onTripsLoaded(getDetailedTripsAction.getTripList());
                }, error -> {
                    Timber.e(error.getMessage());
                }));
        try {
            MockTrips mockTrips = gson.fromJson(new InputStreamReader(context.getAssets().open("trip.json")), MockTrips.class);
            Observable.just(mockTrips).flatMap(m -> Observable.just(m.tripList))
                    .doOnNext(tripMapCallback::updateMapObjectsList)
                    .flatMap(mapObjectHolders -> Observable.from(mapObjectHolders).map(TripClusterItem::new).toList())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(list1 -> {
                tripMapCallback.onTripMapObjectsLoaded(list1);
            }, error -> {
                Timber.e(error, error.getMessage());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private Observable<List<MarkerOptions>> getMapObjectsListObservable(Callback tripMapCallback) {
        return mapObjectsActionPipe.observeSuccess()
                .doOnNext(getMapObjectsAction -> tripMapCallback.updateMapObjectsList(getMapObjectsAction.getMapObjects()))
                .flatMap(tripMapCallback.getMapObjectConverter())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(tripMapCallback.getRemoveAction())
                .flatMap(tripMapCallback.getExistsMarkerFilterer())
                .flatMap(tripMapCallback.getMarkerOptionsConverter());
    }

    //local clustering
    public Observable<List<TripClusterItem>> getTripMapObjectsListObservable(Callback tripMapCallback) {
        return mapObjectsActionPipe.observeSuccess()
                .doOnNext(getMapObjectsAction -> tripMapCallback.updateMapObjectsList(getMapObjectsAction.getMapObjects()))
                .flatMap(getMapObjectsAction -> Observable.just(getMapObjectsAction.getMapObjects()))
                .flatMap(mapObjectHolders -> Observable.from(mapObjectHolders).map(TripClusterItem::new).toList())
                .observeOn(AndroidSchedulers.mainThread());
    }
    //

    public void unsubscribe() {
        if (subscriptions != null && subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
    }

    public void loadTrips(List<String> tripUids) {
        detailedTripsActionPipe.send(new GetDetailedTripsAction(tripUids));
    }

    public void reloadMapObjects(LatLngBounds latLngBounds, String query) {
        GetMapObjectsAction action = new GetMapObjectsAction(tripFilterDataProvider.get(), latLngBounds, query);
        mapObjectsActionPipe.send(action);
    }

    public interface Callback {

        void updateMapObjectsList(List<MapObjectHolder> mapObjectsHolders);

        MapObjectConverter getMapObjectConverter();

        ExistsMarkerFilterer getExistsMarkerFilterer();

        RemoveOldMarkersAction getRemoveAction();

        MarkerOptionsConverter getMarkerOptionsConverter();

        void onMapObjectsLoaded(List<MarkerOptions> options);

        //local clustering
        void onTripMapObjectsLoaded(List<TripClusterItem> tripClusterItems);
        //

        void onTripsLoaded(List<TripModel> trips);
    }

    private class MockTrips {
        public List<MapObjectHolder> tripList;
    }

}
