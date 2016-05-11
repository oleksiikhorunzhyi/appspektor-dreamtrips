package com.worldventures.dreamtrips.modules.trips.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.innahema.collections.query.queriables.Queryable;
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
import java.util.concurrent.TimeUnit;

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
    private Gson gson;

    public TripMapManager(Janet janet, Context context, Gson gson) {
        this.context = context;
        this.gson = gson;
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
        subscriptions.add(mapObjectsActionPipe.observeSuccess()
                .doOnNext(getMapObjectsAction -> mapObjects = getMapObjectsAction.getMapObjects())
                .flatMap(getMapObjectsAction ->
                        Observable.from(getMapObjectsAction.getMapObjects())
                                .map(mapObjectHolder -> new Pair<>(TripPinFactory
                                        .createPinBitmapFromMapObject(context, mapObjectHolder), mapObjectHolder.getItem()))
                                .toList())
                .subscribe(mapObjects -> {
                    if (this.tripMapCallback != null)
                        this.tripMapCallback.onMapObjectsLoaded(mapObjects);
                }, error -> {
                    Timber.e(error.getMessage());
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
        tripMapCallback = null;
    }

    private Subscription subscribeToCameraChanges(GoogleMap googleMap) {
        return MapObservableFactory.createCameraChangeObservable(googleMap)
                .throttleLast(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cameraPosition -> {
//                    mapObjectsActionPipe.send(new GetMapObjectsAction(googleMap.getProjection().getVisibleRegion().latLngBounds));
                    Observable.just(getHoldersMock())
                            .flatMap(getMapObjectsAction -> {
                                mapObjects = new ArrayList<>();
                                mapObjects.addAll(getMapObjectsAction.list);
                                return Observable.from(getMapObjectsAction.list)
                                        .map(mapObjectHolder -> new Pair<>(TripPinFactory
                                                .createPinBitmapFromMapObject(context, mapObjectHolder), mapObjectHolder.getItem()))
                                        .toList();
                            })
                            .subscribe(mapObjects -> {
                                if (this.tripMapCallback != null)
                                    this.tripMapCallback.onMapObjectsLoaded(mapObjects);
                            }, error -> {
                                Timber.e(error.getMessage());
                            });
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

        void onMapObjectsLoaded(List<Pair<Bitmap, MapObject>> mapObjects);

        void onTripsLoaded(List<TripModel> trips);

        void onClusterClicked(Cluster cluster);
    }

    private MockMapObjects getHoldersMock() {
        return gson.fromJson(Math.random() > 0.5 ? getJson() : getJson2(), MockMapObjects.class);
    }

    private String getJson() {
        return "{\"list\" : [ \n" +
                "      {\n" +
                "        \"type\" : \"pin\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 58.464717,\n" +
                "                    \"lng\": 35.046183\n" +
                "                  },\n" +
                "                  \"has_welcome_trips\": false,\n" +
                "                  \"trip_uids\" : [\"1234\", \"aabb2345\"]               \n" +
                "            }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\" : \"cluster\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 48.464717,\n" +
                "                    \"lng\": 35.046183\n" +
                "                  },\n" +
                "                  \"zoom_coordinates\": {\n" +
                "                        \"top_left\": {\n" +
                "                          \"lat\": 48.464717,\n" +
                "                          \"lng\": 35.046183\n" +
                "                        },\n" +
                "                        \"bottom_right\": {\n" +
                "                          \"lat\": -48.464717,\n" +
                "                          \"lng\": -35.046183\n" +
                "                        }\n" +
                "                  },\n" +
                "                  \"trip_count\": 5\n" +
                "            }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\" : \"pin\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 28.464717,\n" +
                "                    \"lng\": 35.046183\n" +
                "                  },\n" +
                "                  \"has_welcome_trips\": false,\n" +
                "                  \"trip_uids\" : [\"1234\", \"aabb2345\"]               \n" +
                "            }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\" : \"cluster\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 18.464717,\n" +
                "                    \"lng\": 25.046183\n" +
                "                  },\n" +
                "                  \"zoom_coordinates\": {\n" +
                "                        \"top_left\": {\n" +
                "                          \"lat\": 18.464717,\n" +
                "                          \"lng\": 25.046183\n" +
                "                        },\n" +
                "                        \"bottom_right\": {\n" +
                "                          \"lat\": -48.464717,\n" +
                "                          \"lng\": -35.046183\n" +
                "                        }\n" +
                "                  },\n" +
                "                  \"trip_count\": 5\n" +
                "            }\n" +
                "      }\n" +
                "]}";
    }

    private String getJson2() {
        return "{\"list\" : [ \n" +
                "      {\n" +
                "        \"type\" : \"pin\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 88.464717,\n" +
                "                    \"lng\": 65.046183\n" +
                "                  },\n" +
                "                  \"has_welcome_trips\": false,\n" +
                "                  \"trip_uids\" : [\"1234\", \"aabb2345\"]               \n" +
                "            }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\" : \"cluster\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 48.464717,\n" +
                "                    \"lng\": 75.046183\n" +
                "                  },\n" +
                "                  \"zoom_coordinates\": {\n" +
                "                        \"top_left\": {\n" +
                "                          \"lat\": 48.464717,\n" +
                "                          \"lng\": 35.046183\n" +
                "                        },\n" +
                "                        \"bottom_right\": {\n" +
                "                          \"lat\": -48.464717,\n" +
                "                          \"lng\": -35.046183\n" +
                "                        }\n" +
                "                  },\n" +
                "                  \"trip_count\": 5\n" +
                "            }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\" : \"pin\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 28.464717,\n" +
                "                    \"lng\": 55.046183\n" +
                "                  },\n" +
                "                  \"has_welcome_trips\": false,\n" +
                "                  \"trip_uids\" : [\"1234\", \"aabb2345\"]               \n" +
                "            }\n" +
                "      },\n" +
                "      {\n" +
                "        \"type\" : \"cluster\",\n" +
                "            \"item\" : {\n" +
                "                  \"coordinates\": {\n" +
                "                    \"lat\": 78.464717,\n" +
                "                    \"lng\": 25.046183\n" +
                "                  },\n" +
                "                  \"zoom_coordinates\": {\n" +
                "                        \"top_left\": {\n" +
                "                          \"lat\": 18.464717,\n" +
                "                          \"lng\": 25.046183\n" +
                "                        },\n" +
                "                        \"bottom_right\": {\n" +
                "                          \"lat\": -48.464717,\n" +
                "                          \"lng\": -35.046183\n" +
                "                        }\n" +
                "                  },\n" +
                "                  \"trip_count\": 5\n" +
                "            }\n" +
                "      }\n" +
                "]}";
    }

    private class MockMapObjects {

        public List<MapObjectHolder> list;

    }
}
