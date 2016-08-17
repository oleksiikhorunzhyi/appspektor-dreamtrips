package com.worldventures.dreamtrips.dtl.model;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPoints;

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
      DtlOfferPoints dtlPointsOffer = new DtlOfferPoints();
      DtlCurrency dtlCurrency = new DtlCurrency();
      dtlCurrency.setDefault(true);
      dtlCurrency.setCode("USD");
      dtlPointsOffer.setCurrencies(Collections.singletonList(dtlCurrency));
      dtlMerchant.setOffers(Collections.singletonList(dtlPointsOffer));

      DtlCurrency dtlCurrencyDefault = dtlMerchant.getDefaultCurrency();

      assertThat(dtlCurrencyDefault).isEqualToComparingFieldByField(dtlCurrency);
   }
}
