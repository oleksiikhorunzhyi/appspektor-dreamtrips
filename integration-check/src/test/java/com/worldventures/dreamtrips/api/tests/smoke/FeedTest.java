package com.worldventures.dreamtrips.api.tests.smoke;

import com.worldventures.dreamtrips.api.feed.GetAccountFeedHttpAction;
import com.worldventures.dreamtrips.api.feed.ImmutableGetAccountFeedHttpAction;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.assertj.core.api.Condition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.assertj.core.api.Assertions.assertThat;

@Features("Feed")
public class FeedTest extends BaseTestWithSession {

    @Fixture("account_feed_params")
    ImmutableGetAccountFeedHttpAction.Params accountParams;

    @BeforeClass
    void setupParams() {
        Date now = new Date();
        accountParams = ImmutableGetAccountFeedHttpAction.Params.builder()
                .from(accountParams)
                .before(now)
                .build();
    }

    @Test
    void testGetAccountFeed() {
        GetAccountFeedHttpAction action = execute(new GetAccountFeedHttpAction(accountParams));

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response())
                .as("Feed must not be empty for user<%s>", authorizedUser().username())
                .isNotEmpty()
                .extracting(FeedItem::createdAt)
                .are(new Condition<>(date -> date.before(accountParams.before()), "Feed item created before params date"))
        ;
    }

}
