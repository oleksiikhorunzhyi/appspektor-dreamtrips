package com.worldventures.dreamtrips.api.tests.smoke;


import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.api.post.GetPostHttpAction;
import com.worldventures.dreamtrips.api.post.UpdatePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.Post;
import com.worldventures.dreamtrips.api.post.model.response.PostSimple;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features({"Feed", "Post"})
public class PostTest extends BaseTestWithSession {

    static final String CLASS_NAME = "CommentTest";
    static final String GROUP_CREATION = CLASS_NAME + "_creation";
    static final String GROUP_MANIPULATION = CLASS_NAME + "_manipulation";
    static final String GROUP_CLEANUP = CLASS_NAME + "_cleanup";

    @Fixture("post_without_location")
    PostData creationData;
    @Fixture("post_with_location")
    PostData updateData;

    private volatile PostSimple createdPost;

    @Test(groups = GROUP_CREATION)
    void testCreatePost() {
        CreatePostHttpAction action = execute(new CreatePostHttpAction(creationData));

        assertThat(action.statusCode()).isEqualTo(200);
        createdPost = action.response();

        checkMainPostInformation(createdPost);
        assertThat(createdPost.description()).isEqualTo(creationData.description());
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = GROUP_CREATION)
    void testGetPost() {
        PostSocialized post = execute(new GetPostHttpAction(this.createdPost.uid())).response();

        assertThat(post.uid()).isEqualTo(this.createdPost.uid());
        checkMainPostInformation(post);
    }

    @Test(groups = GROUP_MANIPULATION, dependsOnGroups = GROUP_CREATION)
    void testUpdatePost() {
        UpdatePostHttpAction action = execute(new UpdatePostHttpAction(createdPost.uid(), updateData));

        assertThat(action.statusCode()).isEqualTo(200);
        PostSocialized updatedPost = action.response();

        checkMainPostInformation(updatedPost);
        assertThat(updatedPost.description()).isEqualTo(updateData.description());
        assertThat(updatedPost.uid()).isEqualTo(createdPost.uid());
        assertThat(updatedPost.location()).isEqualTo(updatedPost.location());
    }

    @Test(groups = GROUP_CLEANUP, dependsOnGroups = GROUP_MANIPULATION)
    void testDeletePost() {
        DeletePostHttpAction action = execute(new DeletePostHttpAction(createdPost.uid()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    private void checkMainPostInformation(Post post) {
        assertThat(post).as("Check createdPost not null").isNotNull();
        assertThat(post.uid()).isNotEmpty();
        assertThat(post.owner()).as("Check owner not null").isNotNull();
        assertThat(post.owner().id()).isNotNull();
        assertThat(post.uid()).isNotEmpty();
        assertThat(post.attachments()).isEmpty();
    }

}
