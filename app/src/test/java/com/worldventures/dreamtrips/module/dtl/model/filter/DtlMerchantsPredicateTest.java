package com.worldventures.dreamtrips.module.dtl.model.filter;

import com.worldventures.dreamtrips.module.dtl.constants.TestConstants;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantAmenitiesPredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantDistancePredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantPricePredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantQueryPredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantTypePredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DtlMerchantsPredicateTest {

    @Test
    public void checkType_Success() {
        DtlMerchantTypePredicate predicate = new DtlMerchantTypePredicate(DtlMerchantType.OFFER);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setOffers(Collections.singletonList(DtlOffer.TYPE_PERK));
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkType_Fail() {
        DtlMerchantTypePredicate predicate = new DtlMerchantTypePredicate(DtlMerchantType.OFFER);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setOffers(Collections.emptyList());
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkQuery_Success_inDisplayName() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("Plano").build();
        DtlMerchantQueryPredicate predicate = new DtlMerchantQueryPredicate(filterData);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Plano");
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkQuery_Fail_inDisplayName() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("London").build();
        DtlMerchantQueryPredicate predicate = new DtlMerchantQueryPredicate(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Texas");

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkQuery_Success_inCategory() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("pizza").build();
        DtlMerchantQueryPredicate predicate = new DtlMerchantQueryPredicate(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Texas");
        dtlMerchant.setCategories(Collections.singletonList(new DtlMerchantAttribute("pizza")));

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkQuery_Fail_inCategory() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("whatever").build();
        DtlMerchantQueryPredicate predicate = new DtlMerchantQueryPredicate(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Texas");
        dtlMerchant.setCategories(Collections.singletonList(new DtlMerchantAttribute("pizza")));

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkBudget_Success() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().maxPrice(1).maxPrice(3).build();
        DtlMerchantPricePredicate predicate = new DtlMerchantPricePredicate(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setBudget(3);

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkBudget_Fail() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().maxPrice(1).maxPrice(3).build();
        DtlMerchantPricePredicate predicate = new DtlMerchantPricePredicate(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setBudget(6);

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkAmenities_Empty() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().amenities(Collections.emptyList()).build();
        DtlMerchantAmenitiesPredicate predicate = new DtlMerchantAmenitiesPredicate(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkAmenities_Success() {
        List<DtlMerchantAttribute> amenities = Collections.singletonList(new DtlMerchantAttribute("Free beer"));
        DtlFilterData filterData = ImmutableDtlFilterData.builder().amenities(amenities).selectedAmenities(amenities).build();
        DtlMerchantAmenitiesPredicate predicate = new DtlMerchantAmenitiesPredicate(filterData);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setAmenities(Collections.singletonList(new DtlMerchantAttribute("Free beer")));
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkAmenities_Fail() {
        List<DtlMerchantAttribute> amenities = Collections.singletonList(new DtlMerchantAttribute("Free beer"));
        DtlFilterData filterData = ImmutableDtlFilterData.builder().amenities(amenities).selectedAmenities(amenities).build();
        DtlMerchantAmenitiesPredicate predicate = new DtlMerchantAmenitiesPredicate(filterData);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setAmenities(Collections.singletonList(new DtlMerchantAttribute("Free beverages")));
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkDistance_Success_MaxDistance() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().distanceType(DistanceType.KMS).build();
        DtlMerchantDistancePredicate predicate = new DtlMerchantDistancePredicate(filterData);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDistanceType(DistanceType.KMS);
        dtlMerchant.setDistance(25d);
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkDistance_Success() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().distanceType(DistanceType.KMS).build();
        DtlMerchantDistancePredicate predicate = new DtlMerchantDistancePredicate(filterData);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDistanceType(DistanceType.KMS);
        dtlMerchant.setDistance(2500d);
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkDistance_Fail() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder()
                .distanceType(DistanceType.KMS)
                .maxDistance(20)
                .build();
        DtlMerchantDistancePredicate predicate = new DtlMerchantDistancePredicate(filterData);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDistanceType(DistanceType.KMS);
        dtlMerchant.setDistance(35000d);
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
