package com.worldventures.dreamtrips.dtl.model.filter;

import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.dtl.constants.TestConstants;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantTypePredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.ImmutableOffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DtlMerchantsPredicateTest {

   @Test
   public void checkType_Success() {
      DtlMerchantTypePredicate predicate = new DtlMerchantTypePredicate(MerchantType.OFFER);
      //
      DtlMerchant dtlMerchant = createDefaultMerchant();
      dtlMerchant.setOffers(Collections.singletonList(ImmutableOffer.builder()
            .title("")
            .description("")
            .disclaimer("")
            .type(OfferType.PERK).build()));
      //
      boolean result = predicate.apply(dtlMerchant);
      assertThat(result).isTrue();
   }

   @Test
   public void checkType_Fail() {
      DtlMerchantTypePredicate predicate = new DtlMerchantTypePredicate(MerchantType.OFFER);
      //
      DtlMerchant dtlMerchant = createDefaultMerchant();
      dtlMerchant.setOffers(Collections.emptyList());
      //
      boolean result = predicate.apply(dtlMerchant);
      assertThat(result).isFalse();
   }

   private static DtlMerchant createDefaultMerchant() {
      DtlMerchant merchant = new DtlMerchant();
      merchant.setBudget(2); // default from 1 to 5;
      merchant.setDisplayName(TestConstants.DEFAULT_SEARCH_QUERY);
      return merchant;
   }
}
