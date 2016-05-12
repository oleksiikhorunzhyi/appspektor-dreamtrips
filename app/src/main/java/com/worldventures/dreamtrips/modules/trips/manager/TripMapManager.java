package com.worldventures.dreamtrips.modules.trips.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.Coordinates;
import com.worldventures.dreamtrips.modules.map.reactive.MapObservableFactory;
import com.worldventures.dreamtrips.modules.trips.api.GetDetailedTripsAction;
import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsAction;
import com.worldventures.dreamtrips.modules.trips.model.Cluster;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
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
    private TripFilterDataProvider tripFilterDataProvider;
    private List<Marker> existsMarkers;

    public TripMapManager(Janet janet, Context context, Gson gson, TripFilterDataProvider tripFilterDataProvider) {
        this.context = context;
        this.gson = gson;
        this.tripFilterDataProvider = tripFilterDataProvider;
        mapObjectsActionPipe = janet.createPipe(GetMapObjectsAction.class, Schedulers.io());
        detailedTripsActionPipe = janet.createPipe(GetDetailedTripsAction.class, Schedulers.io());
        subscriptions = new CompositeSubscription();
    }

    public void subscribe(GoogleMap googleMap, Callback tripMapCallback) {
        this.tripMapCallback = tripMapCallback;
        existsMarkers = new ArrayList<>();
        //
        subscriptions.add(subscribeToCameraChanges(googleMap));
        subscriptions.add(subscribeToPinClicks(googleMap));
        //
        subscriptions.add(mapObjectsActionPipe.observeSuccess()
                .doOnNext(getMapObjectsAction -> mapObjects = getMapObjectsAction.getMapObjects())
                .flatMap(this::convertToPinPair)
                .doOnNext(this::removeOldMarkers)
                .flatMap(this::filterExistsMarkers)
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
        existsMarkers = null;
    }

    public void addMarker(Marker marker) {
        existsMarkers.add(marker);
    }

    public void removeAlphaFromMarkers() {
        Queryable.from(existsMarkers).forEachR(existMarker -> existMarker.setAlpha(1f));
    }

    private Subscription subscribeToCameraChanges(GoogleMap googleMap) {
        return MapObservableFactory.createCameraChangeObservable(googleMap)
                .throttleLast(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cameraPosition -> {

                    // todo query handler
                    // GetMapObjectsAction action = new GetMapObjectsAction(tripFilterDataProvider.get(), googleMap.getProjection().getVisibleRegion().latLngBounds, "");
                    // mapObjectsActionPipe.send(action);
                    Observable.just(getHoldersMock())
                            .flatMap(getMapObjectsAction -> {
                                mapObjects = new ArrayList<>();
                                mapObjects.addAll(getMapObjectsAction.list);
                                return Observable.from(getMapObjectsAction.list)
                                        .map(mapObjectHolder -> new Pair<>(TripPinFactory
                                                .createPinBitmapFromMapObject(context, mapObjectHolder), mapObjectHolder.getItem()))
                                        .toList();
                            })
                            .doOnNext(this::removeOldMarkers)
                            .flatMap(this::filterExistsMarkers)
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
//                            detailedTripsActionPipe.send(new GetDetailedTripsAction(((Pin) holder.getItem()).getTripUids()));
                            addAlphaToMarkers(marker);
                            tripMapCallback.onTripsLoaded(getTripMock().trips);
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
    }

    private MockTripObject getTripMock() {
        return gson.fromJson(Math.random() > 0.5 ? getTripJson() : getTripJsonMult(), MockTripObject.class);
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
                "                  \"trip_uids\" : [\"pjsZk7BboC\", \"kHltu0H9pQ\"]               \n" +
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
                "                  \"trip_uids\" : [\"pjsZk7BboC\", \"kHltu0H9pQ\"]               \n" +
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
                "                  \"trip_uids\" : [\"pjsZk7BboC\", \"kHltu0H9pQ\"]               \n" +
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
                "                  \"trip_uids\" : [\"pjsZk7BboC\", \"kHltu0H9pQ\"]               \n" +
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

    private String getTripJson() {
        return "{\"trips\":[" +
                "{\"id\":1503,\"uid\":\"gxrVFvZmDy\",\"trip_id\":\"025bc79e-8d10-412e-951f-e9044b917ed0\",\"name\":\"July Fourth in Boston\",\"duration\":3,\"rewards_limit\":250,\"dates\":{\"start_on\":\"2016-07-03\",\"end_on\":\"2016-07-06\"},\"featured\":false,\"rewarded\":true,\"platinum\":false,\"available\":true,\"sold_out\":true,\"description\":\"Celebrate Independence Day in the perfect place: Out on the waters of Boston Harbor!\",\"liked\":false,\"likes_count\":0,\"regions\":[{\"id\":198,\"name\":\"North America\"}],\"activities\":[{\"id\":2038,\"name\":\"Arts & Culture\",\"parent_id\":0,\"position\":2,\"icon\":null},{\"id\":2075,\"name\":\"Platinum Experiences\",\"parent_id\":0,\"position\":8,\"icon\":null},{\"id\":2078,\"name\":\"Points of Interest\",\"parent_id\":0,\"position\":9,\"icon\":null}],\"region\":{\"id\":198,\"name\":\"North America\"},\"rewards_rules\":{\"DTP\":\"300\"},\"images\":[{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300146_Boston_1607030_HR_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=500&height=500\"},\"id\":\"c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300661_Boston_1607030_HR_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/\n" +
                "05-12 12:39:46.652 D/Retrofit: trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=500&height=500\"},\"id\":\"a39e467788329a12b835cb50c12e625c84300030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104301130_Boston_1607030_HR_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=500&height=500\"},\"id\":\"ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246536_Boston_1607030_LN_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=500&height=500\"},\"id\":\"ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246708_Boston_1607030_LN_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=500&height=500\"},\"id\":\"28dea2551b9909c8191050c56ee98203f10aa499\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246333_Boston_1607030_LN_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=500&height=500\"},\"id\":\"f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246880_Boston_1607030_LN_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1\n" +
                "05-12 12:39:46.653 D/Retrofit: ab68c0a6?width=500&height=500\"},\"id\":\"9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB|RETINA\"}],\"price\":{\"amount\":549,\"currency\":\"USD\"},\"in_bucket_list\":false,\"created_at\":{\"date\":\"2015-11-05 21:31:25.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"updated_at\":{\"date\":\"2015-11-05 22:03:18.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"location_id\":715,\"recent\":false,\"location\":{\"name\":\"Boston, Massachusetts, United States\",\"lat\":\"42.35843000\",\"lng\":\"-71.05977000\"},\"has_multiple_dates\":false}" +
                "]}";
    }

    private String getTripJsonMult() {
        return "{\"trips\":[\n" +
                "{\"id\":1503,\"uid\":\"gxrVFvZmDy\",\"trip_id\":\"025bc79e-8d10-412e-951f-e9044b917ed0\",\"name\":\"July Fourth in Boston\",\"duration\":3,\"rewards_limit\":250,\"dates\":{\"start_on\":\"2016-07-03\",\"end_on\":\"2016-07-06\"},\"featured\":true,\"rewarded\":true,\"platinum\":false,\"available\":true,\"sold_out\":false,\"description\":\"Celebrate Independence Day in the perfect place: Out on the waters of Boston Harbor!\",\"liked\":false,\"likes_count\":0,\"regions\":[{\"id\":198,\"name\":\"North America\"}],\"activities\":[{\"id\":2038,\"name\":\"Arts & Culture\",\"parent_id\":0,\"position\":2,\"icon\":null},{\"id\":2075,\"name\":\"Platinum Experiences\",\"parent_id\":0,\"position\":8,\"icon\":null},{\"id\":2078,\"name\":\"Points of Interest\",\"parent_id\":0,\"position\":9,\"icon\":null}],\"region\":{\"id\":198,\"name\":\"North America\"},\"rewards_rules\":{\"DTP\":\"300\"},\"images\":[{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300146_Boston_1607030_HR_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=500&height=500\"},\"id\":\"c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300661_Boston_1607030_HR_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/\n" +
                "05-12 12:39:46.652 D/Retrofit: trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=500&height=500\"},\"id\":\"a39e467788329a12b835cb50c12e625c84300030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104301130_Boston_1607030_HR_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=500&height=500\"},\"id\":\"ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246536_Boston_1607030_LN_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=500&height=500\"},\"id\":\"ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246708_Boston_1607030_LN_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=500&height=500\"},\"id\":\"28dea2551b9909c8191050c56ee98203f10aa499\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246333_Boston_1607030_LN_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=500&height=500\"},\"id\":\"f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246880_Boston_1607030_LN_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1\n" +
                "05-12 12:39:46.653 D/Retrofit: ab68c0a6?width=500&height=500\"},\"id\":\"9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB|RETINA\"}],\"price\":{\"amount\":549,\"currency\":\"USD\"},\"in_bucket_list\":false,\"created_at\":{\"date\":\"2015-11-05 21:31:25.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"updated_at\":{\"date\":\"2015-11-05 22:03:18.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"location_id\":715,\"recent\":false,\"location\":{\"name\":\"Boston, Massachusetts, United States\",\"lat\":\"42.35843000\",\"lng\":\"-71.05977000\"},\"has_multiple_dates\":false}, " +
                "{\"id\":1503,\"uid\":\"gxrVFvZmDy\",\"trip_id\":\"025bc79e-8d10-412e-951f-e9044b917ed0\",\"name\":\"July Fourth in Boston\",\"duration\":3,\"rewards_limit\":250,\"dates\":{\"start_on\":\"2016-07-03\",\"end_on\":\"2016-07-06\"},\"featured\":false,\"rewarded\":true,\"platinum\":false,\"available\":true,\"sold_out\":false,\"description\":\"Celebrate Independence Day in the perfect place: Out on the waters of Boston Harbor!\",\"liked\":false,\"likes_count\":0,\"regions\":[{\"id\":198,\"name\":\"North America\"}],\"activities\":[{\"id\":2038,\"name\":\"Arts & Culture\",\"parent_id\":0,\"position\":2,\"icon\":null},{\"id\":2075,\"name\":\"Platinum Experiences\",\"parent_id\":0,\"position\":8,\"icon\":null},{\"id\":2078,\"name\":\"Points of Interest\",\"parent_id\":0,\"position\":9,\"icon\":null}],\"region\":{\"id\":198,\"name\":\"North America\"},\"rewards_rules\":{\"DTP\":\"300\"},\"images\":[{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300146_Boston_1607030_HR_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=500&height=500\"},\"id\":\"c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300661_Boston_1607030_HR_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/\n" +
                "05-12 12:39:46.652 D/Retrofit: trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=500&height=500\"},\"id\":\"a39e467788329a12b835cb50c12e625c84300030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104301130_Boston_1607030_HR_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=500&height=500\"},\"id\":\"ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246536_Boston_1607030_LN_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=500&height=500\"},\"id\":\"ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246708_Boston_1607030_LN_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=500&height=500\"},\"id\":\"28dea2551b9909c8191050c56ee98203f10aa499\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246333_Boston_1607030_LN_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=500&height=500\"},\"id\":\"f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246880_Boston_1607030_LN_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1\n" +
                "05-12 12:39:46.653 D/Retrofit: ab68c0a6?width=500&height=500\"},\"id\":\"9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB|RETINA\"}],\"price\":{\"amount\":549,\"currency\":\"USD\"},\"in_bucket_list\":false,\"created_at\":{\"date\":\"2015-11-05 21:31:25.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"updated_at\":{\"date\":\"2015-11-05 22:03:18.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"location_id\":715,\"recent\":false,\"location\":{\"name\":\"Boston, Massachusetts, United States\",\"lat\":\"42.35843000\",\"lng\":\"-71.05977000\"},\"has_multiple_dates\":false}," +
                "{\"id\":1503,\"uid\":\"gxrVFvZmDy\",\"trip_id\":\"025bc79e-8d10-412e-951f-e9044b917ed0\",\"name\":\"July Fourth in Boston\",\"duration\":3,\"rewards_limit\":250,\"dates\":{\"start_on\":\"2016-07-03\",\"end_on\":\"2016-07-06\"},\"featured\":false,\"rewarded\":true,\"platinum\":false,\"available\":true,\"sold_out\":false,\"description\":\"Celebrate Independence Day in the perfect place: Out on the waters of Boston Harbor!\",\"liked\":false,\"likes_count\":0,\"regions\":[{\"id\":198,\"name\":\"North America\"}],\"activities\":[{\"id\":2038,\"name\":\"Arts & Culture\",\"parent_id\":0,\"position\":2,\"icon\":null},{\"id\":2075,\"name\":\"Platinum Experiences\",\"parent_id\":0,\"position\":8,\"icon\":null},{\"id\":2078,\"name\":\"Points of Interest\",\"parent_id\":0,\"position\":9,\"icon\":null}],\"region\":{\"id\":198,\"name\":\"North America\"},\"rewards_rules\":{\"DTP\":\"300\"},\"images\":[{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300146_Boston_1607030_HR_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=500&height=500\"},\"id\":\"c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300661_Boston_1607030_HR_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/\n" +
                "05-12 12:39:46.652 D/Retrofit: trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=500&height=500\"},\"id\":\"a39e467788329a12b835cb50c12e625c84300030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104301130_Boston_1607030_HR_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=500&height=500\"},\"id\":\"ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246536_Boston_1607030_LN_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=500&height=500\"},\"id\":\"ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246708_Boston_1607030_LN_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=500&height=500\"},\"id\":\"28dea2551b9909c8191050c56ee98203f10aa499\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246333_Boston_1607030_LN_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=500&height=500\"},\"id\":\"f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246880_Boston_1607030_LN_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1\n" +
                "05-12 12:39:46.653 D/Retrofit: ab68c0a6?width=500&height=500\"},\"id\":\"9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB|RETINA\"}],\"price\":{\"amount\":549,\"currency\":\"USD\"},\"in_bucket_list\":false,\"created_at\":{\"date\":\"2015-11-05 21:31:25.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"updated_at\":{\"date\":\"2015-11-05 22:03:18.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"location_id\":715,\"recent\":false,\"location\":{\"name\":\"Boston, Massachusetts, United States\",\"lat\":\"42.35843000\",\"lng\":\"-71.05977000\"},\"has_multiple_dates\":false}," +
                "{\"id\":1503,\"uid\":\"gxrVFvZmDy\",\"trip_id\":\"025bc79e-8d10-412e-951f-e9044b917ed0\",\"name\":\"July Fourth in Boston\",\"duration\":3,\"rewards_limit\":250,\"dates\":{\"start_on\":\"2016-07-03\",\"end_on\":\"2016-07-06\"},\"featured\":false,\"rewarded\":true,\"platinum\":false,\"available\":true,\"sold_out\":true,\"description\":\"Celebrate Independence Day in the perfect place: Out on the waters of Boston Harbor!\",\"liked\":false,\"likes_count\":0,\"regions\":[{\"id\":198,\"name\":\"North America\"}],\"activities\":[{\"id\":2038,\"name\":\"Arts & Culture\",\"parent_id\":0,\"position\":2,\"icon\":null},{\"id\":2075,\"name\":\"Platinum Experiences\",\"parent_id\":0,\"position\":8,\"icon\":null},{\"id\":2078,\"name\":\"Points of Interest\",\"parent_id\":0,\"position\":9,\"icon\":null}],\"region\":{\"id\":198,\"name\":\"North America\"},\"rewards_rules\":{\"DTP\":\"300\"},\"images\":[{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300146_Boston_1607030_HR_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=500&height=500\"},\"id\":\"c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300661_Boston_1607030_HR_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/\n" +
                "05-12 12:39:46.652 D/Retrofit: trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=500&height=500\"},\"id\":\"a39e467788329a12b835cb50c12e625c84300030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104301130_Boston_1607030_HR_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=500&height=500\"},\"id\":\"ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246536_Boston_1607030_LN_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=500&height=500\"},\"id\":\"ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246708_Boston_1607030_LN_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=500&height=500\"},\"id\":\"28dea2551b9909c8191050c56ee98203f10aa499\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246333_Boston_1607030_LN_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=500&height=500\"},\"id\":\"f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246880_Boston_1607030_LN_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1\n" +
                "05-12 12:39:46.653 D/Retrofit: ab68c0a6?width=500&height=500\"},\"id\":\"9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB|RETINA\"}],\"price\":{\"amount\":549,\"currency\":\"USD\"},\"in_bucket_list\":false,\"created_at\":{\"date\":\"2015-11-05 21:31:25.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"updated_at\":{\"date\":\"2015-11-05 22:03:18.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"location_id\":715,\"recent\":false,\"location\":{\"name\":\"Boston, Massachusetts, United States\",\"lat\":\"42.35843000\",\"lng\":\"-71.05977000\"},\"has_multiple_dates\":false}," +
                "{\"id\":1503,\"uid\":\"gxrVFvZmDy\",\"trip_id\":\"025bc79e-8d10-412e-951f-e9044b917ed0\",\"name\":\"July Fourth in Boston\",\"duration\":3,\"rewards_limit\":250,\"dates\":{\"start_on\":\"2016-07-03\",\"end_on\":\"2016-07-06\"},\"featured\":false,\"rewarded\":true,\"platinum\":false,\"available\":true,\"sold_out\":false,\"description\":\"Celebrate Independence Day in the perfect place: Out on the waters of Boston Harbor!\",\"liked\":false,\"likes_count\":0,\"regions\":[{\"id\":198,\"name\":\"North America\"}],\"activities\":[{\"id\":2038,\"name\":\"Arts & Culture\",\"parent_id\":0,\"position\":2,\"icon\":null},{\"id\":2075,\"name\":\"Platinum Experiences\",\"parent_id\":0,\"position\":8,\"icon\":null},{\"id\":2078,\"name\":\"Points of Interest\",\"parent_id\":0,\"position\":9,\"icon\":null}],\"region\":{\"id\":198,\"name\":\"North America\"},\"rewards_rules\":{\"DTP\":\"300\"},\"images\":[{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300146_Boston_1607030_HR_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/c350614c32ff6ae523e2c0ddf857a97aa5d45030?width=500&height=500\"},\"id\":\"c350614c32ff6ae523e2c0ddf857a97aa5d45030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104300661_Boston_1607030_HR_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/\n" +
                "05-12 12:39:46.652 D/Retrofit: trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/a39e467788329a12b835cb50c12e625c84300030?width=500&height=500\"},\"id\":\"a39e467788329a12b835cb50c12e625c84300030\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104301130_Boston_1607030_HR_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ee41db69642c66a7657760fbd0d5e15fcb6097ff?width=500&height=500\"},\"id\":\"ee41db69642c66a7657760fbd0d5e15fcb6097ff\",\"description\":\"\",\"type\":\"RETINA\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246536_Boston_1607030_LN_Detail2.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/ce8ed36df9aaf38db76f4d375536ee155cb57950?width=500&height=500\"},\"id\":\"ce8ed36df9aaf38db76f4d375536ee155cb57950\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246708_Boston_1607030_LN_Detail3.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/28dea2551b9909c8191050c56ee98203f10aa499?width=500&height=500\"},\"id\":\"28dea2551b9909c8191050c56ee98203f10aa499\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246333_Boston_1607030_LN_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/f1e218c3bff2668fb623f8cd1271eb8718b0d402?width=500&height=500\"},\"id\":\"f1e218c3bff2668fb623f8cd1271eb8718b0d402\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/5680db5d-80d8-4cb5-a3d2-2700707cd6a4/o_20151026104246880_Boston_1607030_LN_Detail4.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1ab68c0a6?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/9b77819d9d786c44b46831750604a8e1\n" +
                "05-12 12:39:46.653 D/Retrofit: ab68c0a6?width=500&height=500\"},\"id\":\"9b77819d9d786c44b46831750604a8e1ab68c0a6\",\"description\":\"\",\"type\":\"NORMAL\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB\"},{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\",\"origin_url\":\"http://media.wvhservices.com/worldventures/Albums/e7c39a9a-1455-4637-a9b0-0058305466f9/o_20151026104259614_Boston_1607030_HR_Detail1.jpg\",\"original\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e\"},\"medium\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=1920&height=1080\"},\"thumb\":{\"url\":\"http://techery-dt-staging-imagery.techery.io/trips/1503/images/7e2047978ccd61c294d4aacc4b852883f442320e?width=500&height=500\"},\"id\":\"7e2047978ccd61c294d4aacc4b852883f442320e\",\"description\":\"\",\"type\":\"THUMB|RETINA\"}],\"price\":{\"amount\":549,\"currency\":\"USD\"},\"in_bucket_list\":false,\"created_at\":{\"date\":\"2015-11-05 21:31:25.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"updated_at\":{\"date\":\"2015-11-05 22:03:18.000000\",\"timezone_type\":3,\"timezone\":\"UTC\"},\"location_id\":715,\"recent\":false,\"location\":{\"name\":\"Boston, Massachusetts, United States\",\"lat\":\"42.35843000\",\"lng\":\"-71.05977000\"},\"has_multiple_dates\":false}" +
                "]}";
    }

    private class MockTripObject {
        public List<TripModel> trips;
    }
}
