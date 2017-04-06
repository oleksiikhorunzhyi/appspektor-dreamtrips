package com.worldventures.dreamtrips.api.trip;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripParams;
import com.worldventures.dreamtrips.api.trip.model.TripParamsAdapter;
import com.worldventures.dreamtrips.api.trip.model.TripPin;
import com.worldventures.dreamtrips.api.trip.model.TripPinWrapper;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@Value.Enclosing
@Gson.TypeAdapters
@HttpAction("/api/trips/locations")
public class GetTripsLocationsHttpAction extends AuthorizedHttpAction {

    @Query("query")
    public final String query;

    @Query("duration_min")
    public final Integer durationMin;

    @Query("duration_max")
    public final Integer durationMax;

    @Query("price_min")
    public final Double priceMin;

    @Query("price_max")
    public final Double priceMax;

    @Query("start_date")
    public final String startDate;

    @Query("end_date")
    public final String endDate;

    @Query("regions")
    public final String regions;

    @Query("activities")
    public final String activities;

    @Query("sold_out")
    public final Integer soldOut;

    @Query("liked")
    public final Integer liked;

    @Query("recent")
    public final Integer recentFirst;

    public GetTripsLocationsHttpAction(Params params) {
        TripParamsAdapter adapter = new TripParamsAdapter(params);
        query = adapter.query();
        durationMin = adapter.durationMin();
        durationMax = adapter.durationMax();
        priceMin = adapter.priceMin();
        priceMax = adapter.priceMax();
        startDate = adapter.startDate();
        endDate = adapter.endDate();
        regions = adapter.regions();
        activities = adapter.activities();
        soldOut = adapter.soldOut();
        liked = adapter.liked();
        recentFirst = adapter.recentFirst();
    }

    @Response
    List<TripPinWrapper> tripsPins;

    public List<TripPin> mappedResponse() {
        if (tripsPins == null) return null;
        else if (tripsPins.isEmpty()) return Collections.emptyList();
        //
        List<TripPin> pins = new ArrayList<TripPin>(tripsPins.size());
        for (TripPinWrapper pinWrapper : tripsPins) {
            pins.add(pinWrapper.item());
        }
        return pins;
    }

    public List<TripPinWrapper> response() {
        return tripsPins;
    }

    @Gson.TypeAdapters
    @Value.Immutable
    public interface Params extends TripParams {
    }

}
