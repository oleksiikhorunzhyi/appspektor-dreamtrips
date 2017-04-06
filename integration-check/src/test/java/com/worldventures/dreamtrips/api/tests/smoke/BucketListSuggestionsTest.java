package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.bucketlist.GetBucketListSuggestionsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListSuggestion;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import ru.yandex.qatools.allure.annotations.Features;

import static com.worldventures.dreamtrips.api.bucketlist.model.BucketType.ACTIVITY;
import static com.worldventures.dreamtrips.api.bucketlist.model.BucketType.DINING;
import static com.worldventures.dreamtrips.api.bucketlist.model.BucketType.LOCATION;
import static org.assertj.core.api.Assertions.assertThat;

@Features("Bucket List")
public class BucketListSuggestionsTest extends BaseTestWithSession {

    @DataProvider
    public static Object[][] suggestionParamsProvider() {
        return new Object[][]{
                {ACTIVITY, "wid"},
                {LOCATION, "sing"},
                {DINING, "vega"}
        };
    }

    @Test(dataProvider = "suggestionParamsProvider")
    void testGetBucketListSuggestions(BucketType type, String query) {
        List<BucketListSuggestion> suggestions = execute(new GetBucketListSuggestionsHttpAction(type, query)).response();
        assertThat(suggestions).isNotEmpty();
        suggestions.stream().map(BucketListSuggestion::name).forEach(name -> {
            assertThat(name).containsIgnoringCase(query);
        });
    }

    @Test
    void testGetBucketListSuggestionsFailOnTooShortQuery() {
        GetBucketListSuggestionsHttpAction action = execute(new GetBucketListSuggestionsHttpAction(LOCATION, "ve"));
        assertThat(action.statusCode()).isEqualTo(422);
    }
}
