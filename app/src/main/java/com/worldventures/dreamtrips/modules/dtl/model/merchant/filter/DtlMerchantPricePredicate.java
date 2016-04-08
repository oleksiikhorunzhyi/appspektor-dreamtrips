package com.worldventures.dreamtrips.modules.dtl.model.merchant.filter;

import com.innahema.collections.query.functions.Predicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

/**
 * Opt out merchants that don't match search filter criteria
 */
public class DtlMerchantPricePredicate implements Predicate<DtlMerchant> {

    private final DtlFilterData filterData;

    public DtlMerchantPricePredicate(DtlFilterData filterData) {
        this.filterData = filterData;
    }

    @Override
    public boolean apply(DtlMerchant dtlMerchant) {
        return dtlMerchant.getBudget() >= filterData.getMinPrice() &&
                dtlMerchant.getBudget() <= filterData.getMaxPrice();
    }
}
