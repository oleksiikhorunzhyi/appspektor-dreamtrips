package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.feed.GetTimelineHttpAction;
import com.worldventures.dreamtrips.api.feed.GetUserTimelineHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetTimelineHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetUserTimelineHttpAction;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.assertj.core.api.Condition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Feed")
public class TimelineTest extends BaseTestWithSession {

    @Fixture("user_feed_params")
    ImmutableGetUserTimelineHttpAction.Params userParams;
    @Fixture("account_feed_params")
    ImmutableGetTimelineHttpAction.Params accountParams;

    @BeforeClass
    void setupParams() {
        Date now = new Date();

        userParams = ImmutableGetUserTimelineHttpAction.Params.builder()
                .from(userParams)
                .before(now)
                .build();

        accountParams = ImmutableGetTimelineHttpAction.Params.builder()
                .from(accountParams)
                .before(now)
                .build();
    }

    @Test
    void testGetUserTimeline() {
        GetUserTimelineHttpAction.Params params = userParams.withUserId(session().user().id());
        List<FeedItem> feedItems = execute(new GetUserTimelineHttpAction(params)).response();
        assertThat(feedItems)
                .isNotEmpty()
                .extracting(FeedItem::createdAt)
                .are(new Condition<>(date -> date.before(userParams.before()), "Item created before params date"));
    }

    @Test
    void testGetAccountTimeline() {
        List<FeedItem> feedItems = execute(new GetTimelineHttpAction(accountParams)).response();
        assertThat(feedItems)
                .isNotEmpty()
                .extracting(FeedItem::createdAt)
                .are(new Condition<>(date -> date.before(accountParams.before()), "Item created before params date"));
    }

}
