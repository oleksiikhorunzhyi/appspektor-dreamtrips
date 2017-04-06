package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.likes.DislikeHttpAction;
import com.worldventures.dreamtrips.api.likes.GetLikersHttpAction;
import com.worldventures.dreamtrips.api.likes.LikeHttpAction;
import com.worldventures.dreamtrips.api.post.CreatePostHttpAction;
import com.worldventures.dreamtrips.api.post.DeletePostHttpAction;
import com.worldventures.dreamtrips.api.post.model.request.ImmutablePostData;
import com.worldventures.dreamtrips.api.post.model.request.PostData;
import com.worldventures.dreamtrips.api.post.model.response.PostSimple;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Likes")
public class LikesTest extends BaseTestWithSession {
    private PostSimple post;

    @BeforeClass
    private void createPost() {
        PostData data = ImmutablePostData.builder()
                .description("dummy")
                .build();

        CreatePostHttpAction action = execute(new CreatePostHttpAction(data));
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();

        this.post = action.response();
    }

    @AfterClass
    private void deletePost() {
        DeletePostHttpAction action = execute(new DeletePostHttpAction(post.uid()));
        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test
    void testLikePost() {
        LikeHttpAction action = execute(new LikeHttpAction(post.uid()));

        assertThat(action.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = {"testLikePost"})
    void testGetLikers() {
        GetLikersHttpAction action = execute(new GetLikersHttpAction(post.uid(), 1, 10));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotEmpty();
    }

    @Test(dependsOnMethods = {"testGetLikers"})
    void testDislikePost() {
        DislikeHttpAction action = execute(new DislikeHttpAction(post.uid()));

        assertThat(action.statusCode()).isEqualTo(204);
    }
}
