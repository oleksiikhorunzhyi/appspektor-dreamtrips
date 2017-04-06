package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.success_stories.GetSuccessStoriesHttpAction;
import com.worldventures.dreamtrips.api.success_stories.LikeSuccessStoryHttpAction;
import com.worldventures.dreamtrips.api.success_stories.UnlikeSuccessStoryHttpAction;
import com.worldventures.dreamtrips.api.success_stories.model.SuccessStory;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features("Success Stories")
public class SuccessStoriesTest extends BaseTestWithSession {

    List<SuccessStory> successStories;

    @Test()
    void testGetSuccessStories() {
        GetSuccessStoriesHttpAction action = execute(new GetSuccessStoriesHttpAction());

        successStories = action.response();
        assertThat(successStories).isNotEmpty();
    }

    @Test(dependsOnMethods = "testGetSuccessStories")
    void testLikeSuccessStory() {
        SuccessStory story = successStories.get(0);

        LikeSuccessStoryHttpAction likeAction = execute(new LikeSuccessStoryHttpAction(story.id()));
        assertThat(likeAction.statusCode()).isEqualTo(204);
    }

    @Test(dependsOnMethods = "testGetSuccessStories")
    void testUnlikeSuccessStory() {
        SuccessStory story = successStories.get(0);

        UnlikeSuccessStoryHttpAction unlikeAction = execute(new UnlikeSuccessStoryHttpAction(story.id()));
        assertThat(unlikeAction.statusCode()).isEqualTo(204);
    }
}
