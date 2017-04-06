package com.worldventures.dreamtrips.api.tests.feed;

import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketItemHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;
import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.feed.GetAccountFeedHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetAccountFeedHttpAction;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.photos.GetPhotoHttpAction;
import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;
import com.worldventures.dreamtrips.api.post.GetPostHttpAction;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;
import com.worldventures.dreamtrips.api.trip.GetTripHttpAction;
import com.worldventures.dreamtrips.api.trip.model.TripWithDetails;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseFeedTest extends BaseTestWithSession {

    public static final String APP_VERSION = "1.15.0";
    public static final String APP_VERSION_OLD = "1.9.0";

    Date before = DateTime.now().toDate();

    protected List<FeedItem> getFeedItems(String version) {
        ImmutableGetAccountFeedHttpAction.Params accountParams = ImmutableGetAccountFeedHttpAction.Params.builder()
                .before(before)
                .pageSize(30)
                .build();

        GetAccountFeedHttpAction action = new GetAccountFeedHttpAction(accountParams);
        action.setAppVersionHeader(version);
        action = execute(action);
        assertThat(action.statusCode()).isEqualTo(200);

        List<FeedItem> feedItems = action.response();
        assertThat(feedItems)
                .as("Feed of user<%s> must not be empty", authorizedUser().username())
                .isNotEmpty();

        return feedItems;
    }

    @Nullable
    protected FeedItem findFeedItem(List<FeedItem> feedItems, BaseEntityHolder.Type type) {
        Optional<FeedItem> optionalFeedItem = feedItems
                .stream()
                .filter(feedItem -> feedItem.type() == type)
                .findAny();

        return optionalFeedItem.isPresent() ? optionalFeedItem.get() : null;
    }

    protected FeedItem findFeedItemByUid(List<FeedItem> feedItems, String uid) {
        Optional<FeedItem> optionalFeedItem = feedItems
                .stream()
                .filter(feedItem -> ((UniqueIdentifiable) feedItem.entity()).uid().equals(uid))
                .findAny();

        assertThat(optionalFeedItem.isPresent())
                .as("Check feed item with uid<%s> exists in the feed response", uid)
                .isTrue();

        return optionalFeedItem.get();
    }

    @Nullable
    protected PostSocialized getPost(String uid) {
        GetPostHttpAction action = execute(new GetPostHttpAction(uid));
        assertThat(action.statusCode()).isEqualTo(200);

        assertThat(action.response())
                .as("Post with uid<%s> must exist", uid)
                .isNotNull()
                .isInstanceOf(PostSocialized.class);

        return action.response();
    }

    protected PhotoSocialized getPhoto(String uid) {
        GetPhotoHttpAction action = execute(new GetPhotoHttpAction(uid));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response())
                .as("Photo with uid<%s> must exist", uid)
                .isNotNull().
                isInstanceOf(PhotoSocialized.class);

        return action.response();
    }

    protected TripWithDetails getTrip(String uid) {
        GetTripHttpAction action = execute(new GetTripHttpAction(uid));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response())
                .as("Trip with uid<%s> must exist", uid)
                .isNotNull()
                .isInstanceOf(TripWithDetails.class);

        return action.response();
    }

    protected BucketItemSocialized getBucketItem(String uid) {
        GetBucketItemHttpAction action = execute(new GetBucketItemHttpAction(uid));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response())
                .as("Bucket item with uid<%s> must exist", uid)
                .isNotNull()
                .isInstanceOf(BucketItemSocialized.class);

        return action.response();
    }

    protected void assertFeedItemIsNotNotificationItem(FeedItem feedItem) {
        // these fields are related to notification feed only
        // TODO separate feed item and notification feed item
        assertThat(feedItem.id()).as("Feed item must not have id").isNull();
        assertThat(feedItem.readAt()).as("Feed item must not have read_at").isNull();
    }
}
