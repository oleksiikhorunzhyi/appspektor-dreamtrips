package com.worldventures.dreamtrips.api.tests.smoke.dtl;

import com.worldventures.dreamtrips.api.dtl.merchants.AddReviewHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.GetReviewsMerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.RequestReviewParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ReviewParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features("DT Local")
public class ReviewsTest extends BaseTestWithSession {

    @Fixture("merchant_id") String merchantId;

    @Fixture("brandId") String brandId;

    @Fixture("add_reviews_query_params")
    RequestReviewParams requestReviewParams;

    @Fixture("body_add_reviews_query_params")
    ReviewParams requestBody;

    @Test
    void testGetAllReviews() {
        GetReviewsMerchantsHttpAction action = new GetReviewsMerchantsHttpAction(brandId, merchantId);
        execute(action);

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();
        assertThat(action.response().reviews()).isNotEmpty();
    }

    @Test
    void testAddReview() {
        AddReviewHttpAction action = new AddReviewHttpAction(requestReviewParams, requestBody);
        execute(action);
        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.response()).isNotNull();
    }
}
