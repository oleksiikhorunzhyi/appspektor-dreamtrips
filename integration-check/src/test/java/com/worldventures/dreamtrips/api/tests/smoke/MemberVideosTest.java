package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.member_videos.GetMemberVideoLocalesHttpAction;
import com.worldventures.dreamtrips.api.member_videos.GetMemberVideosHttpAction;
import com.worldventures.dreamtrips.api.member_videos.model.VideoCategory;
import com.worldventures.dreamtrips.api.member_videos.model.VideoLanguage;
import com.worldventures.dreamtrips.api.member_videos.model.VideoLocale;
import com.worldventures.dreamtrips.api.member_videos.model.VideoType;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("MemberVideos")
public class MemberVideosTest extends BaseTestWithSession {

    private VideoLanguage language;

    @Test
    void testGetMemberVideosDefault() {
        for (VideoType type : VideoType.values()) {
            GetMemberVideosHttpAction action = execute(new GetMemberVideosHttpAction(type));
            assertThat(action.response()).isNotEmpty();

            for (VideoCategory category : action.response()) {
                assertThat(category.videos()).isNotEmpty();
            }
        }
    }

    @Test
    void testGetMemberVideoLocales() {
        GetMemberVideoLocalesHttpAction action = execute(new GetMemberVideoLocalesHttpAction());
        List<VideoLocale> locales = action.response();

        assertThat(locales).isNotEmpty();
        assertThat(locales.get(0).languages()).isNotEmpty();

        this.language = locales.get(0).languages().get(0);
    }

    @Test(dependsOnMethods = {"testGetMemberVideoLocales"})
    void testGetMemberVideos() {
        for (VideoType type : VideoType.values()) {
            GetMemberVideosHttpAction action = execute(new GetMemberVideosHttpAction(type, this.language));
            assertThat(action.response()).isNotEmpty();

            for (VideoCategory category : action.response()) {
                assertThat(category.videos()).isNotEmpty();
            }
        }
    }
}
