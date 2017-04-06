package com.worldventures.dreamtrips.api.tests.feed;

import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;
import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;

import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Bucket List"})
public class BucketItemFeedTest extends BaseFeedTest {

    List<FeedItem> feedItems;
    FeedItem feedItem;
    BucketItemSocialized feedBucketItem;
    BucketItemSocialized bucketItem;

    @Test
    void testBucketItemHasValidData() {
        // TODO remove version when problem of testing with different client version will be solved
        feedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItem(feedItems, BaseEntityHolder.Type.BUCKET_LIST_ITEM);

        if (feedItem != null) {
            assertThat(feedItem.entity())
                    .as("Feed item entity can never be null")
                    .isNotNull().isInstanceOf(BucketItemSocialized.class);
            feedBucketItem = (BucketItemSocialized) feedItem.entity();
            bucketItem = getBucketItem(feedBucketItem.uid());

            assertThat(feedItem.action())
                    .as("Bucket list item can be shared or liked")
                    .isIn(FeedItem.Action.SHARE, FeedItem.Action.LIKE);
            assertThat(feedBucketItem.equals(bucketItem));
            assertFeedItemIsNotNotificationItem(feedItem);
        } else {
            throw new SkipException("Skipping test because there are no bucket items in the feed response");
        }
    }

    @Test(dependsOnMethods = "testBucketItemHasValidData")
    void testBucketItemLinkedUser() {
        assertThat(feedItem.links().users())
                .as("Any feed item(uid<%s>) with bucket item must have linked user", feedBucketItem.uid())
                .isNotEmpty();

        ShortUserProfile feedItemLinkedUser = feedItem.links().users().get(0);
        ShortUserProfile feedBucketItemAuthor = feedBucketItem.author();
        ShortUserProfile bucketItemAuthor = bucketItem.author();

        assertThat(feedItemLinkedUser.equals(bucketItemAuthor));
        assertThat(feedBucketItemAuthor.equals(bucketItemAuthor));
    }

    @Test(dependsOnMethods = "testBucketItemHasValidData")
    void testBucketItemType() {
        assertThat(feedBucketItem.type()).isNotEqualTo(BucketType.UNKNOWN);

        if (feedBucketItem.type() == BucketType.ACTIVITY) {
            assertThat(feedBucketItem.activity())
                    .as("Feed item of type activity must have not empty activity field ")
                    .isNotNull();
        } else if (feedBucketItem.type() == BucketType.LOCATION) {
            assertThat(feedBucketItem.location())
                    .as("Feed item of type location must have not empty location field")
                    .isNotNull();
        } else if (feedBucketItem.type() == BucketType.DINING) {
            assertThat(feedBucketItem.dining())
                    .as("Feed item of type dining must have not empty dining field")
                    .isNotNull();
        }
    }
}
