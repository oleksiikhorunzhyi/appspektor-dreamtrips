package com.worldventures.dreamtrips.modules.trips.api;

import com.google.android.gms.maps.model.LatLngBounds;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

//TODO change endpoint
@HttpAction(value = "/api/users/profiles/short", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class GetMapObjectsAction extends BaseHttpAction {

    @Body MapObjectsBody mapObjectsBody;

    @Query("query") String query;
    @Query("duration_min") long durationMin;
    @Query("duration_max") long durationMax;
    @Query("price_min") double priceMin;
    @Query("price_max") double priceMax;
    @Query("start_date") String startDate;
    @Query("end_date") String endDate;
    @Query("regions") String regions;
    @Query("activities") String activities;
    @Query("sold_out") int soldOut;
    @Query("recent") int recent;
    @Query("liked") int liked;

    @Response List<MapObjectHolder> mapObjects;

    public GetMapObjectsAction(LatLngBounds latLngBounds) {
        mapObjectsBody = new MapObjectsBody(latLngBounds.northeast.latitude, latLngBounds.southwest.longitude,
                latLngBounds.southwest.latitude, latLngBounds.northeast.longitude);
    }

    public List<MapObjectHolder> getMapObjects() {
        return mapObjects;
    }

    private class MapObjectsBody {

        private MapPoint topLeft;
        private MapPoint bottomRight;

        public MapObjectsBody(double topLeftLat, double topLeftLng, double bottomRightLat, double bottomRightLng) {
            this.topLeft = new MapPoint(topLeftLat, topLeftLng);
            this.bottomRight = new MapPoint(bottomRightLat, bottomRightLng);
        }

        private class MapPoint {

            private double lat;
            private double lng;

            public MapPoint(double lat, double lng) {
                this.lat = lat;
                this.lng = lng;
            }
        }
    }
}
