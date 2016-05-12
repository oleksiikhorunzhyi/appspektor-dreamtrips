package com.worldventures.dreamtrips.modules.trips.api;

import com.google.android.gms.maps.model.LatLngBounds;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.util.TripsFilterData;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

//TODO change endpoint
@HttpAction(value = "/api/users/profiles/short", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.GET)
public class GetMapObjectsAction extends BaseHttpAction {

    @Query("query")
    String query;
    @Query("duration_min")
    long durationMin;
    @Query("duration_max")
    long durationMax;
    @Query("price_min")
    double priceMin;
    @Query("price_max")
    double priceMax;
    @Query("start_date")
    String startDate;
    @Query("end_date")
    String endDate;
    @Query("regions")
    String regions;
    @Query("activities")
    String activities;
    @Query("sold_out")
    int soldOut;
    @Query("recent")
    int recent;
    @Query("liked")
    int liked;

    @Query("top_left")
    MapPoint topLeftPoint;
    @Query("bottom_right")
    MapPoint bottomRightPoint;

    @Response
    List<MapObjectHolder> mapObjects;

    public GetMapObjectsAction(TripsFilterData tripsFilterData, LatLngBounds latLngBounds, String query) {
        topLeftPoint = new MapPoint(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude);
        bottomRightPoint = new MapPoint(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude);

        this.query = query;
        durationMin = tripsFilterData.getMinNights();
        durationMax = tripsFilterData.getMaxNights();
        priceMin = tripsFilterData.getMinPrice();
        priceMax = tripsFilterData.getMaxPrice();
        startDate = tripsFilterData.getStartDate();
        endDate = tripsFilterData.getEndDate();
        regions = tripsFilterData.getAcceptedRegions();
        activities = tripsFilterData.getAcceptedActivities();
        soldOut = tripsFilterData.isShowSoldOut();
        recent = tripsFilterData.isShowRecentlyAdded();
        liked = tripsFilterData.isShowFavorites();
    }

    public List<MapObjectHolder> getMapObjects() {
        return mapObjects;
    }

    private static class MapPoint {

        private double lat;
        private double lng;

        public MapPoint(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}
