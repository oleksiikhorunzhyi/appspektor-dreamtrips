package com.worldventures.dreamtrips.module.dtl.model;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DtlMerchantCurrencyTest {

    @Test
    public void test_getDefaultCurrency() {
        DtlMerchant dtlMerchant = new DtlMerchant();
        DtlOffer<DtlOfferPointsData> dtlPointsOffer = new DtlOffer<>();
        DtlOfferPointsData dtlOfferPointsDescription = new DtlOfferPointsData();
        DtlCurrency dtlCurrency = new DtlCurrency();
        dtlCurrency.setDefault(true);
        dtlCurrency.setCode("USD");
        dtlOfferPointsDescription.setCurrencies(Collections.singletonList(dtlCurrency));
        dtlPointsOffer.setType(Offer.POINT_REWARD);
        dtlPointsOffer.setOffer(dtlOfferPointsDescription);
        dtlMerchant.setOffers(Collections.singletonList(dtlPointsOffer));

        DtlCurrency dtlCurrencyDefault = dtlMerchant.getDefaultCurrency();

        assertThat(dtlCurrencyDefault).isEqualToComparingFieldByField(dtlCurrency);
    }
}
