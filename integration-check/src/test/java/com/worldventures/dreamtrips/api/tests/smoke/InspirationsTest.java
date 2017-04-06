package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.inspirations.GetInspireMePhotosHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features({"Photos", "Inspirations"})
public class InspirationsTest extends BaseTestWithSession {

    @Test()
    void testGetInspireMePhotos() {
        GetInspireMePhotosHttpAction action = execute(new GetInspireMePhotosHttpAction(Math.random(), 1, 20));

        assertThat(action.response()).isNotEmpty();
    }
}
