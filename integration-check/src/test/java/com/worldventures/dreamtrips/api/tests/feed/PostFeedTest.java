package com.worldventures.dreamtrips.api.tests.feed;

import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;
import com.worldventures.dreamtrips.api.photos.model.Photo;
import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;

import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Post"})
public class PostFeedTest extends BaseFeedTest {

    List<FeedItem> feedItems;
    FeedItem feedItem;
    PostSocialized feedPost;
    PostSocialized post;

    @Test
    void testPostHasValidData() {
        // TODO remove version when problem of testing with different client version will be solved
        feedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItem(feedItems, BaseEntityHolder.Type.POST);

        if (feedItem != null) {
            assertThat(feedItem.entity())
                    .as("Feed item entity can never be null")
                    .isNotNull().isInstanceOf(PostSocialized.class);
            feedPost = (PostSocialized)feedItem.entity();
            post = getPost(feedPost.uid());

            assertThat(feedItem.action())
                    .as("Post can be added or liked")
                    .isIn(FeedItem.Action.ADD, FeedItem.Action.LIKE);
            assertThat(feedPost.equals(post));
            assertFeedItemIsNotNotificationItem(feedItem);
        } else {
            throw new SkipException("Skipping test because there are no posts in the feed response");
        }
    }

    @Test(dependsOnMethods = "testPostHasValidData")
    void testPostLinkedUsers() {
        assertThat(feedItem.links().users())
                .as("Any feed item(uid<%s>) with post must have linked user", feedPost.uid())
                .isNotEmpty();

        ShortUserProfile feedItemLinkedUser = feedItem.links().users().get(0);
        ShortUserProfile feedPostOwner = feedPost.owner();
        ShortUserProfile postOwner = post.owner();

        assertThat(feedItemLinkedUser.equals(postOwner));
        assertThat(feedPostOwner.equals(postOwner));
    }

    @Test(dependsOnMethods = "testPostHasValidData")
    void testPostAttachmentHaveValidData() {
        if (findFeedPostWithAttachment() != null) {
            FeedItem feedItem = findFeedPostWithAttachment();
            assertThat(feedItem.entity())
                    .as("Feed item entity can never be null")
                    .isNotNull().isInstanceOf(PostSocialized.class);
            PostSocialized feedPost = (PostSocialized)feedItem.entity();

            // currently only photo attachments supported
            EntityHolder attachment = feedPost.attachments().stream().findFirst().get();
            Photo feedPhoto = (Photo) attachment.entity();
            PhotoSocialized photo = getPhoto(feedPhoto.uid());

            assertThat(attachment.type()).isEqualTo(BaseEntityHolder.Type.PHOTO);
            assertThat(feedPhoto.equals(photo));
        } else {
            throw new SkipException("Skipping test because there are no posts with attachments in the feed response");
        }
    }

    private FeedItem findFeedPostWithAttachment() {
        Optional<FeedItem> optionalFeedItem = feedItems
                .stream()
                .filter(feedItem -> feedItem.type() == BaseEntityHolder.Type.POST)
                .filter(feedItem -> feedItem.entity() instanceof PostSocialized)
                .filter(feedItem -> ((PostSocialized)feedItem.entity()).attachments().stream().count() > 0)
                .findAny();

        return optionalFeedItem.isPresent() ? optionalFeedItem.get() : null;
    }
}
