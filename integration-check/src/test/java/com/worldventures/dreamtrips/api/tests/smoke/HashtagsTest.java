package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.hashtags.GetHashtagsSearchAction;
import com.worldventures.dreamtrips.api.hashtags.GetHashtagsSuggestsAction;
import com.worldventures.dreamtrips.api.hashtags.model.HashtagsSearchParams;
import com.worldventures.dreamtrips.api.hashtags.model.HashtagsSuggestsParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Features("HashTags")
public class HashtagsTest extends BaseTestWithSession {

    @Fixture("hash_tags_suggests_params")
    HashtagsSuggestsParams suggestsParams;

    @Fixture("hash_tags_search_params")
    HashtagsSearchParams searchParams;

    @Test
    void testGetListOfHashtagsSuggests() {
        GetHashtagsSuggestsAction action = execute(new GetHashtagsSuggestsAction(suggestsParams));
        assertThat(action.response()).isNotEmpty();
    }

    @Test
    void testHashtagsSearch() {
        GetHashtagsSearchAction action = execute(new GetHashtagsSearchAction(searchParams));
        assertThat(action.response()).isNotEmpty();
    }
}
