package com.worldventures.dreamtrips.api.tests.feed;

import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.likes.DislikeHttpAction;
import com.worldventures.dreamtrips.api.likes.LikeHttpAction;
import com.worldventures.dreamtrips.api.likes.model.Likeable;
import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;

import org.jetbrains.annotations.Nullable;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Likes"})
public class LikeFeedTest extends BaseFeedTest {

    List<FeedItem> feedItems;

    @BeforeClass
    void initializeFeed() {
        feedItems = getFeedItems(APP_VERSION);
    }

    @Test
    void testUserLikesPhotoFeedItem() {
        FeedItem feedItem = findUnlikedFeedItem(feedItems, BaseEntityHolder.Type.PHOTO);

        if (feedItem == null) {
            throw new SkipException("Skipping test because there are no unliked photos");
        }

        assertThat(feedItem.entity())
                .as("Feed item entity can never be null")
                .isNotNull().isInstanceOf(PhotoSocialized.class);
        PhotoSocialized photo = (PhotoSocialized) feedItem.entity();
        likeItem(photo.uid());
        ServerUtil.waitForServerLag();

        feedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItemByUid(feedItems, photo.uid());

        assertItemIsLiked((PhotoSocialized) feedItem.entity(), photo);
    }

    @Test(dependsOnMethods = "testUserLikesPhotoFeedItem")
    void testUserLikesPostFeedItem() {
        FeedItem feedItem = findUnlikedFeedItem(feedItems, BaseEntityHolder.Type.POST);

        if (feedItem == null) {
            throw new SkipException("Skipping test because there are no unliked posts");
        }

        assertThat(feedItem.entity())
                .as("Feed item entity can never be null")
                .isNotNull().isInstanceOf(PostSocialized.class);
        PostSocialized post = (PostSocialized) feedItem.entity();
        likeItem(post.uid());
        ServerUtil.waitForServerLag();

        feedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItemByUid(feedItems, post.uid());

        assertItemIsLiked((PostSocialized) feedItem.entity(), post);
    }

    @Test(dependsOnMethods = "testUserLikesPhotoFeedItem")
    void testUserUnlikesPhotoFeedItem() {
        FeedItem feedItem = findLikedFeedItem(feedItems, BaseEntityHolder.Type.PHOTO);
        assertThat(feedItem).isNotNull();

        assertThat(feedItem.entity())
                .as("Feed item entity can never be null")
                .isNotNull().isInstanceOf(PhotoSocialized.class);
        PhotoSocialized photo = (PhotoSocialized) feedItem.entity();
        unlikeItem(photo.uid());
        ServerUtil.waitForServerLag();

        feedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItemByUid(feedItems, photo.uid());

        assertItemIsUnliked((PhotoSocialized) feedItem.entity(), photo);
    }

    @Test(dependsOnMethods = "testUserLikesPostFeedItem")
    void testUserUnlikesPostFeedItem() {
        FeedItem feedItem = findLikedFeedItem(feedItems, BaseEntityHolder.Type.POST);
        assertThat(feedItem).isNotNull();

        assertThat(feedItem.entity())
                .as("Feed item entity can never be null")
                .isNotNull().isInstanceOf(PostSocialized.class);
        PostSocialized post = (PostSocialized) feedItem.entity();
        unlikeItem(post.uid());
        ServerUtil.waitForServerLag();

        feedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItemByUid(feedItems, post.uid());

        assertItemIsUnliked((PostSocialized) feedItem.entity(), post);
    }

    @Nullable
    private FeedItem findUnlikedFeedItem(List<FeedItem> feedItems, BaseEntityHolder.Type type) {
        return findFeedItemByLikedStatus(feedItems, type, false);
    }

    @Nullable
    private FeedItem findLikedFeedItem(List<FeedItem> feedItems, BaseEntityHolder.Type type) {
        return findFeedItemByLikedStatus(feedItems, type, true);
    }

    @Nullable
    private FeedItem findFeedItemByLikedStatus(List<FeedItem> feedItems, BaseEntityHolder.Type type, Boolean liked) {
        Optional<FeedItem> optionalItem = feedItems
                .stream()
                .filter(feedItem -> feedItem.type() == type)
                .filter(feedItem -> ((Likeable) feedItem.entity()).liked() == liked)
                .findAny();

        return optionalItem.isPresent() ? optionalItem.get() : null;
    }

    private void likeItem(String uid) {
        LikeHttpAction action = execute(new LikeHttpAction(uid));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    private void unlikeItem(String uid) {
        DislikeHttpAction action = execute(new DislikeHttpAction(uid));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    private void assertItemIsUnliked(Likeable entity, Likeable previousState) {
        assertThat(entity.liked()).isEqualTo(false);
        assertThat(entity.likes() < previousState.likes());
    }

    private void assertItemIsLiked(Likeable entity, Likeable previousState) {
        assertThat(entity.liked()).isEqualTo(true);
        assertThat(entity.likes() > previousState.likes());
    }
}
