package com.worldventures.dreamtrips.modules.trips.api;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLngBounds;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.util.TripsFilterData;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/trip_clusters", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.GET)
public class GetMapObjectsAction extends BaseHttpAction {

    @Query("query")
    String query;
    @Query("duration_min")
    Integer durationMin;
    @Query("duration_max")
    Integer durationMax;
    @Query("price_min")
    Double priceMin;
    @Query("price_max")
    Double priceMax;
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
    @Query("top_left_lat")
    double top_left_lat;
    @Query("bottom_right_lat")
    double bottom_right_lat;
    @Query("top_left_lng")
    double top_left_lng;
    @Query("bottom_right_lng")
    double bottom_right_lng;

    @Response
    List<MapObjectHolder> mapObjects;

    public GetMapObjectsAction(TripsFilterData tripsFilterData, LatLngBounds latLngBounds, String q) {
        top_left_lat = latLngBounds.northeast.latitude;
        top_left_lng = latLngBounds.southwest.longitude;
        bottom_right_lat = latLngBounds.southwest.latitude;
        bottom_right_lng = latLngBounds.northeast.longitude;

        query = TextUtils.isEmpty(q) ? null : q;
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
        if (mapObjects == null) {
            mapObjects = new ArrayList<>();
        }
        return mapObjects;
    }
}
