package com.worldventures.dreamtrips.modules.trips.api;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.TripQueryData;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/trips")
public class GetTripsHttpAction extends AuthorizedHttpAction {

   @Query("page") int page;
   @Query("per_page") int perPage;
   @Query("query") String query;
   @Query("duration_min") Integer durationMin;
   @Query("duration_max") Integer durationMax;
   @Query("price_min") Double priceMin;
   @Query("price_max") Double priceMax;
   @Query("start_date") String startDate;
   @Query("end_date") String endDate;
   @Query("regions") String regions;
   @Query("activities") String activities;
   @Query("sold_out") Integer soldOut;
   @Query("recent") int recent;
   @Query("liked") int liked;

   @Response List<TripModel> trips;

   public GetTripsHttpAction(TripQueryData tripQueryData) {
      page = tripQueryData.getPage();
      perPage = tripQueryData.getPerPage();
      query = tripQueryData.getQuery();
      durationMin = tripQueryData.getDurationMin();
      durationMax = tripQueryData.getDurationMax();
      priceMin = tripQueryData.getPriceMin();
      priceMax = tripQueryData.getPriceMax();
      startDate = tripQueryData.getStartDate();
      endDate = tripQueryData.getEndDate();
      regions = tripQueryData.getRegions();
      activities = tripQueryData.getActivities();
      soldOut = tripQueryData.isSoldOut();
      recent = tripQueryData.isRecent();
      liked = tripQueryData.isLiked();
   }

   public List<TripModel> getResponseItems() {
      if (trips == null) trips = new ArrayList<>();
      return trips;
   }
}
