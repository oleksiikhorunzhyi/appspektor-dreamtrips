package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import org.immutables.builder.Builder;

public class DtlMerchantsPredicate implements Predicate<DtlMerchant> {

    private final DtlMerchantDistancePredicate distancePredicate;

    private final DtlMerchantAmenitiesPredicate amenitiesPredicate;

    private final DtlMerchantPricePredicate pricePredicate;

    private final DtlMerchantQueryPredicate queryPredicate;

    private final DtlMerchantTypePredicate typePredicate;

    private DtlMerchantsPredicate(DtlMerchantType merchantType,
                                    DtlFilterData filterData) {
        this.distancePredicate = ImmutableDtlMerchantDistancePredicate.of(filterData);
        this.amenitiesPredicate = ImmutableDtlMerchantAmenitiesPredicate.of(filterData);
        this.pricePredicate = ImmutableDtlMerchantPricePredicate.of(filterData);
        this.queryPredicate = ImmutableDtlMerchantQueryPredicate.of(filterData);
        this.typePredicate = ImmutableDtlMerchantTypePredicate.of(merchantType);
    }

    @Builder.Factory
    public static DtlMerchantsPredicate dtlMerchantsPredicate(DtlMerchantType merchantType,
                                                              DtlFilterData filterData) {
        return new DtlMerchantsPredicate(merchantType, filterData);
    }

    @Override
    public boolean apply(DtlMerchant merchant) {
        return typePredicate.apply(merchant)
                && pricePredicate.apply(merchant)
                && distancePredicate.apply(merchant)
                && amenitiesPredicate.apply(merchant)
                && queryPredicate.apply(merchant);
    }
}
