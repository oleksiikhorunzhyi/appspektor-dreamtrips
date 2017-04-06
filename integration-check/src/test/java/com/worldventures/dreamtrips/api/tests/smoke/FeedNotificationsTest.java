package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.comment.CreateCommentHttpAction;
import com.worldventures.dreamtrips.api.comment.DeleteCommentHttpAction;
import com.worldventures.dreamtrips.api.comment.model.Comment;
import com.worldventures.dreamtrips.api.feed.GetFeedNotificationsHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetFeedNotificationsHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableMarkFeedNotificationsReadHttpAction;
import com.worldventures.dreamtrips.api.feed.MarkFeedNotificationReadHttpAction;
import com.worldventures.dreamtrips.api.feed.MarkFeedNotificationsReadHttpAction;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.fixtures.UserCredential;
import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.Post;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.assertj.core.api.Condition;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder.Type.POST;
import static org.assertj.core.api.Assertions.assertThat;

@Features("Feed")
public class FeedNotificationsTest extends BaseTestWithSession {

    @Fixture("feed_notifications_params")
    ImmutableGetFeedNotificationsHttpAction.Params notifParams;

    @Fixture("mark_notifications_params")
    ImmutableMarkFeedNotificationsReadHttpAction.Params markNotifsParams;

    @Fixture("post_without_location")
    PostData postData;

    @Fixture("user_with_no_rds")
    UserCredential someUserCredentials;

    List<FeedItem> notifications;
    Post post;
    Comment comment;

    @BeforeClass
    void setupParams() {
        post = createPost(postData);
        comment = createComment(post, "Feed notification test comment");
    }

    @AfterClass
    void cleanup() {
        SafeExecutor safeExecutor = SafeExecutor.from(this);

        if (comment != null) {
            safeExecutor.execute(new DeleteCommentHttpAction(comment.uid()));
        }
        if (post != null) {
            safeExecutor.execute(new DeletePostHttpAction(post.uid()));
        }
    }

    @Test
    void testGetOldFeedNotifications() {
        Date yesterday = DateTime.now().minusDays(1).toDate();

        ImmutableGetFeedNotificationsHttpAction.Params params = ImmutableGetFeedNotificationsHttpAction.Params.builder()
                .from(notifParams)
                .before(yesterday)
                .build();

        List<FeedItem> notifications = execute(new GetFeedNotificationsHttpAction(params)).response();

        assertThat(notifications)
                .isNotEmpty()
                .extracting(FeedItem::createdAt)
                .are(new Condition<>(date -> date.before(params.before()), "Notification created before params date"));
    }

    @Test
    void testGetFeedNotifications() {
        ImmutableGetFeedNotificationsHttpAction.Params params = ImmutableGetFeedNotificationsHttpAction.Params.builder()
                .from(notifParams)
                .build();

        notifications = execute(new GetFeedNotificationsHttpAction(params)).response();

        assertThat(notifications).isNotEmpty();
        assertThat(findNotificationFeedItem(post.uid())).isNotNull();
    }

    @Test(dependsOnMethods = {"testGetFeedNotifications"})
    void testMarkNotificationRead() {
        FeedItem feedItem = findNotificationFeedItem(post.uid());
        MarkFeedNotificationReadHttpAction action = execute(new MarkFeedNotificationReadHttpAction(feedItem.id()));

        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = {"testGetOldFeedNotifications"})
    void testMarkNotificationsRead() {
        MarkFeedNotificationsReadHttpAction action = execute(new MarkFeedNotificationsReadHttpAction(markNotifsParams));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    private Comment createComment(Post post, String commentText) {
        CreateCommentHttpAction action = as(someUserCredentials).execute(new CreateCommentHttpAction(post.uid(), commentText));
        Comment comment = action.response();

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(comment.uid()).isNotNull();

        return comment;
    }

    private Post createPost(PostData postData) {
        CreatePostHttpAction action = execute(new CreatePostHttpAction(postData));
        Post post = action.response();

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(post.uid()).isNotNull();

        return post;
    }

    @Nullable
    private FeedItem findNotificationFeedItem(String uid) {
        Optional<FeedItem> optionalFeedItem = notifications
                .stream()
                .filter(feedItem -> feedItem.type() == POST)
                .filter(feedItem -> ((Post) feedItem.entity()).uid().equals(uid))
                .findAny();

        return optionalFeedItem.isPresent() ? optionalFeedItem.get() : null;
    }
}
