package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.ysbh.GetYSBHPhotosHttpAction;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features({"Photos", "YSBH"})
public class YSBHTests extends BaseTestWithSession {

    @Test()
    void testGetYSHBPhotos() {
        GetYSBHPhotosHttpAction action = execute(new GetYSBHPhotosHttpAction(1, 20));

        assertThat(action.response()).isNotEmpty();
    }
}
