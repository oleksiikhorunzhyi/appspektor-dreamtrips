package com.worldventures.dreamtrips.api.tests.feed;

import com.worldventures.dreamtrips.api.comment.CreateCommentHttpAction;
import com.worldventures.dreamtrips.api.comment.model.Comment;
import com.worldventures.dreamtrips.api.comment.model.Commentable;
import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;
import com.worldventures.dreamtrips.api.tests.util.ServerUtil;

import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Comments"})
public class CommentFeedTest extends BaseFeedTest{

    private final String commentText = "Lorem ipsum dolor sit amet...";

    List<FeedItem> feedItems;

    @BeforeClass
    void initializeFeed() {
        feedItems = getFeedItems(APP_VERSION);
    }

    @Test
    void testUserCommentsPostFeedItem() {
        FeedItem feedItem = findFeedItem(feedItems, BaseEntityHolder.Type.POST);

        if (feedItem == null) {
            throw new SkipException("Skipping test because there are no feed items of type post in the response");
        }

        assertThat(feedItem.entity())
                .as("Feed item entity can never be null")
                .isNotNull().isInstanceOf(PostSocialized.class);
        PostSocialized post = (PostSocialized) feedItem.entity();

        Comment comment = createComment(post.uid(), commentText);
        ServerUtil.waitForServerLag();

        // check feed item changed after commenting
        List<FeedItem> updatedFeedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItemByUid(updatedFeedItems, post.uid());
        PostSocialized updatedPost = (PostSocialized) feedItem.entity();

        compareComments(post, updatedPost, comment);
    }

    @Test
    void testUserCommentsPhotoFeedItem() {
        FeedItem feedItem = findFeedItem(feedItems, BaseEntityHolder.Type.PHOTO);

        if (feedItem == null) {
            throw new SkipException("Skipping test because there are no feed items of type photo in the response");
        }

        assertThat(feedItem.entity())
                .as("Feed item entity can never be null")
                .isNotNull().isInstanceOf(PhotoSocialized.class);
        PhotoSocialized photo = (PhotoSocialized) feedItem.entity();

        Comment comment = createComment(photo.uid(), commentText);
        ServerUtil.waitForServerLag();

        // check feed item changed after commenting
        List<FeedItem> updatedFeedItems = getFeedItems(APP_VERSION);
        feedItem = findFeedItemByUid(updatedFeedItems, photo.uid());
        PhotoSocialized updatedPhoto = (PhotoSocialized) feedItem.entity();

        compareComments(photo, updatedPhoto, comment);
    }

    private Comment createComment(String uid, String commentText) {
        CreateCommentHttpAction action = execute(new CreateCommentHttpAction(uid, commentText));
        assertThat(action.statusCode()).isEqualTo(200);
        Comment comment = action.response();
        assertThat(comment).isNotNull();

        return comment;
    }

    private void compareComments(Commentable entity, Commentable updatedEntity, Comment comment) {
        assertThat(updatedEntity.comments()).as("Feed item has comment").isNotEmpty();
        assertThat(updatedEntity.commentsCount())
                .as("Feed item's comment count is greater then it was before commenting")
                .isGreaterThan(entity.commentsCount());
        assertThat(updatedEntity.comments().get(0).createdTime().getTime())
                .as("Feed item's comment has created date greater or equal then created by current user")
                .isGreaterThanOrEqualTo(comment.createdTime().getTime());
    }
}
