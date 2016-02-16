package com.worldventures.dreamtrips.module.dtl.model.filter;

import com.worldventures.dreamtrips.module.dtl.constants.TestConstants;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DtlMerchantsPredicateTest {

    @Test
    public void checkType_Success() {
        DtlMerchantsPredicate predicate = createWithType(DtlMerchantType.OFFER);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setOffers(Collections.singletonList(DtlOffer.TYPE_PERK));

        boolean result = predicate.checkType(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkType_Fail() {
        DtlMerchantsPredicate predicate = createWithType(DtlMerchantType.OFFER);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setOffers(Collections.emptyList());

        boolean result = predicate.checkType(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkQuery_Success_inDisplayName() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("Plano").build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Plano");

        boolean result = predicate.checkQuery(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkQuery_Fail_inDisplayName() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("London").build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Texas");

        boolean result = predicate.checkQuery(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkQuery_Success_inCategory() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("pizza").build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Texas");
        dtlMerchant.setCategories(Collections.singletonList(new DtlMerchantAttribute("pizza")));

        boolean result = predicate.checkQuery(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkQuery_Fail_inCategory() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().searchQuery("abc").build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDisplayName("Texas");
        dtlMerchant.setCategories(Collections.singletonList(new DtlMerchantAttribute("pizza")));

        boolean result = predicate.checkQuery(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkBudget_Success() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().maxPrice(1).maxPrice(3).build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setBudget(3);

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkBudget_Fail() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().maxPrice(1).maxPrice(3).build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setBudget(6);

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    public void checkAmenities_Empty() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().amenities(Collections.emptyList()).build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    public void checkAmenities_Success() {
        List<DtlMerchantAttribute> amenities = Collections.singletonList(new DtlMerchantAttribute("Free beer"));
        DtlFilterData filterData = ImmutableDtlFilterData.builder().amenities(amenities).selectedAmenities(amenities).build();
        DtlMerchantsPredicate predicate = createWith(filterData);
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
        DtlMerchantsPredicate predicate = createWith(filterData);
        //
        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setAmenities(Collections.singletonList(new DtlMerchantAttribute("Free beverages")));
        //
        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    @Test
    // TODO this test always true. Change it when fix location check;
    public void checkDistance_Success_MaxDistance() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().distanceType(DistanceType.KMS).build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDistanceType(DistanceType.KMS);
        dtlMerchant.setDistance(25d); //

        boolean result = predicate.apply(dtlMerchant);

        assertThat(result).isTrue();
    }

    @Test
    // TODO this test always true. Change it when fix location check;
    public void checkDistance_Success() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().distanceType(DistanceType.KMS).build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDistanceType(DistanceType.KMS);
        dtlMerchant.setDistance(25d); //

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isTrue();
    }

    @Test
    // TODO this test always false. Change it when fix location check;
    public void checkDistance_Fail() {
        DtlFilterData filterData = ImmutableDtlFilterData.builder().distanceType(DistanceType.KMS).build();
        DtlMerchantsPredicate predicate = createWith(filterData);

        DtlMerchant dtlMerchant = createDefaultMerchant();
        dtlMerchant.setDistanceType(DistanceType.KMS);
        dtlMerchant.setDistance(38000d); //

        boolean result = predicate.apply(dtlMerchant);
        assertThat(result).isFalse();
    }

    public static DtlMerchantsPredicate createWith(DtlFilterData filterData) {
        return ImmutableDtlMerchantsPredicate.builder()
                .filterData(filterData)
                .currentLatLng(TestConstants.DEFAULT_LAT_LNG)
                .merchantType(DtlMerchantType.DINING) // default merchant type if no offers
                .build();
    }

    public static DtlMerchantsPredicate createWithType(DtlMerchantType type) {
        return ImmutableDtlMerchantsPredicate.builder()
                .filterData(ImmutableDtlFilterData.builder().build())
                .currentLatLng(TestConstants.DEFAULT_LAT_LNG)
                .merchantType(type)
                .build();
    }

    public static DtlMerchant createDefaultMerchant() {
        DtlMerchant merchant = new DtlMerchant();
        merchant.setBudget(2); // default from 1 tp 5;
        merchant.setDisplayName(TestConstants.DEFAULT_SEARCH_QUERY);
        return merchant;
    }

}
