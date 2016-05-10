package com.worldventures.dreamtrips.modules.trips.api;

import com.google.android.gms.maps.model.LatLngBounds;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;

import java.util.List;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

//TODO change endpoint
@HttpAction(value = "/api/users/profiles/short", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class GetMapObjectsAction extends BaseHttpAction {

    @Body MapObjectsBody mapObjectsBody;

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
