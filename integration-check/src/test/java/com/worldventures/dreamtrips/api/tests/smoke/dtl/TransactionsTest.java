package com.worldventures.dreamtrips.api.tests.smoke.dtl;

import com.worldventures.dreamtrips.api.dtl.merchants.AddRatingHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.AddTransactionHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.EstimatePointsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.TransactionDetails;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableEstimationParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRatingParams;
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.Transaction;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features("DT Local")
public class TransactionsTest extends BaseTestWithSession {

    @Fixture("merchant_id") String merchantId;

    @Fixture("transaction") Transaction transaction;

    @Test
    void testAddTransaction() {
        AddTransactionHttpAction action = execute(getTransactionsHttpAction());

        assertThat(action.statusCode()).isEqualTo(200);
        TransactionDetails transactionDetails = action.transactionDetails();
        assertThat(transactionDetails).isNotNull();
    }

    @Test
    void testAddRating() {
        String transactionId = execute(getTransactionsHttpAction()).transactionDetails().id();
        AddRatingHttpAction ratingAction = new AddRatingHttpAction(
                merchantId,
                ImmutableRatingParams.builder()
                        .rating(3)
                        .transactionId(transactionId)
                        .comment("It was ok.")
                        .build()
        );

        execute(ratingAction);

        assertThat(ratingAction.statusCode()).isEqualTo(204);
    }

    @Test
    void testAddRatingWithoutOptional() {
        String transactionId = execute(getTransactionsHttpAction()).transactionDetails().id();
        AddRatingHttpAction ratingAction = new AddRatingHttpAction(
                merchantId,
                ImmutableRatingParams.builder()
                        .rating(3)
                        .transactionId(transactionId)
                        .build()
        );

        execute(ratingAction);

        assertThat(ratingAction.statusCode()).isEqualTo(204);
    }

    @Test
    void testPointsEstimation() {
        EstimatePointsHttpAction action = new EstimatePointsHttpAction(
                merchantId,
                ImmutableEstimationParams.builder()
                        .checkinTime("2016-01-23T10:47:04Z")
                        .billTotal(100d)
                        .currencyCode("USD")
                        .build()
        );

        execute(action);

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.estimatedPoints()).isNotNull();
    }

    private AddTransactionHttpAction getTransactionsHttpAction() {
        return new AddTransactionHttpAction(merchantId, transaction);
    }
}
