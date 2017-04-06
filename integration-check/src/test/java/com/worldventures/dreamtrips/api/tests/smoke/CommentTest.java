package com.worldventures.dreamtrips.api.tests.smoke;


import com.worldventures.dreamtrips.api.comment.CreateCommentHttpAction;
import com.worldventures.dreamtrips.api.comment.DeleteCommentHttpAction;
import com.worldventures.dreamtrips.api.comment.GetCommentsHttpAction;
import com.worldventures.dreamtrips.api.comment.UpdateCommentHttpAction;
import com.worldventures.dreamtrips.api.comment.model.Comment;
import com.worldventures.dreamtrips.api.http.executor.SafeExecutor;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.PostSimple;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Comments"})
public class CommentTest extends BaseTestWithSession {

    static final String CLASS_NAME = "CommentTest";
    static final String GROUP_CREATION = CLASS_NAME + "_creation";
    static final String GROUP_MANIPULATION = CLASS_NAME + "_manipulation";
    static final String GROUP_CLEANUP = CLASS_NAME + "_cleanup";

    private final String commentPrefix = "comment";
    private final String commentText = commentPrefix + "0";
    private final String updatedCommentText = "updated at " + System.currentTimeMillis() + ". Original is: " + commentText;

    @Fixture("post_without_location")
    PostData data;

    volatile PostSimple post;
    volatile Comment testComment;

    @BeforeClass
    public void initializePost() {
        CreatePostHttpAction action = execute(new CreatePostHttpAction(data));
        post = action.response();

        assertThat(post).isNotNull();
        assertThat(post.uid()).isNotEmpty();
    }

    @AfterClass
    public void cleanup() {
        if (post != null) {
            SafeExecutor safeExecutor = SafeExecutor.from(this);
            safeExecutor.execute(new DeletePostHttpAction(post.uid()));
        }
    }

    @Test(groups = GROUP_CREATION)
    void testCreateComment() {
        CreateCommentHttpAction action = execute(new CreateCommentHttpAction(post.uid(), commentText));
        assertThat(action.statusCode()).isEqualTo(200);

        testComment = action.response();
        checkMainCommentInfo(testComment);
        assertThat(testComment.text()).isEqualTo(commentText);
        assertThat(testComment.createdTime().getTime()).isGreaterThan(0);
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = GROUP_CREATION)
    void testUpdateComment() {
        UpdateCommentHttpAction action = execute(new UpdateCommentHttpAction(testComment.uid(), updatedCommentText));
        assertThat(action.statusCode()).isEqualTo(200);

        Comment updateComment = action.response();
        checkMainCommentInfo(updateComment);
        assertThat(updateComment.text()).isEqualTo(updatedCommentText);
        assertThat(updateComment.updatedTime().getTime())
                .isNotEqualTo(testComment.createdTime().getTime());
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = GROUP_CREATION)
    void testCommentsList() {
        for (int i = 1; i <= 10; i++) {
            CreateCommentHttpAction action = execute(new CreateCommentHttpAction(post.uid(), commentPrefix + i));
            assertThat(action.statusCode()).isEqualTo(200);
        }

        GetCommentsHttpAction action = execute(new GetCommentsHttpAction(post.uid(), 1, 11));
        assertThat(action.statusCode()).isEqualTo(200);
        List<Comment> comments = action.response();
        assertThat(comments).hasSize(11)
                .doesNotHaveDuplicates()
                .doesNotContainNull();
        for (Comment comment : comments) {
            checkMainCommentInfo(comment);
        }
    }

    @Test(groups = GROUP_CLEANUP, dependsOnGroups = GROUP_MANIPULATION)
    void testDeleteComment() {
        DeleteCommentHttpAction deleteCommendAction = execute(new DeleteCommentHttpAction(testComment.uid()));
        assertThat(deleteCommendAction.statusCode()).isEqualTo(204);

        GetCommentsHttpAction action = execute(new GetCommentsHttpAction(post.uid(), 1, 11));
        List<Comment> comments = action.response();
        assertThat(comments).extracting("uid")
                .doesNotContain(testComment.uid());
    }

    private void checkMainCommentInfo(Comment comment) {
        assertThat(comment.uid()).isNotEmpty();
        assertThat(comment.postId()).isEqualTo(post.uid());
        assertThat(comment.author().id()).isNotNull();
        assertThat(comment.parentId()).isNullOrEmpty();
    }

}
