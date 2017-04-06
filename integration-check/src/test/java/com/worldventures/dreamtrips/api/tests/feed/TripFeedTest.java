package com.worldventures.dreamtrips.api.tests.feed;

import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;
import com.worldventures.dreamtrips.api.profile.GetPublicUserProfileHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripWithDetails;

import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Trips"})
public class TripFeedTest extends BaseFeedTest {

    List<FeedItem> feedItems;
    FeedItem feedItem;

    @BeforeClass
    void initFeed() {
        feedItems = getFeedItems(APP_VERSION);
    }

    @Test
    void testTripHasValidData() {
        feedItem = findFeedItem(feedItems, BaseEntityHolder.Type.TRIP);

        if (feedItem == null) {
            throw new SkipException("Skipping test because there are no trips in the feed response");
        }

        assertThat(feedItem.entity())
                .as("Feed item entity can never be null")
                .isNotNull().isInstanceOf(TripWithDetails.class);
        TripWithDetails feedTrip = (TripWithDetails)feedItem.entity();
        TripWithDetails trip = getTrip(feedTrip.uid());

        assertThat(feedItem.action())
                .as("Trip can be liked only")
                .isEqualTo(FeedItem.Action.LIKE);
        assertThat(feedTrip.equals(trip));
        assertFeedItemIsNotNotificationItem(feedItem);
    }

    @Test(dependsOnMethods = "testTripHasValidData")
    void testTripLinkedUsers() {
        assertThat(feedItem.links().users())
                .as("Any feed item with liked trip must have linked user")
                .isNotEmpty();

        ShortUserProfile profile = feedItem.links().users().get(0);
        GetPublicUserProfileHttpAction action = execute(new GetPublicUserProfileHttpAction(profile.id()));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response().equals(profile));
    }
}
