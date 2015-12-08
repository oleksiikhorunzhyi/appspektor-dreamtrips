package com.worldventures.dreamtrips.module.dtl.model.filter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class DtlMerchantsPredicateTest {

    @Test
    public void checkType_Success() {
        DtlMerchantsPredicate predicate = DtlMerchantsPredicate.Builder.create()
                .withMerchantType(DtlMerchantType.OFFER)
                .build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setOffers(Collections.singletonList(DtlOffer.TYPE_PERK));

        boolean result = predicate.checkType(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    public void checkType_Fail() {
        DtlMerchantsPredicate predicate = DtlMerchantsPredicate.Builder.create()
                .withMerchantType(DtlMerchantType.OFFER)
                .build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setOffers(Collections.emptyList());

        boolean result = predicate.checkType(dtlMerchant);

        assertThat(result).isFalse();
    }

    @Test
    public void checkQuery_Success_inDisplayName() {
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withQuery("Plano").build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setDisplayName("Plano");

        boolean result = predicate.checkQuery(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    public void checkQuery_Fail_inDisplayName() {
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withQuery("Plano").build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setDisplayName("Texas");

        boolean result = predicate.checkQuery(dtlMerchant);

        assertThat(result).isFalse();
    }

    @Test
    public void checkQuery_Success_inCategory() {
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withQuery("pizza").build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setDisplayName("Texas");
        dtlMerchant.setCategories(Collections.singletonList(new DtlMerchantAttribute("pizza")));

        boolean result = predicate.checkQuery(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    public void checkQuery_Fail_inCategory() {
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withQuery("bbq").build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setDisplayName("Plano");
        dtlMerchant.setCategories(Collections.singletonList(new DtlMerchantAttribute("pizza")));

        boolean result = predicate.checkQuery(dtlMerchant);

        assertThat(result).isFalse();
    }

}
