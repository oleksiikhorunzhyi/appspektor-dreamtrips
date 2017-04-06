package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.podcasts.GetPodcastsHttpAction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Podcasts")
public class PodcastsTest extends BaseTestWithSession {

    @Test
    void testGetPodcasts() {
        GetPodcastsHttpAction action = execute(new GetPodcastsHttpAction(1, 20));
        assertThat(action.response()).isNotEmpty();
    }
}
