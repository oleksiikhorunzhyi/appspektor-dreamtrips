package com.worldventures.dreamtrips.api.tests.smoke.dtl;

import com.worldventures.dreamtrips.api.dtl.merchants.GetMerchantByIdHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.GetThinMerchantsHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsActionParams;
import com.worldventures.dreamtrips.api.tests.BaseTestWithSession;

import org.testng.annotations.Test;

import ie.corballis.fixtures.annotation.Fixture;
import ru.yandex.qatools.allure.annotations.Features;

import static org.fest.assertions.api.Assertions.assertThat;

@Features("DT Local")
public class MerchantsTest extends BaseTestWithSession {

    @Fixture("merchant_id") String merchantId;

    @Fixture("thin_merchants_query_params")
    ThinMerchantsActionParams params;

    @Test
    void testGetThinMerchants() {
        GetThinMerchantsHttpAction action = new GetThinMerchantsHttpAction(params);

        execute(action);

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.merchants()).isNotEmpty();
    }

    @Test
    void testGetMerchantById() {
        GetMerchantByIdHttpAction action = new GetMerchantByIdHttpAction(merchantId);

        execute(action);

        assertThat(action.statusCode()).isEqualTo(200);
        assertThat(action.merchant()).isNotNull();
        assertThat(action.merchant().id()).isEqualTo(merchantId);
    }
}
