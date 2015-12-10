package com.worldventures.dreamtrips.module.dtl.model.filter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlPlacesFilterAttribute;
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

    @Test
    public void checkBudget_Success() {
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create()
                        .withDtlFilterData(DtlFilterData.createDefault())
                        .build();

        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setBudget(2);

        boolean result = predicate.checkPrice(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    public void checkBudget_Fail() {
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create()
                        .withDtlFilterData(DtlFilterData.createDefault())
                        .build();

        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setBudget(6);

        boolean result = predicate.checkPrice(dtlMerchant);

        assertThat(result).isFalse();
    }

    @Test
    public void checkAmenities_Empty() {
        DtlFilterData dtlFilterData = DtlFilterData.createDefault();
        dtlFilterData.setAmenities(Collections.singletonList(new DtlPlacesFilterAttribute("Free beer", true)));
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withDtlFilterData(dtlFilterData).build();
        DtlMerchant dtlMerchant = new DtlMerchant();

        boolean result = predicate.checkAmenities(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    public void checkAmenities_Success() {
        DtlFilterData dtlFilterData = DtlFilterData.createDefault();
        dtlFilterData.setAmenities(Collections.singletonList(new DtlPlacesFilterAttribute("Free beer", true)));
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withDtlFilterData(dtlFilterData).build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setAmenities(Collections.singletonList(new DtlMerchantAttribute("Free beer")));

        boolean result = predicate.checkAmenities(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    public void checkAmenities_Fail() {
        DtlFilterData dtlFilterData = DtlFilterData.createDefault();
        dtlFilterData.setAmenities(Collections.singletonList(new DtlPlacesFilterAttribute("Free beer", true)));
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withDtlFilterData(dtlFilterData).build();
        DtlMerchant dtlMerchant = new DtlMerchant();
        dtlMerchant.setAmenities(Collections.singletonList(new DtlMerchantAttribute("Free beverages")));

        boolean result = predicate.checkAmenities(dtlMerchant);

        assertThat(result).isFalse();
    }

    @Test
    public void checkDistance_Success_MaxDistance() {
        DtlFilterData dtlFilterData = DtlFilterData.createDefault();
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withDtlFilterData(dtlFilterData).build();

        DtlMerchant dtlMerchant = new DtlMerchant();

        boolean result = predicate.checkDistance(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    public void checkDistance_Success() {
        DtlFilterData dtlFilterData = DtlFilterData.createDefault();
        DtlMerchantsPredicate predicate =
                DtlMerchantsPredicate.Builder.create().withDtlFilterData(dtlFilterData).build();

        DtlMerchant dtlMerchant = new DtlMerchant();

        boolean result = predicate.checkDistance(dtlMerchant);

        assertThat(result).isFalse();
    }

}
