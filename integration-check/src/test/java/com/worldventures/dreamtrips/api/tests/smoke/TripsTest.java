package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.likes.DislikeHttpAction;
import com.worldventures.dreamtrips.api.likes.LikeHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.trip.GetTripActivitiesHttpAction;
import com.worldventures.dreamtrips.api.trip.GetTripHttpAction;
import com.worldventures.dreamtrips.api.trip.GetTripRegionsHttpAction;
import com.worldventures.dreamtrips.api.trip.GetTripsDetailsHttpAction;
import com.worldventures.dreamtrips.api.trip.GetTripsHttpAction;
import com.worldventures.dreamtrips.api.trip.GetTripsLocationsHttpAction;
import com.worldventures.dreamtrips.api.trip.ImmutableGetTripsHttpAction;
import com.worldventures.dreamtrips.api.trip.model.Trip;
import com.worldventures.dreamtrips.api.trip.model.TripPin;
import com.worldventures.dreamtrips.api.trip.model.TripWithoutDetails;

import org.assertj.core.api.Condition;
import org.testng.annotations.Test;

import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Trips")
public class TripsTest extends BaseTestWithSession {

    @Fixture("trip_params_empty")
    GetTripsHttpAction.Params emptyParams;
    @Fixture("trip_params_filtered")
    GetTripsHttpAction.Params filteredParams;
    @Fixture("trip_params_filter_all")
    GetTripsHttpAction.Params filteredAllParams;
    @Fixture("trips_locations_params")
    GetTripsLocationsHttpAction.Params locationParams;
    @Fixture("trip_locations_params_filter_all")
    GetTripsLocationsHttpAction.Params locationAllParams;

    private volatile List<TripWithoutDetails> trips;
    private volatile List<TripPin> pins;
    private volatile Trip likedTrip;

    @Test
    void testGetTrips() {
        GetTripsHttpAction action = execute(new GetTripsHttpAction(emptyParams));
        assertThat(action.statusCode()).isEqualTo(200);
        trips = action.response();
        assertThat(trips)
                .as("Trips must exist in the response")
                .isNotEmpty()
                .extracting(Trip::rewardRules)
                // rewardRules are always null, see DT-2881
                .are(new Condition<>(rr -> rr.size() == 0, "always null in the API response"));

        // check pagination
        assertThat(trips.stream().count()).isLessThanOrEqualTo(emptyParams.perPage());
    }

    @Test
    void testGetTripsFilteredByDuration() {
        GetTripsHttpAction action = execute(new GetTripsHttpAction(filteredParams));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response())
                .as("Trips must exist in the response")
                .isNotEmpty()
                .extracting(Trip::duration)
                .are(new Condition<>(d -> d >= filteredParams.durationMin(), "With minimal duration"));
    }

    @Test(dependsOnMethods = "testGetTrips")
    void testGetTripsFilteredByQuery() {
        ImmutableGetTripsHttpAction.Params params = ImmutableGetTripsHttpAction.Params.builder()
                .from(emptyParams)
                .query(trips.get(0).name().substring(0,1).toLowerCase())
                .build();
        GetTripsHttpAction action = execute(new GetTripsHttpAction(params));
        assertThat(action.statusCode()).isEqualTo(200);
        // check only if there are trips in response
        assertThat(action.response())
            .are(new Condition<>(
                t -> t.name().toLowerCase().contains(params.query()) ||
                t.description().toLowerCase().contains(params.query()) ||
                t.location().name().toLowerCase().contains(params.query()),
                "name, description or location name must contain queried substring"
            ));
    }

    @Test
    void testGetTripsAllFiltersTriggered() {
        GetTripsHttpAction action = execute(new GetTripsHttpAction(filteredAllParams));
        assertThat(action.statusCode()).isEqualTo(200);
    }

    @Test(dependsOnMethods = "testGetTrips")
    void testGetTripDetails() {
        GetTripHttpAction action = execute(new GetTripHttpAction(trips.get(0).uid()));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();
        assertThat(action.response().contentItems()).isNotEmpty();
    }

    @Test(dependsOnMethods = "testGetTrips")
    void testLikeTrip() {
        likedTrip = trips.stream().filter(t -> !t.liked()).findAny().get();
        LikeHttpAction action = execute(new LikeHttpAction(likedTrip.uid()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = "testLikeTrip")
    void testUnlikeTrip() {
        DislikeHttpAction action = execute(new DislikeHttpAction(likedTrip.uid()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test
    void testGetTripRegions() {
        GetTripRegionsHttpAction action = execute(new GetTripRegionsHttpAction());
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotEmpty();
    }

    @Test
    void testGetTripActivities() {
        GetTripActivitiesHttpAction action = execute(new GetTripActivitiesHttpAction());
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotEmpty();
    }

    @Test
    void testGetTripsLocationsFilteredByDuration() {
        GetTripsLocationsHttpAction action = execute(new GetTripsLocationsHttpAction(locationParams));
        assertThat(action.statusCode()).isEqualTo(200);
        pins = action.mappedResponse();
        assertThat(pins)
                .as("Trips must exist in the response")
                .isNotEmpty()
                .extracting(TripPin::tripsUids).isNotEmpty();
    }

    @Test
    void testGetTripsLocationsWithFilters() {
        GetTripsLocationsHttpAction action = execute(new GetTripsLocationsHttpAction(locationAllParams));
        assertThat(action.statusCode()).isEqualTo(200);
    }

    @Test(dependsOnMethods = "testGetTripsLocationsFilteredByDuration")
    void testGetTripsDetails() {
        GetTripsDetailsHttpAction action = execute(new GetTripsDetailsHttpAction(pins.get(0).tripsUids()));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response())
                .as("Details for trips: %s must exist", pins.get(0).tripsUids().toString())
                .isNotNull()
                .hasSameSizeAs(pins.get(0).tripsUids());
    }

}
