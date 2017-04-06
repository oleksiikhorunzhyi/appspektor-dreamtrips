package com.worldventures.dreamtrips.api.tests.feed;

import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;
import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;

import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Photos"})
public class PhotoFeedTest extends BaseFeedTest {

    List<FeedItem> feedItems;
    FeedItem feedItem;
    PhotoSocialized feedPhoto;
    PhotoSocialized photo;

    @Test
    void testOldFeedDoesNotContainPostsWithAttachments() {
        // TODO remove version when problem of testing with different client version will be solved
        feedItems = getFeedItems(APP_VERSION_OLD);

        assertThat(feedItems)
                .filteredOn(feedItem -> feedItem.type() == BaseEntityHolder.Type.POST)
                .filteredOn(feedItem -> ((PostSocialized)feedItem.entity()).attachments().stream().count() > 0)
                .isEmpty();
    }

    @Test(dependsOnMethods = "testOldFeedDoesNotContainPostsWithAttachments")
    void testPhotoHasValidData() {
        feedItem = findFeedItem(feedItems, BaseEntityHolder.Type.PHOTO);

        if (feedItem != null) {
            assertThat(feedItem.entity())
                    .as("Feed item entity can never be null")
                    .isNotNull().isInstanceOf(PhotoSocialized.class);
            feedPhoto = (PhotoSocialized) feedItem.entity();
            photo = getPhoto(feedPhoto.uid());

            assertThat(feedItem.action())
                    .as("Photo can be added or liked")
                    .isIn(FeedItem.Action.ADD, FeedItem.Action.LIKE);
            assertThat(feedPhoto.equals(photo));
            assertFeedItemIsNotNotificationItem(feedItem);
        } else {
            throw new SkipException("Skipping test because there are no photos in the feed response");
        }
    }

    @Test(dependsOnMethods = "testPhotoHasValidData")
    void testPhotoLinkedUsers() {
        assertThat(feedItem.links().users())
                .as("Any feed item(uid<%s>) with photo must have linked user", feedPhoto.uid())
                .isNotEmpty();

        ShortUserProfile feedItemLinkedUser = feedItem.links().users().get(0);
        ShortUserProfile feedPhotoAuthor = feedPhoto.author();
        ShortUserProfile postAuthor = photo.author();

        assertThat(feedItemLinkedUser.equals(postAuthor));
        assertThat(feedPhotoAuthor.equals(postAuthor));
    }

}
