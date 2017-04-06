package com.worldventures.dreamtrips.api.tests.smoke.dtl;

import com.worldventures.dreamtrips.api.dtl.locations.LocationsHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("DT Local")
public class LocationsTest extends BaseTestWithSession {

    @Test
    void testGetLocationsByQuery() {
        LocationsHttpAction action = new LocationsHttpAction("Dal", null);

        checkLocationResponse(execute(action));
    }

    @Test
    void testGetLocationsByCoordinates() {
        LocationsHttpAction action = new LocationsHttpAction(null, "51.50,0");

        checkLocationResponse(execute(action));
    }

    private void checkLocationResponse(LocationsHttpAction action) {
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.locations()).isNotEmpty();
    }
}
